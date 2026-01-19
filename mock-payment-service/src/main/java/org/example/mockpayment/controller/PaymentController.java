package org.example.mockpayment.controller;

import org.example.mockpayment.dto.PaymentRequest;
import org.example.mockpayment.dto.WebhookRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/payments")
public class PaymentController {

    @Value("${ecommerce.webhook.url}")
    private String webhookUrl;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    @PostMapping("/create")
    public Map<String, Object> createPayment(@RequestBody PaymentRequest request) {
        String paymentId = "pay_mock_" + UUID.randomUUID().toString().substring(0, 8);

        // Schedule webhook callback after 3 seconds
        scheduler.schedule(() -> {
            try {
                WebhookRequest webhook = new WebhookRequest(
                        request.getOrderId(),
                        "SUCCESS",
                        paymentId
                );
                restTemplate.postForObject(webhookUrl, webhook, String.class);
            } catch (Exception e) {
                System.err.println("Failed to send webhook: " + e.getMessage());
            }
        }, 3, TimeUnit.SECONDS);

        Map<String, Object> response = new HashMap<>();
        response.put("paymentId", paymentId);
        response.put("orderId", request.getOrderId());
        response.put("amount", request.getAmount());
        response.put("status", "PENDING");

        return response;
    }
}
