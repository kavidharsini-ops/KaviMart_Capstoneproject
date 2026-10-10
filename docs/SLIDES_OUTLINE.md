# KaviMart — Capstone Presentation Slides Outline
**Anna University R2025 MCA/CS Capstone Review**  
**Project:** KaviMart — Multi-Seller Marketplace Web Application  

---

### Slide 1: Title & Project Overview
- **Title:** KaviMart — A Multi-Seller Artisan Marketplace Web Application
- **Student Name:** Kavidharsini R (Register Number)
- **Institution:** Anna University, Chennai
- **Domain:** Web Technologies / Cloud Application Engineering
- **Core Vision:** An authentic, multi-vendor marketplace connecting independent craftspeople with conscious buyers, built on core Java enterprise specifications.

---

### Slide 2: Problem Statement & Motivation
- **Context:** Large e-commerce monopolies overwhelm local artisans and small-scale sellers with high barriers, complex tooling, and opaque policies.
- **Problem:** Need for an accessible, community-driven marketplace with distinct workflows for Buyers, Sellers, and Admins.
- **Technical Challenge:** Building an enterprise-grade multi-tier web application using pure Java Servlets, JSP, JDBC, and SQL transactions without relying on heavy frameworks like Spring Boot.

---

### Slide 3: Technology Stack & Architectural Decision
- **Platform & Packaging:** Java 17 LTS, Maven, Standard WAR distribution.
- **Web & Presentation Tier:** Servlet 4.0.1, JSP, JSTL 1.2, Vanilla Modern CSS/JavaScript.
- **Database & Data Tier:** H2 Relational Database Engine (embedded file mode & in-memory for testing), HikariCP Connection Pool.
- **Security & Utilities:** jBCrypt (salted password hashing), Gson (JSON serialization), SLF4J / Logback (request correlation logging).
- **AI Integration:** Native Java HTTP Client communicating with Google Gemini API with fallback mechanisms.

---

### Slide 4: System Architecture & Request Lifecycle (D1)
- **Three-Stage Filter Chain:**
  1. `EncodingFilter`: Universal UTF-8 character encoding.
  2. `LoggingFilter`: Per-request MDC tracking with `X-Request-ID` and timing latency logging.
  3. `AuthFilter`: Centralized RBAC route guarding.
- **Layer Separation:** Web Controllers (`BaseServlet` subclasses) &rarr; Business Services &rarr; Data Access Objects (DAOs) &rarr; Database.
- Reference to Architecture Diagram (`docs/architecture.png`).

---

### Slide 5: Database Design & Soft-Delete Invariant (D2)
- **Relational Schema:** 6 normalized tables (`users`, `products`, `cart_items`, `orders`, `order_items`, `reviews`).
- **Referential Integrity:** Primary keys, foreign key constraints, unique constraints (`uq_cart_user_product`, `uq_reviews_user_product`).
- **Soft Deletion (`is_active`):** Preserves historical orders and accounting integrity when products or accounts are removed or banned.
- Reference to Entity-Relationship Diagram (`docs/er-diagram.png`).

---

### Slide 6: Place-Order Transaction Workflow (D3)
- **Transaction Atomicity:** Managed in `JdbcOrderDao.placeOrder` using `connection.setAutoCommit(false)`.
- **Step-by-Step Flow:**
  1. Buyer initiates checkout from `/buyer/cart`.
  2. Inventory stock rows locked via `SELECT ... FOR UPDATE`.
  3. Stock sufficiency verified; orders table row created.
  4. Line items created with frozen unit prices; product stock decremented.
  5. Buyer cart cleared; transaction committed atomically (`c.commit()`).
  6. Automatic rollback on stock contention or transient failure.
- Reference to Sequence Diagram (`docs/sequence-diagram.png`).

---

### Slide 7: Software Design Patterns Implemented
- **DAO Pattern:** Clear abstraction between business rules and database queries (`UserDao`, `ProductDao`, `OrderDao`).
- **Front Controller Pattern:** Coordinated request handling through `BaseServlet` hierarchy.
- **Singleton Pattern:** Centrally managed `HikariDataSource` pool initialized in `AppContextListener`.
- **Factory Method Pattern:** `PaymentStrategyFactory` and `ChatProviderFactory` for runtime dependency wiring.
- **Strategy Pattern:** Interchangeable payment engines (`MockPaymentStrategy`) and chatbot providers.
- **Builder Pattern:** Fluent, immutable construction of `OrderSummaryDTO`.

---

### Slide 8: Security & Defensive Programming
- **SQL Injection Prevention:** 100% PreparedStatement usage with parameterized queries across all DAOs.
- **Credential Protection:** BCrypt hashing (12 salt rounds), zero passwords stored in plain text or written to logs.
- **Session Hardening:** Session fixation prevention via `req.changeSessionId()` on authentication, explicit 30-minute timeout, and `HttpOnly` cookie flags.
- **XSS Neutralization:** Universal output escaping via JSTL `<c:out>` and `<fmt:formatNumber>`.
- **Error Obfuscation:** Custom 403, 404, 500 error views without revealing stack traces.

---

### Slide 9: AI Shopping Assistant Integration
- **Hybrid AI Pipeline:** Google Gemini 1.5 Flash (`GeminiChatProvider`) with deterministic offline FAQ provider (`MockChatProvider`).
- **Defensive Safeguards:**
  - 8-second HTTP timeout to prevent request hanging.
  - Server-side domain prompt confining responses to KaviMart catalog, orders, and policies.
  - 10 messages/minute session rate limiter to prevent abuse.
  - In-memory duplicate query cache per session.

---

### Slide 10: Testing, Quality Metrics & Conclusion
- **Quality Assurance:**
  - **85 Automated Tests:** 100% green build via `mvn -B clean verify`.
  - **Checkstyle:** Zero violations against corporate coding standards.
  - **SpotBugs:** Zero defects or vulnerability warnings.
- **Honest Limitations:** Embedded H2 mode (ideal for single-node demo; cluster requires Postgres); mock payment gateway.
- **Future Enhancements:** Live UPI/Card gateway integration (Razorpay), CSRF token defense, multi-region database migration.
- **Closing & Q&A.**
