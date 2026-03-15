package com.revpay.invoice_service.client;

import com.revpay.invoice_service.dto.UserResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "USER-SERVICE")
public interface UserServiceClient {

    @GetMapping("/api/users/{id}")
    UserResponse getUserById(
            @PathVariable("id") Long id);

    @GetMapping("/api/users/email/{email}")
    UserResponse getUserByEmail(
            @PathVariable("email") String email);
}