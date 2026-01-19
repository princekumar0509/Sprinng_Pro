package org.example.client;

import org.example.dto.PaymentRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Component
public class PaymentServiceClient {

    @Value("${payment.service.url}")
    private String paymentServiceUrl;

    private final RestTemplate restTemplate;

    public PaymentServiceClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public Map<String, Object> createPayment(PaymentRequest request) {
        String url = paymentServiceUrl + "/payments/create";
        return restTemplate.postForObject(url, request, Map.class);
    }
}
