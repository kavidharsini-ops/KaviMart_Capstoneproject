# KaviMart Security Checklist & Verification Evidence

This document provides a line-by-line verification and evidence log for all security controls enforced in the **KaviMart** application.

---

## 1. SQL Injection Prevention & Database Hygiene

| Requirement | Status | Concrete Evidence |
| :--- | :--- | :--- |
| **All queries use `PreparedStatement`** | **VERIFIED** | Every DAO implementation uses `connection.prepareStatement(...)` with bind variables (`?`). Verified via codebase grep (`git grep prepareStatement src/main/java`), resulting in 37 prepared statement instances across `JdbcCartDao`, `JdbcDatabaseDao`, `JdbcOrderDao`, `JdbcProductDao`, `JdbcReviewDao`, and `JdbcUserDao`. No raw string concatenation is used in queries. |
| **No `DriverManager` outside listener** | **VERIFIED** | Verified with zero grep matches across `src/`. Database connection pooling is managed centrally via `HikariDataSource` configured in `AppContextListener.java` (lines 87–95). In unit tests, `JdbcDataSource` is initialized via `TestDatabase.java`. |
| **`try-with-resources` everywhere** | **VERIFIED** | Every `Connection`, `PreparedStatement`, `ResultSet`, and `InputStream` is managed inside a `try (...)` block, guaranteeing clean disposal and preventing connection/resource leaks. |

### Automated Verification Test
- `com.kavi.kavimart.security.SecurityPayloadTest#sqlInjectionInLoginEmailIsSafelyTreatedAsLiteral`
- `com.kavi.kavimart.security.SecurityPayloadTest#sqlInjectionInCatalogSearchIsSafelyHandledByPreparedStatement`
Both tests confirm that payloads such as `' OR '1'='1' --` and `'; DROP TABLE products; --` are treated strictly as string literals by `PreparedStatement` and cause no unauthorized data access or table modification.

---

## 2. Authentication & Credential Protection

| Requirement | Status | Concrete Evidence |
| :--- | :--- | :--- |
| **Strong Password Hashing (BCrypt)** | **VERIFIED** | Passwords are hashed using jBCrypt (`org.mindrot.jbcrypt.BCrypt`) with salt cost factor 12 in `PasswordUtil.java`: <br>`BCrypt.hashpw(password, BCrypt.gensalt(12))`. |
| **No Passwords in Application Logs** | **VERIFIED** | `LoggingFilter.java` logs only the HTTP method, URI, and request duration in milliseconds (`LOG.info("{} {} completed in {}ms", method, uri, duration);`). Request bodies and passwords are never logged or stored in MDC context. |
| **Session ID Regenerated on Login** | **VERIFIED** | `AuthServlet.java` explicitly calls `req.changeSessionId()` on successful login (line 60) immediately before binding `SessionKeys.USER` to prevent Session Fixation attacks. |
| **Explicit Session Timeout** | **VERIFIED** | Configured as 30 minutes in two independent locations: <br>1. `src/main/webapp/WEB-INF/web.xml`: `<session-timeout>30</session-timeout>`<br>2. `AppContextListener.java`: `context.setSessionTimeout(30);`. |
| **HttpOnly Session Cookie** | **VERIFIED** | Declared in `src/main/webapp/WEB-INF/web.xml`: <br>`<cookie-config><http-only>true</http-only></cookie-config>`, protecting `JSESSIONID` from client-side script theft. |

---

## 3. Cross-Site Scripting (XSS) Prevention & Output Encoding

| Requirement | Status | Concrete Evidence |
| :--- | :--- | :--- |
| **All JSP output escaped** | **VERIFIED** | All dynamic user and database values are wrapped with JSTL `<c:out value="..." />` or formatted via `<fmt:formatNumber ... />`. No unescaped `${...}` expressions are rendered into raw HTML. |
| **XSS Payload Handling** | **VERIFIED** | Verified with automated tests `xssPayloadInProductNameStoredAsSafeLiteralString` and `xssPayloadInReviewCommentStoredSafelyWithoutExecution` in `SecurityPayloadTest.java`. Input like `<script>alert('XSS')</script>` is safely stored as literal text in H2 CLOB/VARCHAR and escaped upon JSP rendering. |

---

## 4. Access Control & Authorization (RBAC)

| Requirement | Status | Concrete Evidence |
| :--- | :--- | :--- |
| **Centralized Route Protection** | **VERIFIED** | `AuthFilter.java` intercepts all incoming requests (`/*`): <br>- Validates authentication state (`SessionKeys.USER`).<br>- Enforces role checks (`ADMIN` for `/admin/*`, `SELLER` for `/seller/*` and `/api/v1/seller/*`, `BUYER` for `/buyer/*`, `/api/v1/cart/*`, `/api/v1/orders/*`). |
| **Unauthenticated Redirects & API Envelopes** | **VERIFIED** | - Unauthenticated web page requests are 302-redirected to `/auth/login?next=...`.<br>- Unauthenticated API requests receive HTTP 401 JSON envelope `{ "success": false, "error": { "code": "UNAUTHORIZED", "message": "Please sign in to continue." } }`. |
| **Role Violation Handling** | **VERIFIED** | - Web role mismatches render the 403 Forbidden error page.<br>- API role mismatches return HTTP 403 JSON envelope with code `FORBIDDEN`. |
| **No Stack Traces on Error** | **VERIFIED** | Custom error pages mapped in `web.xml` for 403, 404, 500, and `java.lang.Throwable` to `/WEB-INF/views/error/*.jsp` and `BaseServlet.error(...)` forward to `/WEB-INF/jsp/error.jsp`. No stack traces or internal server details are exposed to users. |

### Automated Verification Test
- `com.kavi.kavimart.filter.AuthFilterTest` (7 test scenarios covering public paths, 302 login redirects, 401 API denials, and 403 role enforcement for buyers, sellers, and admins).

---

## 5. Security Test Summary

```
[INFO] Running com.kavi.kavimart.filter.AuthFilterTest
[INFO] Tests run: 7, Failures: 0, Errors: 0, Skipped: 0
[INFO] Running com.kavi.kavimart.security.SecurityPayloadTest
[INFO] Tests run: 4, Failures: 0, Errors: 0, Skipped: 0
```
All automated security tests are executed during the Maven verify lifecycle (`mvn -B clean verify`) and pass with 100% success.
