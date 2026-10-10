# KaviMart — Capstone Project Final Report
**Anna University R2025 Semester 3 | Master of Computer Applications / Computer Science**  
**Project Title:** KaviMart: A Multi-Seller Artisan Marketplace Web Application  
**Author:** Kavidharsini R  
**Date:** October 10, 2026  

---

## 1. Executive Summary

**KaviMart** is a full-stack, multi-seller artisan marketplace developed using standard Java Enterprise technologies: **Java 17**, **Java Servlets 4.0 (JSR 369)**, **Apache Tomcat 9**, **JSP & JSTL 1.2**, **JDBC**, **HikariCP**, and an embedded **H2** relational database.

The platform connects independent craftspeople and small-scale sellers with conscious buyers. It features role-based access control across **Buyer**, **Seller**, and **Admin** personas, an atomic transactional checkout pipeline, verified-purchase product ratings, an interactive AI shopping assistant powered by Google Gemini (with deterministic offline fallback), and versioned RESTful JSON endpoints.

---

## 2. System Architecture

KaviMart adheres to a strict multi-tier, modular enterprise architecture:

```
[ Web Browser / HTTP Client ]
             │
             ▼
[ Filter Chain: UTF-8 Encoding ➔ Request-ID Logging ➔ Role-Based AuthFilter ]
             │
             ▼
[ Front Controller Servlets (Auth, Catalog, Cart, Orders, Reviews, Admin, API) ]
             │
             ▼
[ Business Service Layer (UserService, ProductService, CartService, OrderService, ReviewService, ChatService) ]
             │
             ▼
[ Data Access Object (DAO) Layer (UserDao, ProductDao, CartDao, OrderDao, ReviewDao) ]
             │
             ▼
[ Connection Pool: HikariCP DataSource ]
             │
             ▼
[ Relational Storage: H2 Database Engine (schema.sql / migrations) ]
```

### Key Architectural Layers
1. **Filter Pipeline:**
   - `EncodingFilter`: Enforces UTF-8 character encoding on all requests and responses.
   - `LoggingFilter`: Generates a unique UUID `X-Request-ID` per transaction, populates MDC, and logs elapsed processing duration.
   - `AuthFilter`: Enforces role-based access control (RBAC) across `/buyer/*`, `/seller/*`, `/admin/*`, and `/api/v1/*`.
2. **Controller Layer:** Extends `BaseServlet` to coordinate parameters, validate models, and forward to JSP views or output JSON envelopes.
3. **Service Layer:** Encapsulates business invariants, pricing calculations, status transitions, and AI client communication.
4. **DAO Layer:** Encapsulates all SQL statements using parameterized `PreparedStatement` instances.

---

## 3. Database Design & Entity-Relationship (ER) Model

The database schema (`src/main/resources/db/schema.sql`) maintains relational integrity across 6 core entities:

### Entities & Relationships
- **`users`:** Stores account credentials (`id`, `name`, `email`, `password_hash`, `role`, `is_active`, `created_at`). Supports roles `BUYER`, `SELLER`, `ADMIN`.
- **`products`:** Seller listings (`id`, `seller_id`, `name`, `description`, `price`, `stock_qty`, `category`, `image_url`, `is_active`, `created_at`). Foreign key to `users(id)` with `ON DELETE CASCADE`.
- **`cart_items`:** Ephemeral shopping bag lines (`id`, `user_id`, `product_id`, `quantity`, `created_at`). Enforces uniqueness on `(user_id, product_id)`.
- **`orders`:** Transactional customer orders (`id`, `buyer_id`, `status`, `total_amount`, `created_at`). Valid statuses: `PENDING`, `CONFIRMED`, `SHIPPED`, `DELIVERED`, `CANCELLED`.
- **`order_items`:** Frozen purchase line items (`id`, `order_id`, `product_id`, `quantity`, `unit_price`, `created_at`). Stores captured unit price at time of purchase.
- **`reviews`:** Verified product ratings (`id`, `product_id`, `user_id`, `rating`, `comment`, `created_at`). Enforces rating `CHECK (rating BETWEEN 1 AND 5)` and uniqueness on `(user_id, product_id)`.

### Soft Deletion Strategy
Both `users` and `products` incorporate an `is_active` boolean column. Deactivating a product retains historical order line associations and purchase records without violating foreign key constraints.

---

## 4. Software Design Patterns Implemented

| Design Pattern | Implementation in KaviMart | Concrete Location |
| :--- | :--- | :--- |
| **DAO (Data Access Object)** | Decouples business logic from SQL JDBC queries via interfaces and JDBC implementations. | `CartDao`, `OrderDao`, `ProductDao`, `ReviewDao`, `UserDao` in `dao/` and `dao/jdbc/` |
| **Front Controller** | Dispatches requests to specialized servlet controllers while sharing common error and session handling in `BaseServlet`. | `BaseServlet`, `ProductServlet`, `OrderServlet`, `AdminServlet`, `ApiServlet` |
| **Singleton** | Application DataSource (`HikariDataSource`) and thread pool initialized once during application lifecycle and injected into servlet context. | `AppContextListener.contextInitialized` |
| **Factory Method** | Instantiates runtime strategy/provider based on environment variables and system settings. | `PaymentStrategyFactory`, `ChatProviderFactory` |
| **Strategy** | Pluggable payment processor algorithms and chatbot providers interchangeable at runtime. | `PaymentStrategy` (`MockPaymentStrategy`), `ChatProvider` (`GeminiChatProvider`, `MockChatProvider`) |
| **Builder** | Immutable assembly of multi-attribute DTOs with complex item hierarchies. | `OrderSummaryDTO.Builder` |

---

## 5. Security Architecture

1. **SQL Injection Defense:** Zero use of `Statement` or string concatenation in queries; 100% of database interactions employ `PreparedStatement` with bind variables (`?`). Verified through `SecurityPayloadTest`.
2. **Password Cryptography:** Passwords hashed using standard `jBCrypt` with salt round factor 12. Plain passwords are never stored, logged, or returned in response DTOs.
3. **Session Management:** Session ID rotated on authentication (`req.changeSessionId()`) to eliminate Session Fixation. Explicit 30-minute session timeout and `HttpOnly` cookie flags configured in `web.xml`.
4. **Cross-Site Scripting (XSS):** All dynamic view renders utilize JSTL `<c:out value="..." />` XML-escaping. Automated test `SecurityPayloadTest` verifies that HTML/script injection attempts are neutralized.
5. **Role-Based Access Control:** `AuthFilter` centrally guards protected paths, serving 302 login redirects for browser sessions and 401/403 JSON envelopes for REST API consumers.

---

## 6. AI Assistant Chatbot Architecture

KaviMart includes a hybrid AI assistant designed to handle visitor and shopper inquiries:
- **`GeminiChatProvider`:** Leverages Google's `gemini-1.5-flash` model via native `java.net.http.HttpClient`. API keys remain strictly server-side (`GEMINI_API_KEY`). Implements an 8-second HTTP timeout and domain-constrained system instructions.
- **`MockChatProvider`:** Offline fallback provider answering 13 distinct FAQ categories (cancellations, order tracking, selling rules, payments, bag management, reviews, stock availability).
- **Session Caching & Rate Limiting:** `ChatService` enforces a strict 10 messages/minute quota per session and caches repeated questions in memory to minimize external API costs.

---

## 7. Testing, Verification & Code Quality

- **Automated Tests:** 85 automated test cases executed via JUnit 5 and Mockito across DAOs, services, servlets, filters, DTOs, and security payloads (`mvn -B clean verify`).
- **Static Analysis:**
  - **Checkstyle:** Zero violations against configured standards; all classes and methods in `service/` and `dao/` fully documented with Javadoc.
  - **SpotBugs:** Zero high/major bugs or warnings detected.

---

## 8. Project History & Honest Development Realities

- **Early Phase Commit Density:** Initial weeks had fewer Git commits as extensive architectural planning, schema experimentation, local prototype benchmarking, and UI design iterations were performed locally prior to structured feature branching.
- **Milestone Versioning:** Development transitioned into systematic tagged releases:
  - `v0.1.0`: Core architecture, models, and CRUD foundation.
  - `v1.0.0`: Final review release with the Gemini-ready chatbot, local Tomcat deploy script, rupee seed data, security tests, REST API specification and evaluation documents.

---

## 9. Known Limitations

1. **Embedded Database Mode:** Uses H2 file-backed storage (`jdbc:h2:file:./data/kavimart`). While lightweight and self-contained, horizontal scalability requires external database clustering (PostgreSQL/MySQL).
2. **Ephemeral Cloud Storage:** On free container platforms (e.g. Render free tier), container sleep/restart cycles without persistent block storage reset the H2 file database.
3. **Simulated Payment Gateway:** Checkout uses `MockPaymentStrategy`. Commercial deployment requires merchant onboarding with Razorpay or Stripe.

---

## 10. Conclusion

KaviMart demonstrates a comprehensive, robust, and secure implementation of modern enterprise Java principles. By strictly avoiding heavy frameworks, the project highlights core computer science and software engineering competencies in concurrency, transactional consistency, relational database normalization, architectural patterns, and defensive programming.
