package com.revpay.transaction_service.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public class MoneyRequestDTO {

    @NotBlank(message = "Recipient is required")
    private String recipientIdentifier;

    @NotNull(message = "Amount is required")
    @DecimalMin(value = "1.0",
            message = "Amount must be at least 1")
    private BigDecimal amount;

    private String purpose;

    public MoneyRequestDTO() {}

    // Getters
    public String getRecipientIdentifier() { return recipientIdentifier; }
    public BigDecimal getAmount() { return amount; }
    public String getPurpose() { return purpose; }

    // Setters
    public void setRecipientIdentifier(String recipientIdentifier) { this.recipientIdentifier = recipientIdentifier; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
    public void setPurpose(String purpose) { this.purpose = purpose; }
}