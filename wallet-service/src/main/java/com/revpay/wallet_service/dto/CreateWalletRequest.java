package com.revpay.wallet_service.dto;

import jakarta.validation.constraints.NotNull;

public class CreateWalletRequest {

    @NotNull(message = "User ID is required")
    private Long userId;

    public CreateWalletRequest() {}

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
}