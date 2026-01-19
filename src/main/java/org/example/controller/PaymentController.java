package org.example.controller;

import org.example.dto.PaymentRequest;
import org.example.service.PaymentService;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping("/create")
    public Map<String, Object> createPayment(@RequestBody PaymentRequest request) {
        return paymentService.createPayment(request.getOrderId(), request.getAmount());
    }
}
