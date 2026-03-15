package com.revpay.invoice_service.client;

import com.revpay.invoice_service.dto.WalletResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Map;

@FeignClient(name = "WALLET-SERVICE")
public interface WalletServiceClient {

    @GetMapping("/api/wallets/internal/balance/{userId}")
    WalletResponse getBalance(
            @PathVariable("userId") Long userId);

    @PostMapping("/api/wallets/internal/deduct")
    WalletResponse deductBalance(
            @RequestBody Map<String, Object> request);

    @PostMapping("/api/wallets/internal/credit")
    WalletResponse creditBalance(
            @RequestBody Map<String, Object> request);
}