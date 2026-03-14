package com.revpay.wallet_service.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public class InternalWalletRequest {

    @NotNull(message = "User ID is required")
    private Long userId;

    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.01",
            message = "Amount must be greater than 0")
    private BigDecimal amount;

    public InternalWalletRequest() {}

    public InternalWalletRequest(Long userId,
                                 BigDecimal amount) {
        this.userId = userId;
        this.amount = amount;
    }

    // Getters
    public Long getUserId() { return userId; }
    public BigDecimal getAmount() { return amount; }

    // Setters
    public void setUserId(Long userId) { this.userId = userId; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
}