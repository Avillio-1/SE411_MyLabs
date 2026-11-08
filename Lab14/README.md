# Lab 14: Spring CRUD

An independently written course REST API based on the Lab14 assignment PDF.
The supplied solution archive was not opened or used.

## Setup and run

- Java 17 or newer and Maven 3.6.3 or newer.
- Spring Boot **4.0.5**, Maven, JAR packaging, Java **17** target.
- Group/package: `psu.se411` / `psu.se411.lab14`; artifact: `lab14`.
- Spring Web MVC, Spring Data JDBC, H2, and Bean Validation.

From this directory:

```shell
mvn clean verify
mvn spring-boot:run
```

The API is at `http://localhost:8080/api/v1/courses`. Stop it with Ctrl+C.
To use a different port: `mvn spring-boot:run -Dspring-boot.run.arguments=--server.port=8081`.
After packaging, you can also run `java -jar target/lab14-0.0.1-SNAPSHOT.jar`.

In Eclipse, use **File > Import > Maven > Existing Maven Projects**, select this
directory, and finish. If dependencies are not resolved, right-click the project
and choose **Maven > Update Project**. Run `Lab14Application` as a Java application
or run the Maven goal `clean spring-boot:run`, as described in the assignment.
The project configuration was written directly to match the handout rather than
generated through the Spring Initializr UI.

## API design

| Operation | Method and route | Success | Error |
| --- | --- | --- | --- |
| Create | `POST /api/v1/courses` | 201, created JSON, `Location` header | 400 invalid input; 422 duplicate ID |
| List | `GET /api/v1/courses` | 200, JSON array | |
| Read | `GET /api/v1/courses/{id}` | 200, course JSON | 404 missing course |
| Update | `PUT /api/v1/courses/{id}` | 204, empty body | 404 missing course; 400 invalid input or mismatched ID |
| Delete | `DELETE /api/v1/courses/{id}` | 204, empty body | 404 missing course |

POST and PUT accept JSON, for example:

```json
{
  "id": 14,
  "code": "SE414",
  "name": "Web Services",
  "description": "Designing and implementing REST APIs.",
  "credits": 3
}
```

IDs are supplied by the client and must be nonnegative integers; zero is valid.
POST requires a new ID. PUT replaces the course at the URL and requires a matching
body ID. `description` is optional. List ordering is unspecified.
See [requests.http](requests.http) for a complete manual CRUD sequence and error cases.

## Implementation and validation

- `Course` is the required record with Spring Data's `@Id` annotation.
- `CourseRepository` provides Spring Data JDBC reads and deletion.
- `CourseService` handles existence checks, insert/update decisions, and transactions.
- `CourseController` maps the five endpoints and chooses their HTTP responses.
- `schema.sql` creates `courses`; `data.sql` loads three sample courses at startup.
- The H2 database lives in memory. Restarting the application resets it to the seed data.

Both POST and PUT use `@Valid`. The record uses `@NotNull @PositiveOrZero` for IDs,
`@NotBlank` for codes, `@NotBlank @Size(min = 2)` for names, and
`@NotNull @Min(1) @Max(10)` for credits. Null required values are rejected too.
Spring MVC returns 400 for validation failures and malformed requests, using its
Problem Details support for errors.

Because IDs are assigned by the client, creation explicitly uses
`JdbcAggregateTemplate.insert`. A plain repository `save` would treat a non-null
ID as an existing entity. The database primary key also prevents duplicate rows.
See Spring's [persistence documentation](https://docs.spring.io/spring-data/relational/reference/jdbc/entity-persistence.html).

The handout asks for Copilot-assisted validation. Validation here was implemented
with Codex assistance, not Copilot; no Copilot run is claimed.

## Verification

`mvn clean verify` runs integration tests against an actual embedded HTTP server
and H2 database. Tests cover seeded reads, a full CRUD lifecycle, Location headers,
empty 204 responses, persisted updates/deletes, duplicate IDs (including simultaneous
POST requests), missing resources,
all validation rules (including nulls and valid boundaries), malformed JSON,
mismatched PUT IDs, an empty list, and nonnumeric path IDs. Every test resets its
data; production startup scripts are used to reload the fixtures.

The Java 17 target is configured in the POM; verification may run on a newer JDK.

Verified on 8 November 2026 using JDK 21: **24 tests passed**, with zero failures,
errors, or skipped tests. The packaged JAR also started successfully on port 18014;
an HTTP GET returned 200 and the three courses loaded by the startup SQL scripts.
