package psu.se411.lab14;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.jdbc.Sql;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.json.JsonMapper;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(statements = "DELETE FROM courses")
@Sql(scripts = "/data.sql")
class CourseApiTests {
    @LocalServerPort
    private int port;

    @Autowired
    private JdbcTemplate jdbc;

    private final HttpClient client = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(5)).build();
    private final JsonMapper json = JsonMapper.builder().build();

    @Test
    void readsSeedCoursesAndOneCourse() throws Exception {
        var all = request("GET", "", null);
        assertEquals(200, all.statusCode());
        assertEquals(3, json.readTree(all.body()).size());
        var one = request("GET", "/1", null);
        assertEquals(200, one.statusCode());
        assertEquals("SE411", json.readTree(one.body()).get("code").asString());
    }

    @Test
    void createsReadsUpdatesAndDeletesCourse() throws Exception {
        String original = course("14", "SE414", "Web Services", "3");
        var created = request("POST", "", original);
        assertEquals(201, created.statusCode(), created.body());
        assertEquals("/api/v1/courses/14", created.headers().firstValue("Location").orElseThrow());
        assertEquals(json.readTree(original), json.readTree(created.body()));
        assertEquals(json.readTree(original), json.readTree(request("GET", "/14", null).body()));

        String updated = course("14", "SE415", "Advanced Web Services", "4");
        var update = request("PUT", "/14", updated);
        assertEquals(204, update.statusCode(), update.body());
        assertEquals("", update.body());
        assertEquals(json.readTree(updated), json.readTree(request("GET", "/14", null).body()));
        assertEquals(4, jdbc.queryForObject("SELECT credits FROM courses WHERE id = 14", Integer.class));

        var deleted = request("DELETE", "/14", null);
        assertEquals(204, deleted.statusCode());
        assertEquals("", deleted.body());
        assertEquals(404, request("GET", "/14", null).statusCode());
        assertEquals(404, request("DELETE", "/14", null).statusCode());
        assertEquals(0, jdbc.queryForObject("SELECT COUNT(*) FROM courses WHERE id = 14", Integer.class));
    }

    @Test
    void duplicateIdReturns422AndPreservesOriginal() throws Exception {
        JsonNode before = json.readTree(request("GET", "/1", null).body());
        var duplicate = request("POST", "", course("1", "OTHER", "Replacement", "5"));
        assertEquals(422, duplicate.statusCode(), duplicate.body());
        assertEquals(before, json.readTree(request("GET", "/1", null).body()));
        assertEquals(3, count());
    }

    @Test
    void simultaneousCreatesWithOneIdReturnOneCreatedAndTheRest422() throws Exception {
        var executor = Executors.newFixedThreadPool(8);
        var ready = new CountDownLatch(8);
        var start = new CountDownLatch(1);
        var responses = new ArrayList<Future<Integer>>();
        try {
            for (int i = 0; i < 8; i++) {
                responses.add(executor.submit(() -> {
                    ready.countDown();
                    if (!start.await(10, TimeUnit.SECONDS)) {
                        throw new IllegalStateException("Timed out waiting for concurrent requests");
                    }
                    return request("POST", "", course("14", "SE414", "Web Services", "3")).statusCode();
                }));
            }
            assertTrue(ready.await(10, TimeUnit.SECONDS));
            start.countDown();
            int created = 0;
            for (var response : responses) {
                int status = response.get(15, TimeUnit.SECONDS);
                assertTrue(status == 201 || status == 422, "Unexpected status: " + status);
                if (status == 201) {
                    created++;
                }
            }
            assertEquals(1, created);
            assertEquals(4, count());
        } finally {
            start.countDown();
            executor.shutdownNow();
        }
    }

    @Test
    void missingResourcesReturn404WithoutCreatingRows() throws Exception {
        assertEquals(404, request("GET", "/999", null).statusCode());
        assertEquals(404, request("PUT", "/999", course("999", "X", "Missing", "3")).statusCode());
        assertEquals(404, request("DELETE", "/999", null).statusCode());
        assertEquals(3, count());
    }

    @ParameterizedTest
    @MethodSource("invalidCourses")
    void rejectsInvalidPostWithoutChangingDatabase(String body) throws Exception {
        var result = request("POST", "", body);
        assertEquals(400, result.statusCode(), result.body());
        assertEquals(3, count());
    }

    static Stream<String> invalidCourses() {
        return Stream.of(
                course("-1", "SE414", "Web Services", "3"),
                course("null", "SE414", "Web Services", "3"),
                course("14", "", "Web Services", "3"),
                course("14", "   ", "Web Services", "3"),
                course("14", "SE414", "A", "3"),
                course("14", "SE414", "", "3"),
                course("14", "SE414", "  ", "3"),
                course("14", "SE414", "Web Services", "0"),
                course("14", "SE414", "Web Services", "11"),
                course("14", "SE414", "Web Services", "null"),
                "{\"id\":14,\"name\":\"Web Services\",\"credits\":3}",
                "{\"id\":14,\"code\":\"SE414\",\"credits\":3}",
                "{}", "null", "{broken json}");
    }

    @Test
    void acceptsZeroIdTwoCharacterNameAndCreditBoundaries() throws Exception {
        assertEquals(201, request("POST", "", course("0", "X", "AB", "1")).statusCode());
        assertEquals(201, request("POST", "", course("14", "Y", "CD", "10")).statusCode());
        assertEquals(200, request("GET", "/0", null).statusCode());
    }

    @Test
    void rejectsMismatchedOrInvalidUpdatesAndPreservesOriginal() throws Exception {
        JsonNode before = json.readTree(request("GET", "/1", null).body());
        assertEquals(400, request("PUT", "/1", course("2", "X", "Changed", "3")).statusCode());
        assertEquals(400, request("PUT", "/1", course("1", "X", "Changed", "11")).statusCode());
        assertEquals(before, json.readTree(request("GET", "/1", null).body()));
    }

    @Test
    void returnsEmptyArrayAfterDeletingAllCourses() throws Exception {
        for (long id = 1; id <= 3; id++) {
            assertEquals(204, request("DELETE", "/" + id, null).statusCode());
        }
        var response = request("GET", "", null);
        assertEquals(200, response.statusCode());
        assertEquals("[]", response.body());
    }

    @Test
    void rejectsNonNumericPathId() throws Exception {
        assertEquals(400, request("GET", "/abc", null).statusCode());
    }

    private int count() {
        return jdbc.queryForObject("SELECT COUNT(*) FROM courses", Integer.class);
    }

    private HttpResponse<String> request(String method, String suffix, String body) throws Exception {
        var builder = HttpRequest.newBuilder(URI.create("http://localhost:" + port + "/api/v1/courses" + suffix))
                .timeout(Duration.ofSeconds(10));
        if (body != null) {
            builder.header("Content-Type", "application/json");
        }
        return client.send(builder.method(method, body == null
                ? HttpRequest.BodyPublishers.noBody() : HttpRequest.BodyPublishers.ofString(body)).build(),
                HttpResponse.BodyHandlers.ofString());
    }

    private static String course(String id, String code, String name, String credits) {
        return """
                {"id":%s,"code":"%s","name":"%s","description":"Lab test course","credits":%s}
                """.formatted(id, code, name, credits);
    }
}
