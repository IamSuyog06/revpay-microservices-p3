package com.revpay.wallet_service.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class PaymentMethodRequest {

    @NotBlank(message = "Card holder name is required")
    private String cardHolderName;

    @NotBlank(message = "Card number is required")
    @Size(min = 16, max = 16,
            message = "Card number must be 16 digits")
    @Pattern(regexp = "^[0-9]{16}$",
            message = "Card number must contain only digits")
    private String cardNumber;

    @NotBlank(message = "Expiry month is required")
    @Pattern(regexp = "^(0[1-9]|1[0-2])$",
            message = "Expiry month must be between 01 and 12")
    private String expiryMonth;

    @NotBlank(message = "Expiry year is required")
    @Pattern(regexp = "^[0-9]{4}$",
            message = "Expiry year must be 4 digits")
    private String expiryYear;

    @NotBlank(message = "CVV is required")
    @Pattern(regexp = "^[0-9]{3,4}$",
            message = "CVV must be 3 or 4 digits")
    private String cvv;

    @NotNull(message = "Card type is required")
    private String cardType; // "CREDIT" or "DEBIT"

    private String billingAddress;

    public PaymentMethodRequest() {}

    // Getters
    public String getCardHolderName() { return cardHolderName; }
    public String getCardNumber() { return cardNumber; }
    public String getExpiryMonth() { return expiryMonth; }
    public String getExpiryYear() { return expiryYear; }
    public String getCvv() { return cvv; }
    public String getCardType() { return cardType; }
    public String getBillingAddress() { return billingAddress; }

    // Setters
    public void setCardHolderName(String cardHolderName) { this.cardHolderName = cardHolderName; }
    public void setCardNumber(String cardNumber) { this.cardNumber = cardNumber; }
    public void setExpiryMonth(String expiryMonth) { this.expiryMonth = expiryMonth; }
    public void setExpiryYear(String expiryYear) { this.expiryYear = expiryYear; }
    public void setCvv(String cvv) { this.cvv = cvv; }
    public void setCardType(String cardType) { this.cardType = cardType; }
    public void setBillingAddress(String billingAddress) { this.billingAddress = billingAddress; }
}