package com.revpay.user_service.dto;


public class LoginResponse {

    private String token;
    private String email;
    private String fullName;
    private String role;
    private Long userId;

    // Constructors
    public LoginResponse() {}

    public LoginResponse(String token, String email,
                         String fullName, String role, Long userId) {
        this.token = token;
        this.email = email;
        this.fullName = fullName;
        this.role = role;
        this.userId = userId;
    }

    // Getters
    public String getToken() { return token; }
    public String getEmail() { return email; }
    public String getFullName() { return fullName; }
    public String getRole() { return role; }
    public Long getUserId() { return userId; }

    // Setters
    public void setToken(String token) { this.token = token; }
    public void setEmail(String email) { this.email = email; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public void setRole(String role) { this.role = role; }
    public void setUserId(Long userId) { this.userId = userId; }
}
