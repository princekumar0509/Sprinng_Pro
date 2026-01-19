package org.example.service;

import org.example.model.*;
import org.example.repository.*;
import org.springframework.stereotype.Service;

import java.util.*;


@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final CartRepository cartRepository;
    private final ProductRepository productRepository;
    private final PaymentRepository paymentRepository;

    public OrderService(OrderRepository orderRepository,
                        CartRepository cartRepository,
                        ProductRepository productRepository,
                        PaymentRepository paymentRepository) {
        this.orderRepository = orderRepository;
        this.cartRepository = cartRepository;
        this.productRepository = productRepository;
        this.paymentRepository = paymentRepository;
    }

    // POST /api/orders
    // Create order from cart
    public Map<String, Object> createOrder(String userId) {


        List<CartItem> cartItems = cartRepository.findByUserId(userId);
        if (cartItems.isEmpty()) {
            throw new RuntimeException("Cart is empty");
        }

        Order order = new Order();
        order.setUserId(userId);
        order.setStatus("CREATED");
        order.setCreatedAt(java.time.Instant.now());
        order = orderRepository.save(order);

        double totalAmount = 0;
        List<Map<String, Object>> items = new ArrayList<>();

        for (CartItem cartItem : cartItems) {

            Product product = productRepository.findById(cartItem.getProductId())
                    .orElseThrow(() -> new RuntimeException("Product not found"));

            if (product.getStock() < cartItem.getQuantity()) {
                throw new RuntimeException("Insufficient stock for product: " + product.getName());
            }

            // Create OrderItem
            OrderItem orderItem = new OrderItem();
            orderItem.setOrderId(order.getId());
            orderItem.setProductId(product.getId());
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setPrice(product.getPrice());

            // reduce stock
            product.setStock(product.getStock() - cartItem.getQuantity());
            productRepository.save(product);

            totalAmount += product.getPrice() * cartItem.getQuantity();

            Map<String, Object> item = new HashMap<>();
            item.put("productId", product.getId());
            item.put("quantity", cartItem.getQuantity());
            item.put("price", product.getPrice());

            items.add(item);
        }

        order.setTotalAmount(totalAmount);
        orderRepository.save(order);

        // clear cart
        cartRepository.deleteByUserId(userId);

        Map<String, Object> response = new HashMap<>();
        response.put("id", order.getId());
        response.put("userId", userId);
        response.put("totalAmount", totalAmount);
        response.put("status", order.getStatus());
        response.put("items", items);

        return response;
    }


    // GET /api/orders/{orderId}
    // Get order details
    public Map<String, Object> getOrderDetails(String orderId) {

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        Optional<Payment> paymentOpt = paymentRepository.findByOrderId(orderId);

        Map<String, Object> response = new HashMap<>();
        response.put("id", order.getId());
        response.put("userId", order.getUserId());
        response.put("totalAmount", order.getTotalAmount());
        response.put("status", order.getStatus());

        if (paymentOpt.isPresent()) {
            Payment payment = paymentOpt.get();
            Map<String, Object> paymentMap = new HashMap<>();
            paymentMap.put("id", payment.getId());
            paymentMap.put("status", payment.getStatus());
            paymentMap.put("amount", order.getTotalAmount());
            response.put("payment", paymentMap);
        }

        return response;
    }
}
