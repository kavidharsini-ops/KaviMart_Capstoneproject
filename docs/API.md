# KaviMart REST API Specification (`/api/v1`)

KaviMart provides versioned RESTful JSON endpoints under the base path `/api/v1/*`.

All responses follow a uniform JSON response envelope: `ApiEnvelope<T>`.

---

## 1. Response Envelope Format

### Success Response Envelope
```json
{
  "success": true,
  "data": { ... },
  "error": null
}
```

### Error Response Envelope
```json
{
  "success": false,
  "data": null,
  "error": {
    "code": "ERROR_CODE_STRING",
    "message": "User-friendly explanation of the issue.",
    "fields": {
      "fieldName": "Specific error description for this field"
    }
  }
}
```

### Common HTTP Status Codes
- `200 OK`: Request succeeded.
- `201 Created`: Resource successfully created (e.g., placing an order, submitting a review).
- `400 Bad Request`: Validation failure or malformed JSON payload.
- `401 Unauthorized`: Authentication required; valid user session absent.
- `403 Forbidden`: Authenticated user does not possess the required role (e.g. Buyer accessing Seller endpoints).
- `404 Not Found`: Target resource does not exist.
- `409 Conflict`: Business invariant violation (e.g. insufficient inventory, unverified purchase review, invalid order state transition).
- `429 Too Many Requests`: Rate limit exceeded (e.g., >10 chatbot requests per minute).
- `500 Internal Server Error`: Unexpected server exception.

---

## 2. API Endpoints Reference

### 2.1 System & Health

#### `GET /api/v1/health`
- **Description:** Verifies service uptime and database connection pool availability.
- **Access:** Public
- **Response `200 OK`:**
  ```json
  {
    "success": true,
    "data": {
      "status": "UP",
      "db": "UP"
    },
    "error": null
  }
  ```

---

### 2.2 Product Catalog

#### `GET /api/v1/products`
- **Description:** Searches and paginates active marketplace listings.
- **Access:** Public
- **Query Parameters:**
  - `q` (string, optional): Search keyword.
  - `category` (string, optional): Category filter (`Apparel`, `Accessories`, `Home`, `Beauty`).
  - `sort` (string, optional): `newest`, `price_asc`, or `price_desc`.
  - `page` (int, default: 1): Page number.
  - `pageSize` (int, default: 12): Items per page (max 48).
- **Response `200 OK`:**
  ```json
  {
    "success": true,
    "data": {
      "items": [
        {
          "id": 101,
          "sellerId": 2,
          "sellerName": "Aarav Sharma",
          "name": "Handwoven Indigo Tote",
          "description": "Handloom cotton tote bag with natural indigo dye.",
          "price": 899.00,
          "stockQty": 20,
          "category": "Accessories",
          "imageUrl": "https://images.unsplash.com/...",
          "createdAt": "2026-10-09T10:00:00"
        }
      ],
      "total": 1,
      "page": 1,
      "pageSize": 12,
      "pages": 1
    },
    "error": null
  }
  ```

#### `GET /api/v1/products/{id}`
- **Description:** Retrieves complete details for an individual active product.
- **Access:** Public
- **Response `200 OK`:** Product object in `data`.
- **Response `404 Not Found`:** If product does not exist or was soft-deleted.

---

### 2.3 Shopping Bag (Cart)

#### `GET /api/v1/cart`
- **Description:** Retrieves all cart lines and the subtotal for the logged-in buyer.
- **Access:** Authenticated (`BUYER`)
- **Response `200 OK`:**
  ```json
  {
    "success": true,
    "data": {
      "items": [
        {
          "id": 1,
          "userId": 4,
          "product": { "id": 101, "name": "Handwoven Indigo Tote", "price": 899.00 },
          "quantity": 2,
          "lineTotal": 1798.00
        }
      ],
      "total": 1798.00
    },
    "error": null
  }
  ```

#### `POST /api/v1/cart`
- **Description:** Adds an item to the shopping bag or increments quantity.
- **Access:** Authenticated (`BUYER`)
- **Headers:** `Content-Type: application/json`
- **Request Body:**
  ```json
  {
    "productId": 101,
    "quantity": 2
  }
  ```
- **Response `200 OK`:** Updated cart items and subtotal.
- **Response `409 Conflict`:** If requested quantity exceeds seller's available stock.

#### `PUT /api/v1/cart/{productId}` (or `PUT /api/v1/cart`)
- **Description:** Updates the absolute quantity of an item in the bag.
- **Access:** Authenticated (`BUYER`)
- **Request Body:**
  ```json
  {
    "productId": 101,
    "quantity": 3
  }
  ```
- **Response `200 OK`:** Updated cart state.

#### `DELETE /api/v1/cart/{productId}`
- **Description:** Removes an item from the cart.
- **Access:** Authenticated (`BUYER`)
- **Response `200 OK`:** Updated cart state after item removal.

---

### 2.4 Orders & Checkout

#### `GET /api/v1/orders`
- **Description:** Lists order history for the logged-in buyer.
- **Access:** Authenticated (`BUYER`)
- **Response `200 OK`:** Array of `OrderSummaryDTO` objects in `data`.

#### `POST /api/v1/orders`
- **Description:** Executes atomic checkout of the buyer's current cart. Checks stock, charges mock payment, creates order items, decrements stock, and clears the cart.
- **Access:** Authenticated (`BUYER`)
- **Response `201 Created`:**
  ```json
  {
    "success": true,
    "data": {
      "id": 12,
      "buyerId": 4,
      "buyerName": "Mira Patel",
      "status": "PENDING",
      "totalAmount": 1798.00,
      "createdAt": "2026-10-10T08:30:00",
      "items": [
        {
          "productId": 101,
          "productName": "Handwoven Indigo Tote",
          "quantity": 2,
          "unitPrice": 899.00,
          "sellerId": 2
        }
      ]
    },
    "error": null
  }
  ```
- **Response `409 Conflict`:** Cart empty or insufficient inventory.

#### `PUT /api/v1/orders/{id}/cancel`
- **Description:** Cancels a `PENDING` order and automatically restores item inventory.
- **Access:** Authenticated (`BUYER`)
- **Response `200 OK`:**
  ```json
  {
    "success": true,
    "data": { "status": "CANCELLED" },
    "error": null
  }
  ```
- **Response `409 Conflict`:** If order is not in `PENDING` status or belongs to another buyer.

---

### 2.5 Seller Orders & Status Transitions

#### `GET /api/v1/seller/orders`
- **Description:** Lists all incoming orders and aggregate sales revenue for the seller.
- **Access:** Authenticated (`SELLER`)
- **Response `200 OK`:**
  ```json
  {
    "success": true,
    "data": {
      "orders": [ ... ],
      "stats": {
        "orders": 5,
        "revenue": 4495.00
      }
    },
    "error": null
  }
  ```

#### `PUT /api/v1/seller/orders/{id}`
- **Description:** Transitions an order forward through the state machine: `PENDING` &rarr; `CONFIRMED` &rarr; `SHIPPED` &rarr; `DELIVERED`.
- **Access:** Authenticated (`SELLER`)
- **Request Body:**
  ```json
  {
    "status": "CONFIRMED"
  }
  ```
- **Response `200 OK`:**
  ```json
  {
    "success": true,
    "data": { "status": "updated" },
    "error": null
  }
  ```
- **Response `409 Conflict`:** If invalid transition is requested (e.g. attempting to jump from `PENDING` directly to `DELIVERED`).

---

### 2.6 Verified Product Reviews

#### `GET /api/v1/reviews?productId={id}`
- **Description:** Retrieves all verified reviews and average star rating for a product.
- **Access:** Public
- **Response `200 OK`:**
  ```json
  {
    "success": true,
    "data": {
      "items": [
        {
          "id": 1,
          "productId": 101,
          "userId": 4,
          "userName": "Mira Patel",
          "rating": 5,
          "comment": "Exquisite quality and craftmanship!",
          "createdAt": "2026-10-10T08:00:00"
        }
      ],
      "averageRating": 5.0
    },
    "error": null
  }
  ```

#### `POST /api/v1/reviews`
- **Description:** Submits a review. Requires verified delivery of the product to the buyer.
- **Access:** Authenticated (`BUYER`)
- **Request Body:**
  ```json
  {
    "productId": 101,
    "rating": 5,
    "comment": "Super fast delivery and authentic item."
  }
  ```
- **Response `201 Created`:** Created review entity.
- **Response `403 Forbidden`:** If user has not purchased and received the product.
- **Response `409 Conflict`:** If buyer has already reviewed this product.

---

### 2.7 AI Assistant Chatbot

#### `POST /api/v1/chat`
- **Description:** Handles real-time customer assistance queries with session caching and rate limiting.
- **Access:** Public
- **Request Body:**
  ```json
  {
    "message": "Where is my order?"
  }
  ```
- **Response `200 OK`:**
  ```json
  {
    "success": true,
    "data": {
      "reply": "Every order moves through Pending, Confirmed, Shipped and Delivered. Open My orders to see the current status. The seller updates it as your order progresses."
    },
    "error": null
  }
  ```
- **Response `429 Too Many Requests`:** When exceeding 10 messages per minute.
