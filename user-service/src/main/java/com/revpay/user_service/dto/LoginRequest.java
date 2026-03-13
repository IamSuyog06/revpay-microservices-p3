package com.revpay.user_service.dto;

import jakarta.validation.constraints.NotBlank;

public class LoginRequest {

    @NotBlank(message = "Email or phone is required")
    private String emailOrPhone;

    @NotBlank(message = "Password is required")
    private String password;

    // Constructors
    public LoginRequest() {}

    // Getters
    public String getEmailOrPhone() { return emailOrPhone; }
    public String getPassword() { return password; }

    // Setters
    public void setEmailOrPhone(String emailOrPhone) { this.emailOrPhone = emailOrPhone; }
    public void setPassword(String password) { this.password = password; }
}