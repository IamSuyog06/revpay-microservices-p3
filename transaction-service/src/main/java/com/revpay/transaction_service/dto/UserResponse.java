package com.revpay.transaction_service.dto;

public class UserResponse {

    private Long id;
    private String email;
    private String fullName;
    private String phone;
    private String role;

    public UserResponse() {}

    // Getters
    public Long getId() { return id; }
    public String getEmail() { return email; }
    public String getFullName() { return fullName; }
    public String getPhone() { return phone; }
    public String getRole() { return role; }

    // Setters
    public void setId(Long id) { this.id = id; }
    public void setEmail(String email) { this.email = email; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public void setPhone(String phone) { this.phone = phone; }
    public void setRole(String role) { this.role = role; }
}