package org.example.webhook;

import org.example.dto.PaymentWebhookRequest;
import org.example.service.PaymentService;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/webhooks")
public class PaymentWebhookController {

    private final PaymentService paymentService;

    public PaymentWebhookController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping("/payment")
    public Map<String, String> handlePaymentWebhook(@RequestBody PaymentWebhookRequest request) {
        paymentService.handleWebhook(request.getOrderId(), request.getStatus(), request.getPaymentId());
        return Map.of("message", "Webhook processed successfully");
    }
}
