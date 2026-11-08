# Lab 13: Spring Introduction

A Maven / Java 17+ / Spring Boot 4.1.2 application generated with Spring
Initializr. It exposes a JSON product API backed by an in-memory H2 database.

## Run

From this directory, with Java 17 or later installed:

```powershell
.\mvnw.cmd clean spring-boot:run
```

On macOS/Linux use `./mvnw clean spring-boot:run`. If Maven is already
installed, `mvn clean spring-boot:run` also works.

Open <http://localhost:8080/api/v1/products>. Stop the application with Ctrl+C.
H2 is in memory: every new application start recreates the table and reloads
the 17 products from the handout (IDs 10 through 26).

For Eclipse: **File > Import > Maven > Existing Maven Projects**, select this
directory, and finish. Right-click `pom.xml`, select **Run As > Maven build**,
and enter `clean spring-boot:run`. Use **Maven > Update Project** if imports
are unresolved. The application entry point is `psu.lab13.Lab13Application`.

## Completed requirements

| Handout requirement | Implementation |
| --- | --- |
| Maven, Spring Boot 4.1.2, Java 17+, JAR, Spring Web | `pom.xml`, Initializr-generated Maven wrapper |
| Spring Data JDBC and H2 | JDBC starter and H2 runtime dependency |
| Product record with Spring Data `@Id` | `model/Product.java` |
| Product table and all supplied sample rows | `src/main/resources/schema.sql` and `data.sql` |
| CRUD plus paging/sorting repository | `repositories/ProductRepository.java` extends both required interfaces |
| Service and constructor injection | `services/ProductService.java` and `controllers/ProductController.java` |
| List and find-by-ID endpoints | `GET /api/v1/products` and `GET /api/v1/products/{id}` |
| Exact money representation | `BigDecimal` in Java and `DECIMAL(10,2)` in SQL |
| AI-assisted input validation | Validation starter and `@Positive` on the ID path parameter |
| Test API responses as JSON | Integration tests and `Lab13.postman_collection.json` |

The controller is named `ProductController`, as shown in the handout's code
example (the instruction that calls it `ProductService` is a naming typo).
The repository supports CRUD and pagination; the lab requires only the two
GET routes to be exposed over HTTP. Products are listed in ascending ID order.
Generated database IDs start at 27 to avoid collisions with the supplied IDs.

## API and validation answer

| Request | Expected result |
| --- | --- |
| `GET /api/v1/products` | 200, JSON array of 17 products |
| `GET /api/v1/products/10` | 200, first product |
| `GET /api/v1/products/999` | 404, positive ID that does not exist |
| `GET /api/v1/products/-1` | **400 Bad Request**, negative ID rejected |
| `GET /api/v1/products/0` | 400, ID must be strictly positive |
| `GET /api/v1/products/abc` | 400, ID must be an integer |
| `GET /api/v1/products/2147483648` | 400, ID exceeds the Java Integer range |

**Answer to step 14: sending a negative ID returns HTTP 400 Bad Request.**

AI recommendation applied: add `spring-boot-starter-validation` and annotate
the `@PathVariable` with `jakarta.validation.constraints.Positive`. Spring MVC
performs method validation before executing the controller. There is no
class-level `@Validated` annotation, following the framework's
[built-in MVC method validation guidance](https://docs.spring.io/spring-framework/reference/web/webmvc/mvc-controller/ann-validation.html).
The ID is the input for the assigned GET endpoint; there is no product-body
input endpoint in this lab. JSON problem details are enabled for error responses.

Example successful response for ID 10:

```json
{
  "id": 10,
  "name": "3d printer",
  "description": "A beginner friendly 3d printer",
  "price": 1840.25
}
```

`BigDecimal("0.10").add(new BigDecimal("0.20"))` produces exactly `0.30`.
The integration test also saves and reads this value through H2 to verify
that the SQL column preserves the cents.

## Verify

```powershell
.\mvnw.cmd clean verify
```

Or use `mvn clean verify`. Tests cover application startup, the entire seeded
ID set, product JSON fields, missing IDs, negative and zero IDs, malformed
and overflowing integers, repository paging/sorting, and decimal persistence.
Database changes made by the money test are rolled back.

Verification completed on 8 November 2026:

- `mvn clean verify`: **BUILD SUCCESS**, 12 tests, 0 failures, 0 errors,
  0 skipped (Java 25, compiled for Java 17).
- Packaged JAR started successfully on Java 21.
- Nine live HTTP checks passed against the packaged JAR on loopback port
  18013: list, IDs 10 and 26, unknown ID 999, and invalid IDs -1, 0, `abc`,
  `1.5`, and `2147483648`. The default application port remains 8080.
- The negative-ID request returned a JSON problem response with status 400.

For manual HTTP checks while the server runs:

```powershell
curl.exe -i http://localhost:8080/api/v1/products
curl.exe -i http://localhost:8080/api/v1/products/10
curl.exe -i http://localhost:8080/api/v1/products/-1
curl.exe -i http://localhost:8080/api/v1/products/999
```

Import `Lab13.postman_collection.json` into Postman or its VS Code extension.
It includes assertions for list, lookup, missing-product, and invalid-ID
requests. Its `baseUrl` variable defaults to `http://localhost:8080`.

## Build a standalone JAR

```powershell
.\mvnw.cmd clean package
java -jar target/lab13-0.0.1-SNAPSHOT.jar
```

No external database or credentials are needed. Generated build output stays
under the ignored `target/` directory.
