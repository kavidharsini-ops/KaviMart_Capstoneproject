# KaviMart

KaviMart is a multi-seller marketplace with separate buyer, seller, and admin workflows. It is built as a Java 17 Maven WAR using Servlet 4, JSP/JSTL, JDBC, H2, and plain CSS/JavaScript. A built-in helper chatbot answers common questions about orders, the bag, reviews, and selling.

## Features

- **Accounts:** buyer and seller registration, BCrypt login, session ID rotation, and active-account checks.
- **Catalog:** public search, category and price filters, paging, and product detail pages. Prices are shown in rupees.
- **Buyer:** stock-limited cart, transactional checkout, order history, cancellation of pending orders (stock is restored), and verified-purchase reviews.
- **Seller:** dashboard, product listings (add, edit, remove), and order status progression.
- **Admin:** ban or activate member accounts, view all orders, and remove listings.
- **Chatbot:** floating "Ask KaviMart" helper on every page. It uses a mock provider with rate limiting, and the provider can be swapped without changing the servlet.
- **API:** health check and JSON endpoints under `/api/v1/`.
- **Soft delete:** users and products have an `is_active` flag, so old orders stay valid after a listing is removed.

## Technology

| Area | Technology |
| --- | --- |
| Runtime / packaging | Java 17, Maven, WAR |
| Web | Servlet 4.0.1 (javax), JSP, JSTL 1.2 |
| Database | H2 2.2 (embedded file mode; in-memory for tests) |
| Connection pooling | HikariCP |
| Password hashing | jBCrypt |
| JSON / logging | Gson, SLF4J, Logback |
| Tests and quality | JUnit 5, Mockito, Checkstyle, SpotBugs |
| Development server | Embedded Tomcat 9 (dev profile only) |
| Deployment | Apache Tomcat 9; a Dockerfile for Tomcat 9 is included |

## Design

### Architecture

Requests pass through three filters (encoding, logging, authentication and role check), then a servlet controller, a service, and a DAO that talks to the H2 database. The chat servlet uses its own chat service with a pluggable provider.

![Architecture diagram](docs/architecture.png)

### Database (ER diagram)

![ER diagram](docs/er-diagram.png)

The schema is in `src/main/resources/db/schema.sql`. Versioned changes are in `src/main/resources/db/migrations/` (V1 to V4).

### Use cases

![Use case diagram](docs/use-case.png)

## Project structure

```
src/main/java/com/kavi/kavimart/
  controller/   servlets (auth, catalog, cart, orders, reviews, admin, chat, API)
  service/      business rules, plus service/ai for the chatbot
  dao/          JDBC data access
  model/        User, Product, CartItem, Review and query helpers
  dto/          request and response objects
  filter/       Encoding, Logging, Auth filters
  listener/     AppContextListener (starts the database)
  exception/    custom exceptions
  util/         helpers
src/main/resources/db/   schema.sql, seed.sql, migrations/
src/main/webapp/         JSP views (WEB-INF/jsp) and assets (CSS, JS)
```

## Run locally

Prerequisites: JDK 17 and Maven 3.8+.

Recommended: set the seed password before the first start. All demo accounts use it.

```powershell
$env:SEED_PASSWORD = "choose-a-password"
```

If `SEED_PASSWORD` is not set, the accounts are seeded with a local development password and a warning is logged. Seeding happens only when the database is empty, so to change the password later, stop the app and delete the H2 database files (by default `./data/kavimart`, relative to where the app is started). This removes all data.

Build and run with the embedded Tomcat (dev profile):

```sh
mvn clean verify
mvn -Pdev package exec:java
```

Open http://localhost:8080. The H2 database is created under `data/kavimart`.

Optional settings: `JDBC_URL`, `JDBC_USER`, `JDBC_PASSWORD`, and `PORT`. You can also copy `src/main/resources/config.properties.example` to `src/main/resources/config.properties`.

## Deploy on Tomcat 9 (Windows)

The script `deploy-local.ps1` builds the WAR, copies it to Tomcat, starts Tomcat, and checks that the app responds.

1. Open `deploy-local.ps1` and set `$project` (this folder) and `$tomcat` (your Tomcat 9 folder) at the top.
2. Run:

```powershell
powershell -ExecutionPolicy Bypass -File .\deploy-local.ps1
```

3. Open http://localhost:8080/kavimart/

If the script says a port is busy, close the old Tomcat window and run it again. To deploy by hand, run `mvn clean package -DskipTests` and copy `target/kavimart.war` to Tomcat's `webapps` folder.

## Demo accounts

| Role | Email |
| --- | --- |
| Admin | admin@kavimart.com |
| Seller | seller1@kavimart.com, seller2@kavimart.com |
| Buyer | buyer1@kavimart.com, buyer2@kavimart.com |

The password is the value of `SEED_PASSWORD` that was set when the database was first created, or the local development password if it was not set. Passwords are stored as BCrypt hashes. Always set your own `SEED_PASSWORD` before any real deployment.

## Test

`mvn clean verify` runs DAO tests against in-memory H2, service tests with Mockito, and servlet tests with request and response mocks. The chatbot service, mock provider, and servlet have their own tests.
