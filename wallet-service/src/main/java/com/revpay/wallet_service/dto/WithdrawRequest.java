package com.revpay.wallet_service.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public class WithdrawRequest {

    @NotNull(message = "Amount is required")
    @DecimalMin(value = "1.0",
            message = "Amount must be at least 1")
    private BigDecimal amount;

    @NotNull(message = "PIN is required")
    private String pin;

    public WithdrawRequest() {}

    // Getters
    public BigDecimal getAmount() { return amount; }
    public String getPin() { return pin; }

    // Setters
    public void setAmount(BigDecimal amount) { this.amount = amount; }
    public void setPin(String pin) { this.pin = pin; }
}