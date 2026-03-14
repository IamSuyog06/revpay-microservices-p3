package com.revpay.user_service.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "WALLET-SERVICE")
public interface WalletServiceClient {

    @PostMapping("/api/wallets/create")
    void createWallet(@RequestBody CreateWalletRequest request);
}