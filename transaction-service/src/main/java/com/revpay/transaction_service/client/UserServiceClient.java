package com.revpay.transaction_service.client;

import com.revpay.transaction_service.dto.UserResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "USER-SERVICE")
public interface UserServiceClient {

    @GetMapping("/api/users/{id}")
    UserResponse getUserById(
            @PathVariable("id") Long id);

    @GetMapping("/api/users/email/{email}")
    UserResponse getUserByEmail(
            @PathVariable("email") String email);

    @PostMapping("/api/users/{userId}/verify-pin")
    Boolean verifyPin(
            @PathVariable("userId") Long userId,
            @RequestParam("pin") String pin);
}