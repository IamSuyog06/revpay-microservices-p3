package com.revpay.user_service.dto;

import jakarta.validation.constraints.NotBlank;

public class SecurityQuestionRequest {

    @NotBlank(message = "Email or phone is required")
    private String emailOrPhone;

    public SecurityQuestionRequest() {}

    // Getter
    public String getEmailOrPhone() { return emailOrPhone; }

    // Setter
    public void setEmailOrPhone(String emailOrPhone) { this.emailOrPhone = emailOrPhone; }
}