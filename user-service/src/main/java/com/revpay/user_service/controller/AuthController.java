package com.revpay.user_service.controller;

import com.revpay.user_service.dto.*;
import com.revpay.user_service.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<UserResponse> register(
            @Valid @RequestBody RegisterRequest request) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(userService.register(request));
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(
            @Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(userService.login(request));
    }

    @GetMapping("/security-questions")
    public ResponseEntity<List<SecurityQuestionResponse>> getSecurityQuestions() {
        return ResponseEntity.ok(
                SecurityQuestionResponse.getAllQuestions());
    }

    @PostMapping("/get-security-question")
    public ResponseEntity<SecurityQuestionCheckResponse> getSecurityQuestion(
            @Valid @RequestBody SecurityQuestionRequest request) {
        return ResponseEntity.ok(
                userService.getSecurityQuestion(request));
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<String> forgotPassword(
            @Valid @RequestBody ForgotPasswordRequest request) {
        userService.forgotPassword(request);
        return ResponseEntity.ok(
                "Password reset successfully");
    }
}