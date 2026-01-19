# Postman Testing Guide

## Complete Order Flow Test

### Step 1: Create Products

**POST** `http://localhost:8080/api/products`

```json
{
  "name": "Laptop",
  "description": "Gaming Laptop",
  "price": 50000.0,
  "stock": 10
}
```

**Response:**
```json
{
  "id": "67890abc...",
  "name": "Laptop",
  "description": "Gaming Laptop",
  "price": 50000.0,
  "stock": 10
}
```

**Save the `id` as `{{productId}}`**

---

### Step 2: List All Products

**GET** `http://localhost:8080/api/products`

**Response:**
```json
[
  {
    "id": "67890abc...",
    "name": "Laptop",
    "price": 50000.0,
    "stock": 10
  }
]
```

---

### Step 3: Add Item to Cart

**POST** `http://localhost:8080/api/cart/add`

```json
{
  "userId": "user123",
  "productId": "{{productId}}",
  "quantity": 2
}
```

**Response:**
```json
{
  "id": "cart456...",
  "userId": "user123",
  "productId": "67890abc...",
  "quantity": 2
}
```

---

### Step 4: View Cart

**GET** `http://localhost:8080/api/cart/user123`

**Response:**
```json
[
  {
    "id": "cart456...",
    "productId": "67890abc...",
    "quantity": 2,
    "product": {
      "id": "67890abc...",
      "name": "Laptop",
      "price": 50000.0
    }
  }
]
```

---

### Step 5: Create Order from Cart

**POST** `http://localhost:8080/api/orders`

```json
{
  "userId": "user123"
}
```

**Response:**
```json
{
  "id": "order789...",
  "userId": "user123",
  "totalAmount": 100000.0,
  "status": "CREATED",
  "items": [
    {
      "productId": "67890abc...",
      "quantity": 2,
      "price": 50000.0
    }
  ]
}
```

**Save the `id` as `{{orderId}}`**

---

### Step 6: Create Payment

**POST** `http://localhost:8080/api/payments/create`

```json
{
  "orderId": "{{orderId}}",
  "amount": 100000.0
}
```

**Response:**
```json
{
  "paymentId": "pay_mock_abc123",
  "orderId": "order789...",
  "amount": 100000.0,
  "status": "PENDING"
}
```

---

### Step 7: Wait 3 Seconds, Then Check Order Status

**GET** `http://localhost:8080/api/orders/{{orderId}}`

**Response (after webhook):**
```json
{
  "id": "order789...",
  "userId": "user123",
  "totalAmount": 100000.0,
  "status": "PAID",
  "payment": {
    "id": "pay123...",
    "status": "SUCCESS",
    "amount": 100000.0
  }
}
```

---

### Step 8: Clear Cart (Optional)

**DELETE** `http://localhost:8080/api/cart/user123/clear`

**Response:**
```json
{
  "message": "Cart cleared successfully"
}
```

---

## Postman Variables

Create these variables in Postman for easier testing:

- `baseUrl`: `http://localhost:8080`
- `userId`: `user123`
- `productId`: (set from Step 1 response)
- `orderId`: (set from Step 5 response)

## Expected Flow

1. ✅ Create products
2. ✅ Add items to cart
3. ✅ View cart
4. ✅ Create order (cart cleared, stock reduced)
5. ✅ Create payment (status: PENDING)
6. ⏳ Wait 3 seconds
7. ✅ Webhook called automatically
8. ✅ Order status updated to PAID
9. ✅ Payment status updated to SUCCESS
