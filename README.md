# Minimal E-Commerce Backend API

A Spring Boot REST API for e-commerce with MongoDB and mock payment service integration.

## Prerequisites

- Java 17+
- Maven
- MongoDB (running on localhost:27017)

## Project Structure

```
Minimal-E-Commerce-Backend-API/
├── src/main/java/org/example/          # Main E-Commerce API (Port 8080)
│   ├── controller/                      # REST Controllers
│   ├── service/                         # Business Logic
│   ├── repository/                      # MongoDB Repositories
│   ├── model/                           # Domain Models
│   ├── dto/                             # Request/Response DTOs
│   ├── client/                          # Payment Service Client
│   ├── webhook/                         # Webhook Controllers
│   └── config/                          # Configuration
└── mock-payment-service/                # Mock Payment Service (Port 8081)
    └── src/main/java/org/example/mockpayment/
```

## Running the Applications

### 1. Start MongoDB
```bash
# Make sure MongoDB is running on localhost:27017
mongod
```

### 2. Start Mock Payment Service (Port 8081)
```bash
cd mock-payment-service
mvn spring-boot:run
```

### 3. Start E-Commerce API (Port 8080)
```bash
# In the main project directory
mvn spring-boot:run
```

## API Endpoints

### Product APIs
- **POST** `/api/products` - Create product
- **GET** `/api/products` - List all products

### Cart APIs
- **POST** `/api/cart/add` - Add item to cart
- **GET** `/api/cart/{userId}` - Get user's cart
- **DELETE** `/api/cart/{userId}/clear` - Clear cart

### Order APIs
- **POST** `/api/orders` - Create order from cart
- **GET** `/api/orders/{orderId}` - Get order details

### Payment APIs
- **POST** `/api/payments/create` - Create payment
- **POST** `/api/webhooks/payment` - Payment webhook (called by mock service)

## Testing Flow

### 1. Create Products
```json
POST http://localhost:8080/api/products
{
  "name": "Laptop",
  "description": "Gaming Laptop",
  "price": 50000.0,
  "stock": 10
}
```

### 2. Add to Cart
```json
POST http://localhost:8080/api/cart/add
{
  "userId": "user123",
  "productId": "<product-id>",
  "quantity": 2
}
```

### 3. View Cart
```
GET http://localhost:8080/api/cart/user123
```

### 4. Create Order
```json
POST http://localhost:8080/api/orders
{
  "userId": "user123"
}
```

### 5. Create Payment
```json
POST http://localhost:8080/api/payments/create
{
  "orderId": "<order-id>",
  "amount": 100000.0
}
```

### 6. Check Order Status (after 3 seconds)
```
GET http://localhost:8080/api/orders/<order-id>
```

## How It Works

1. **Product Management**: Create and list products
2. **Cart Management**: Add items to cart, view cart, clear cart
3. **Order Creation**: Convert cart items to order, reduce stock, clear cart
4. **Payment Flow**:
   - E-Commerce API calls Mock Payment Service
   - Mock Payment Service returns PENDING status
   - After 3 seconds, Mock Payment Service calls webhook
   - Webhook updates payment and order status to SUCCESS/PAID

## Database Collections

- `products` - Product catalog
- `cart_items` - Shopping cart items
- `orders` - Customer orders
- `order_items` - Items in each order
- `payments` - Payment records
- `users` - User information (optional)

## Notes

- Mock Payment Service automatically calls the webhook after 3 seconds
- Order status: CREATED → PAID (after successful payment)
- Payment status: PENDING → SUCCESS
- Stock is reduced when order is created
- Cart is cleared after order creation
