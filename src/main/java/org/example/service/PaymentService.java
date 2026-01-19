package org.example.service;

import org.example.model.Order;
import org.example.model.Payment;
import org.example.repository.OrderRepository;
import org.example.repository.PaymentRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;

    public PaymentService(PaymentRepository paymentRepository,
                          OrderRepository orderRepository) {
        this.paymentRepository = paymentRepository;
        this.orderRepository = orderRepository;
    }

    // POST /api/payments/create
    public Map<String, Object> createPayment(String orderId, Double amount) {

        // Validate order exists
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        // Check if payment already exists for this order
        Optional<Payment> existingPayment = paymentRepository.findByOrderId(orderId);
        if (existingPayment.isPresent()) {
            return buildPaymentResponse(existingPayment.get());
        }

        // Create new payment
        Payment payment = new Payment();
        payment.setOrderId(orderId);
        order.setTotalAmount(amount);
        payment.setStatus("PENDING");
        payment.setCreatedAt(Instant.now());

        payment = paymentRepository.save(payment);

        // Generate mock payment ID
        String mockPaymentId = "pay_mock_" + UUID.randomUUID();
        payment.setPaymentId(mockPaymentId);
        paymentRepository.save(payment);

        // Simulate webhook callback (async)
        simulateWebhook(payment.getId());

        return buildPaymentResponse(payment);
    }

    // Mock webhook simulation
    private void simulateWebhook(String paymentId) {
        new Thread(() -> {
            try {
                Thread.sleep(3000); // 3 seconds delay
                handleWebhookSuccess(paymentId);
            } catch (InterruptedException ignored) {
            }
        }).start();
    }

    // Webhook handler (mock)
    private void handleWebhookSuccess(String paymentId) {

        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new RuntimeException("Payment not found"));

        payment.setStatus("SUCCESS");
        paymentRepository.save(payment);

        // Update order status
        Order order = orderRepository.findById(payment.getOrderId())
                .orElseThrow(() -> new RuntimeException("Order not found"));

        order.setStatus("PAID");
        orderRepository.save(order);
    }

    // Response builder
    private Map<String, Object> buildPaymentResponse(Payment payment) {

        Map<String, Object> response = new HashMap<>();
        response.put("paymentId", payment.getPaymentId());
        response.put("orderId", payment.getOrderId());
        response.put("amount", order.getTotalAmount());
        response.put("status", payment.getStatus());

        return response;
    }
}
