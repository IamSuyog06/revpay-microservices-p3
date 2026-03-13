package com.revpay.user_service.dto;

public class SecurityQuestionCheckResponse {

    private String emailOrPhone;
    private String securityQuestion;

    public SecurityQuestionCheckResponse() {}

    public SecurityQuestionCheckResponse(
            String emailOrPhone, String securityQuestion) {
        this.emailOrPhone = emailOrPhone;
        this.securityQuestion = securityQuestion;
    }

    // Getters
    public String getEmailOrPhone() { return emailOrPhone; }
    public String getSecurityQuestion() { return securityQuestion; }

    // Setters
    public void setEmailOrPhone(String emailOrPhone) { this.emailOrPhone = emailOrPhone; }
    public void setSecurityQuestion(String securityQuestion) { this.securityQuestion = securityQuestion; }
}