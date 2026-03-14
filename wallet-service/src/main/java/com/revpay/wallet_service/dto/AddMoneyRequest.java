package com.revpay.wallet_service.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public class AddMoneyRequest {

    @NotNull(message = "Amount is required")
    @DecimalMin(value = "1.0",
            message = "Amount must be at least 1")
    private BigDecimal amount;

    @NotNull(message = "Payment method ID is required")
    private Long paymentMethodId;

    @NotNull(message = "PIN is required")
    private String pin;

    public AddMoneyRequest() {}

    // Getters
    public BigDecimal getAmount() { return amount; }
    public Long getPaymentMethodId() { return paymentMethodId; }
    public String getPin() { return pin; }

    // Setters
    public void setAmount(BigDecimal amount) { this.amount = amount; }
    public void setPaymentMethodId(Long paymentMethodId) { this.paymentMethodId = paymentMethodId; }
    public void setPin(String pin) { this.pin = pin; }
}