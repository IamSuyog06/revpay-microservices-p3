package com.revpay.transaction_service.dto;

import java.math.BigDecimal;

public class WalletResponse {

    private Long id;
    private Long userId;
    private BigDecimal balance;

    public WalletResponse() {}

    // Getters
    public Long getId() { return id; }
    public Long getUserId() { return userId; }
    public BigDecimal getBalance() { return balance; }

    // Setters
    public void setId(Long id) { this.id = id; }
    public void setUserId(Long userId) { this.userId = userId; }
    public void setBalance(BigDecimal balance) { this.balance = balance; }
}