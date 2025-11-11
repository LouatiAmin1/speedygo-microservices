package com.esprit.microservice.microservicetrip.DTO;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class PaymentDTO {

    private Integer paymentId;
    private BigDecimal amount;
    private String currency;
    private String status;
    private String paymentMethod;
    private String stripeChargeId;
    private LocalDateTime createdAt;

    // Constructeur vide
    public PaymentDTO() {}

    // Constructeur complet
    public PaymentDTO(Integer paymentId, BigDecimal amount, String currency, String status,
                      String paymentMethod, String stripeChargeId, LocalDateTime createdAt) {
        this.paymentId = paymentId;
        this.amount = amount;
        this.currency = currency;
        this.status = status;
        this.paymentMethod = paymentMethod;
        this.stripeChargeId = stripeChargeId;
        this.createdAt = createdAt;
    }

    // Getters et Setters
    public Integer getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(Integer paymentId) {
        this.paymentId = paymentId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getStripeChargeId() {
        return stripeChargeId;
    }

    public void setStripeChargeId(String stripeChargeId) {
        this.stripeChargeId = stripeChargeId;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
