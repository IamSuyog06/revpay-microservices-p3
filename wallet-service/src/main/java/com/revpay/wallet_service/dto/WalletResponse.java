package com.revpay.wallet_service.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class WalletResponse {

    private Long id;
    private Long userId;
    private BigDecimal balance;
    private boolean isActive;
    private LocalDateTime createdAt;

    public WalletResponse() {}

    // Getters
    public Long getId() { return id; }
    public Long getUserId() { return userId; }
    public BigDecimal getBalance() { return balance; }
    public boolean isActive() { return isActive; }
    public LocalDateTime getCreatedAt() { return createdAt; }

    // Setters
    public void setId(Long id) { this.id = id; }
    public void setUserId(Long userId) { this.userId = userId; }
    public void setBalance(BigDecimal balance) { this.balance = balance; }
    public void setActive(boolean active) { isActive = active; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}