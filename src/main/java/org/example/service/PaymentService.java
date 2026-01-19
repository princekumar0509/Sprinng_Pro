package org.example.service;

import org.example.client.PaymentServiceClient;
import org.example.dto.PaymentRequest;
import org.example.model.Order;
import org.example.model.Payment;
import org.example.repository.OrderRepository;
import org.example.repository.PaymentRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Map;
import java.util.Optional;

@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;
    private final PaymentServiceClient paymentServiceClient;

    public PaymentService(PaymentRepository paymentRepository,
                          OrderRepository orderRepository,
                          PaymentServiceClient paymentServiceClient) {
        this.paymentRepository = paymentRepository;
        this.orderRepository = orderRepository;
        this.paymentServiceClient = paymentServiceClient;
    }

    public Map<String, Object> createPayment(String orderId, Double amount) {
        // Validate order exists and status is CREATED
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        if (!"CREATED".equals(order.getStatus())) {
            throw new RuntimeException("Order status must be CREATED");
        }

        // Check if payment already exists
        Optional<Payment> existingPayment = paymentRepository.findByOrderId(orderId);
        if (existingPayment.isPresent()) {
            Payment payment = existingPayment.get();
            return Map.of(
                    "paymentId", payment.getPaymentId(),
                    "orderId", payment.getOrderId(),
                    "amount", payment.getAmount(),
                    "status", payment.getStatus()
            );
        }

        // Call mock payment service
        PaymentRequest request = new PaymentRequest();
        request.setOrderId(orderId);
        request.setAmount(amount);

        Map<String, Object> paymentResponse = paymentServiceClient.createPayment(request);

        // Save payment in database
        Payment payment = new Payment();
        payment.setOrderId(orderId);
        payment.setAmount(amount);
        payment.setStatus("PENDING");
        payment.setPaymentId((String) paymentResponse.get("paymentId"));
        payment.setCreatedAt(Instant.now());
        paymentRepository.save(payment);

        return paymentResponse;
    }

    public void handleWebhook(String orderId, String status, String paymentId) {
        // Find payment by orderId
        Payment payment = paymentRepository.findByOrderId(orderId)
                .orElseThrow(() -> new RuntimeException("Payment not found for order: " + orderId));

        // Update payment status
        payment.setStatus(status);
        paymentRepository.save(payment);

        // Update order status
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        if ("SUCCESS".equals(status)) {
            order.setStatus("PAID");
        } else {
            order.setStatus("FAILED");
        }
        orderRepository.save(order);
    }
}
