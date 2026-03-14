package com.revpay.user_service.client;

public class CreateWalletRequest {

    private Long userId;

    public CreateWalletRequest() {}

    public CreateWalletRequest(Long userId) {
        this.userId = userId;
    }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
}