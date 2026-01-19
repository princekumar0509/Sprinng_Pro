package org.example.mockpayment.dto;

public class WebhookRequest {
    private String orderId;
    private String status;
    private String paymentId;

    public WebhookRequest() {}

    public WebhookRequest(String orderId, String status, String paymentId) {
        this.orderId = orderId;
        this.status = status;
        this.paymentId = paymentId;
    }

    public String getOrderId() { return orderId; }
    public void setOrderId(String orderId) { this.orderId = orderId; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getPaymentId() { return paymentId; }
    public void setPaymentId(String paymentId) { this.paymentId = paymentId; }
}
