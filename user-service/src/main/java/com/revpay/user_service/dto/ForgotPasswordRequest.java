package com.revpay.user_service.dto;

import jakarta.validation.constraints.NotBlank;

public class ForgotPasswordRequest {

    @NotBlank(message = "Email or phone is required")
    private String emailOrPhone;

    @NotBlank(message = "Security answer is required")
    private String securityAnswer;

    @NotBlank(message = "New password is required")
    private String newPassword;

    public ForgotPasswordRequest() {}

    // Getters
    public String getEmailOrPhone() { return emailOrPhone; }
    public String getSecurityAnswer() { return securityAnswer; }
    public String getNewPassword() { return newPassword; }

    // Setters
    public void setEmailOrPhone(String emailOrPhone) { this.emailOrPhone = emailOrPhone; }
    public void setSecurityAnswer(String securityAnswer) { this.securityAnswer = securityAnswer; }
    public void setNewPassword(String newPassword) { this.newPassword = newPassword; }
}
