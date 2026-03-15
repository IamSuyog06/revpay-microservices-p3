package com.revpay.transaction_service.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public class SendMoneyRequest {

    // Can be email, phone, or userId
    @NotBlank(message = "Recipient is required")
    private String recipientIdentifier;

    @NotNull(message = "Amount is required")
    @DecimalMin(value = "1.0",
            message = "Amount must be at least 1")
    private BigDecimal amount;

    @NotBlank(message = "PIN is required")
    private String pin;

    private String note;

    public SendMoneyRequest() {}

    // Getters
    public String getRecipientIdentifier() { return recipientIdentifier; }
    public BigDecimal getAmount() { return amount; }
    public String getPin() { return pin; }
    public String getNote() { return note; }

    // Setters
    public void setRecipientIdentifier(String recipientIdentifier) { this.recipientIdentifier = recipientIdentifier; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
    public void setPin(String pin) { this.pin = pin; }
    public void setNote(String note) { this.note = note; }
}