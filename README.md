# KaviMart

KaviMart is a multi-seller marketplace with separate buyer, seller, and admin workflows. It is built as a Java 17-compatible Maven WAR using Servlet 4, JSP/JSTL, JDBC, H2, and plain CSS/JavaScript.

## Technology

| Area | Technology |
| --- | --- |
| Runtime / packaging | Java 17, Maven, WAR |
| Web | Servlet 4.0.1 (javax), JSP, JSTL 1.2 |
| Database | H2 embedded file mode; in-memory test database |
| Connection pooling | HikariCP |
| Password hashing | jBCrypt |
| JSON / logging | Gson, SLF4J, Logback |
| Tests and quality | JUnit 5, Mockito, Checkstyle, SpotBugs |
| Development server | Embedded Tomcat 9 (dev profile only) |

## Run locally

Prerequisites: JDK 17 and Maven 3.8+. The source and bytecode target Java 17.

```sh
cd kavimart
mvn clean verify
mvn -Pdev package exec:java
```

Open http://localhost:8080. The H2 database is created under `data/kavimart`. Configure `JDBC_URL`, `JDBC_USER`, `JDBC_PASSWORD`, and `PORT` if needed, or copy `src/main/resources/config.properties.example` to `src/main/resources/config.properties`.

## Seed account

Admin: `admin@kavimart.com` / `KaviAdmin123!`. The password is BCrypt-hashed during first-time seed initialization. Sample seller and buyer accounts are listed on the login page. Change the seeded password before exposing a real deployment.

## Deploy the WAR

Build with `mvn clean package`, then deploy `target/kavimart.war` to a standalone Apache Tomcat 9 installation. The embedded Tomcat dependencies are only used by the `dev` profile and are not bundled in the WAR.

## Features

- Buyer and seller registration, BCrypt login, session ID rotation, and active-account checks.
- Seller-owned product management; public catalog search, category/price filters, paging, and detail pages.
- Stock-limited cart, transactional checkout, order history, cancellation/restocking, and seller order status progression.
- Admin user activation/ban, order view, and listing moderation.
- Verified-purchase reviews, health and JSON API endpoints.

## Test

`mvn clean verify` runs DAO tests against H2 in-memory, service tests with Mockito, and servlet tests with request/response mocks.
