# KaviMart Manual End-to-End Test Sheet

This document contains the step-by-step manual test cases covering the entire end-to-end user journey:  
**Registration &rarr; Browse & Filter &rarr; Add to Cart &rarr; Checkout &rarr; Order History & Status &rarr; Product Review**.

Fill in the **Status (Pass/Fail)** and **Notes / Observation** columns during manual verification.

---

## Test Suite: Core Marketplace Lifecycle

| Test ID | Scenario / Feature | Step-by-Step Actions | Expected Result | Status (Pass/Fail) | Notes / Observation |
| :--- | :--- | :--- | :--- | :--- | :--- |
| **TC-01** | **User Registration (Buyer)** | 1. Navigate to `/auth/register`<br>2. Fill in Name, unique Email, password (min 8 chars), and Role = `Buyer`<br>3. Submit form | Account created successfully; redirected to `/catalog` or login page. Session established. | | |
| **TC-02** | **User Login & Session** | 1. Navigate to `/auth/login`<br>2. Enter registered credentials<br>3. Submit form | Authentication succeeds; session ID rotated; user redirected to role homepage (`/catalog`). | | |
| **TC-03** | **Catalog Browsing & Search** | 1. Navigate to `/catalog`<br>2. Type "Tote" in search box and click search<br>3. Filter by category "Accessories" | Catalog filters items matching keyword and category without page reload error. | | |
| **TC-04** | **Product Detail View** | 1. Click on a product card from `/catalog`<br>2. Observe product details | Product title, price (₹), stock quantity, seller name, description, and star rating display cleanly. | | |
| **TC-05** | **Add to Bag (Cart)** | 1. On product detail or catalog page, select quantity 2<br>2. Click "Add to bag"<br>3. Open bag via navigation link `/buyer/cart` | Cart reflects item added with quantity 2 and computed line total. Stock limit enforced. | | |
| **TC-06** | **Cart Quantity Adjustment** | 1. On `/buyer/cart`, increment quantity<br>2. Verify updated total<br>3. Try setting quantity above stock | Quantity updates running subtotal; quantity above stock displays friendly conflict message. | | |
| **TC-07** | **Mock Checkout Flow** | 1. On `/buyer/cart`, click checkout button<br>2. Confirm checkout dialog/action | Atomic transaction executes: stock decremented, cart cleared, order status set to `PENDING`. Redirected to `/buyer/orders?placed=1`. | | |
| **TC-08** | **Order History Verification** | 1. Navigate to `/buyer/orders`<br>2. Inspect newly placed order | Order ID, items, timestamp, and status `PENDING` appear in buyer's order history. | | |
| **TC-09** | **Seller Order Management** | 1. Sign in as seller account (`seller1@kavimart.com`)<br>2. Open `/seller/orders`<br>3. Advance status: `PENDING` &rarr; `CONFIRMED` &rarr; `SHIPPED` &rarr; `DELIVERED` | Status updates step-by-step; invalid skipped transitions are prevented. | | |
| **TC-10** | **Verified Purchase Review** | 1. Sign in as buyer<br>2. Navigate to product detail page of the delivered item<br>3. Fill 5-star rating and comment<br>4. Submit review | Review is persisted; average rating recalculated; unverified buyers cannot submit. | | |
| **TC-11** | **Order Cancellation** | 1. Place a new order as buyer<br>2. In `/buyer/orders`, click "Cancel" while status is `PENDING` | Order moves to `CANCELLED`; inventory stock is restored to products table. | | |
| **TC-12** | **AI Assistant (Chatbot)** | 1. Open chat widget on any page<br>2. Ask "Where is my order?" and "How do I sell products?" | Chatbot replies with relevant KaviMart assistance within rate limit (max 10 msgs/min). | | |

---

## Tester Sign-off

- **Tester Name:** ___________________________
- **Date Tested:** ___________________________
- **Overall Result:** [ ] ALL PASS &nbsp;&nbsp;&nbsp;&nbsp; [ ] ISSUES FOUND
