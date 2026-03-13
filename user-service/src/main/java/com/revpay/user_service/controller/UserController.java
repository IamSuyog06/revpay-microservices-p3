package com.revpay.user_service.controller;

import com.revpay.user_service.dto.*;
import com.revpay.user_service.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUserById(
            @PathVariable Long id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<UserResponse> getUserByEmail(
            @PathVariable String email) {
        return ResponseEntity.ok(
                userService.getUserByEmail(email));
    }

    @PutMapping("/{id}/profile")
    public ResponseEntity<UserResponse> updateProfile(
            @PathVariable Long id,
            @Valid @RequestBody UpdateProfileRequest request) {
        return ResponseEntity.ok(
                userService.updateProfile(id, request));
    }

    @PutMapping("/{id}/change-password")
    public ResponseEntity<String> changePassword(
            @PathVariable Long id,
            @Valid @RequestBody ChangePasswordRequest request) {
        userService.changePassword(id, request);
        return ResponseEntity.ok("Password changed successfully");
    }

    @PostMapping("/{id}/set-pin")
    public ResponseEntity<String> setPin(
            @PathVariable Long id,
            @Valid @RequestBody SetPinRequest request) {
        userService.setPin(id, request);
        return ResponseEntity.ok("PIN set successfully");
    }

    // Internal endpoint — called by other services (Wallet, Transaction)
    // to verify PIN before transactions
    @PostMapping("/{id}/verify-pin")
    public ResponseEntity<Boolean> verifyPin(
            @PathVariable Long id,
            @RequestParam String pin) {
        return ResponseEntity.ok(userService.verifyPin(id, pin));
    }
}