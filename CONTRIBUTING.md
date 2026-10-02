# Contributing

1. Clone the repository: `git clone <repository-url>`
2. Enter the project: `cd <repository>/kavimart`
3. Confirm JDK 17 and Maven are available: `java -version` and `mvn -version`.
4. Copy `src/main/resources/config.properties.example` to `src/main/resources/config.properties` if you need custom database settings.
5. Run tests and quality checks: `mvn clean verify`.
6. Start the local Tomcat 9 runner: `mvn -Pdev package exec:java`.
7. Open the URL printed by the runner (normally http://localhost:8080).
8. Keep database changes in `src/main/resources/db/schema.sql` and versioned migration files; add tests for behavior changes.
