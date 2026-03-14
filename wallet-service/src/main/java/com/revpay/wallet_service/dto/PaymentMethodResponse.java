package com.revpay.wallet_service.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.time.LocalDateTime;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class PaymentMethodResponse {

    private Long id;
    private Long userId;
    private String cardHolderName;
    private String maskedCardNumber;
    private String expiryMonth;
    private String expiryYear;
    private String cardType;
    private String billingAddress;
    private boolean isDefault;
    private LocalDateTime createdAt;

    public PaymentMethodResponse() {}

    // Getters
    public Long getId() { return id; }
    public Long getUserId() { return userId; }
    public String getCardHolderName() { return cardHolderName; }
    public String getMaskedCardNumber() { return maskedCardNumber; }
    public String getExpiryMonth() { return expiryMonth; }
    public String getExpiryYear() { return expiryYear; }
    public String getCardType() { return cardType; }
    public String getBillingAddress() { return billingAddress; }
    public boolean isDefault() { return isDefault; }
    public LocalDateTime getCreatedAt() { return createdAt; }

    // Setters
    public void setId(Long id) { this.id = id; }
    public void setUserId(Long userId) { this.userId = userId; }
    public void setCardHolderName(String cardHolderName) { this.cardHolderName = cardHolderName; }
    public void setMaskedCardNumber(String maskedCardNumber) { this.maskedCardNumber = maskedCardNumber; }
    public void setExpiryMonth(String expiryMonth) { this.expiryMonth = expiryMonth; }
    public void setExpiryYear(String expiryYear) { this.expiryYear = expiryYear; }
    public void setCardType(String cardType) { this.cardType = cardType; }
    public void setBillingAddress(String billingAddress) { this.billingAddress = billingAddress; }
    public void setDefault(boolean aDefault) { isDefault = aDefault; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}