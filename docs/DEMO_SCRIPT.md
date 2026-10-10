# KaviMart — 5-Minute Final Review Demonstration Script

This script provides an exact, rehearsed, minute-by-minute walkthrough for the capstone viva demonstration.  
**Total Target Time:** 5 Minutes.  

> **Note on Credentials:** Use the seed accounts seeded into the system. Enter your configured `SEED_PASSWORD` (set prior to app launch).

---

## Pre-Demo Checklist (Do 2 minutes before the review)
1. Ensure Tomcat or local runner is active on `http://localhost:8080/kavimart/` (or `http://localhost:8080/`).
2. Open three separate browser windows or profiles (Window 1: Guest/Buyer, Window 2: Seller, Window 3: Admin).

---

## Walkthrough Breakdown

### Minute 0:00 – 1:00 | Introduction & Public Catalog Browsing (Guest)
1. **Show:** Open home page (`/`).
   - *Say:* "Respected examiners, KaviMart is a multi-seller artisan marketplace built using Java 17, Servlet 4, JSP/JSTL, and HikariCP connection pooling over an H2 relational database."
2. **Action:** Click **"Shop"** in the navigation header (`/catalog`).
3. **Action:** Search for **"Tote"** in the search bar &rarr; click **Find something**.
   - *Highlight:* The search executes via `JdbcProductDao` using parameterized `PreparedStatement` with pagination.
4. **Action:** Click category filter **"Accessories"** &rarr; change sort to **"Price: low to high"**.
5. **Action:** Click on the **"Handwoven Indigo Tote"** product card to view the product detail page.
   - *Highlight:* Live stock count, seller attribution, description, and verified buyer reviews.

---

### Minute 1:00 – 2:15 | Buyer Authentication, Shopping Bag & Atomic Checkout
1. **Action:** Click **"Sign in"** in the top navigation (`/auth/login`).
2. **Action:** Sign in as buyer:
   - **Email:** `buyer1@kavimart.com`
   - *Highlight:* Mention that passwords are hashed using **BCrypt (12 salt rounds)**, and upon login, **session ID rotation** prevents Session Fixation.
3. **Action:** Navigate back to product page &rarr; select quantity `2` &rarr; click **"Add to bag"**.
4. **Action:** Click **"Bag"** in the navigation header (`/buyer/cart`).
   - *Highlight:* Running subtotal calculation and client-side and server-side stock boundary checks.
5. **Action:** Click **"Check out"** button.
   - *Highlight:* "Our `OrderDao.placeOrder` executes an **atomic transaction**: it locks the stock row, checks inventory, deducts quantity, captures the unit price, creates the order, and clears the cart all within a single database transaction."
6. **Action:** Arrive at `/buyer/orders?placed=1` &rarr; show new order in **PENDING** state.

---

### Minute 2:15 – 3:15 | Seller Dashboard & Order Lifecycle Progression
1. **Action:** In Window 2, sign in as seller:
   - **Email:** `seller1@kavimart.com`
   - *Say:* "Notice the system automatically routes the user to `/seller/dashboard` based on role-based access control."
2. **Action:** Highlight Seller Dashboard metrics:
   - Total Orders count and aggregate non-cancelled Revenue calculated via `JdbcOrderDao.sellerStats`.
3. **Action:** Click **"Orders"** (`/seller/orders`).
4. **Action:** Locate the buyer's newly placed order (`PENDING`).
5. **Action:** Click status advance button to move:
   - `PENDING` &rarr; `CONFIRMED`
   - `CONFIRMED` &rarr; `SHIPPED`
   - `SHIPPED` &rarr; `DELIVERED`
   - *Highlight:* The state machine prevents skipping states (e.g. `PENDING` directly to `DELIVERED` is strictly rejected).

---

### Minute 3:15 – 4:00 | Verified Purchase Review & AI Assistant Chatbot
1. **Action:** Switch back to Window 1 (Buyer `buyer1@kavimart.com`).
2. **Action:** Refresh the product detail page (`/products/101`).
3. **Action:** Scroll down to the **"Notes from buyers"** review form:
   - Select 5 stars: `★★★★★ · Loved it`.
   - Enter comment: `"Exceptional craft quality! Fast delivery."`
   - Click **"Share your review"**.
   - *Highlight:* `ReviewService` checks that only buyers with a confirmed `DELIVERED` purchase can submit reviews; all output is escaped with JSTL `<c:out>` to prevent XSS.
4. **Action:** Click floating **"Ask KaviMart"** chatbot icon at bottom-right of the screen.
5. **Action:** Type: `"Where is my order?"` &rarr; receive instant status workflow guidance.
6. **Action:** Type: `"How do I sell products?"` &rarr; receive seller onboarding instructions.
   - *Highlight:* "The chatbot uses a pluggable `ChatProvider` architecture. It supports Google Gemini API with server-side keys and gracefully falls back to an offline FAQ provider, protected by session rate limiting."

---

### Minute 4:00 – 4:45 | Admin Moderation Console
1. **Action:** In Window 3, sign in as admin:
   - **Email:** `admin@kavimart.com`
   - System redirects to `/admin`.
2. **Action:** Show the Admin Console:
   - **Accounts Table:** View all registered users; demonstrate the soft-deactivate/ban toggle (`is_active = FALSE`).
   - **Products Table:** Demonstrate administrative moderation (soft delete) keeping historical order integrity intact.
   - **System Orders:** Complete visibility across all marketplace transactions.

---

### Minute 4:45 – 5:00 | Architecture & Test Suite Summary
1. **Action:** Show terminal / IDE terminal.
2. **Action:** Point to test results:
   - "Our project contains **85 automated tests** across DAOs, Services, Controllers, and Security Payloads."
   - "Every query uses `PreparedStatement`. Checkstyle and SpotBugs run with zero major violations."
3. **Conclusion:** "Thank you! I am ready for questions."
