package com.revpay.wallet_service.entity;

import com.revpay.wallet_service.enums.CardType;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "payment_methods")
public class PaymentMethod {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    private String cardHolderName;

    // Only last 4 digits stored
    @Column(nullable = false, length = 4)
    private String lastFourDigits;

    @Column(nullable = false)
    private String expiryMonth;

    @Column(nullable = false)
    private String expiryYear;

    // CVV is NEVER stored
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CardType cardType;

    private String billingAddress;

    @Column(nullable = false)
    private boolean isDefault = false;

    @Column(nullable = false)
    private boolean isActive = true;

    @Column(updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public PaymentMethod() {}

    // Getters
    public Long getId() { return id; }
    public Long getUserId() { return userId; }
    public String getCardHolderName() { return cardHolderName; }
    public String getLastFourDigits() { return lastFourDigits; }
    public String getExpiryMonth() { return expiryMonth; }
    public String getExpiryYear() { return expiryYear; }
    public CardType getCardType() { return cardType; }
    public String getBillingAddress() { return billingAddress; }
    public boolean isDefault() { return isDefault; }
    public boolean isActive() { return isActive; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }

    // Setters
    public void setId(Long id) { this.id = id; }
    public void setUserId(Long userId) { this.userId = userId; }
    public void setCardHolderName(String cardHolderName) { this.cardHolderName = cardHolderName; }
    public void setLastFourDigits(String lastFourDigits) { this.lastFourDigits = lastFourDigits; }
    public void setExpiryMonth(String expiryMonth) { this.expiryMonth = expiryMonth; }
    public void setExpiryYear(String expiryYear) { this.expiryYear = expiryYear; }
    public void setCardType(CardType cardType) { this.cardType = cardType; }
    public void setBillingAddress(String billingAddress) { this.billingAddress = billingAddress; }
    public void setDefault(boolean aDefault) { isDefault = aDefault; }
    public void setActive(boolean active) { isActive = active; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    @Override
    public String toString() {
        return "PaymentMethod{" +
                "id=" + id +
                ", userId=" + userId +
                ", lastFourDigits='" + lastFourDigits + '\'' +
                ", cardType=" + cardType +
                ", isDefault=" + isDefault +
                '}';
    }
}