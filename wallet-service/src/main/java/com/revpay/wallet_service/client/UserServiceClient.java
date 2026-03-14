package com.revpay.wallet_service.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "USER-SERVICE")
public interface UserServiceClient {

    // Called to verify PIN before add/withdraw
    @PostMapping("/api/users/{userId}/verify-pin")
    Boolean verifyPin(
            @PathVariable("userId") Long userId,
            @RequestParam("pin") String pin);
}