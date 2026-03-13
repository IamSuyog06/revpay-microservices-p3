package com.revpay.user_service.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.LocalDateTime;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserResponse {

    private Long id;
    private String email;
    private String fullName;
    private String phone;
    private String role;
    private boolean isVerified;
    private boolean isActive;
    private LocalDateTime createdAt;

    // Business fields
    private String businessName;
    private String businessType;
    private String taxId;
    private String businessAddress;
    private String businessContact;
    private String verificationStatus;

    public UserResponse() {}

    // Getters
    public Long getId() { return id; }
    public String getEmail() { return email; }
    public String getFullName() { return fullName; }
    public String getPhone() { return phone; }
    public String getRole() { return role; }
    public boolean isVerified() { return isVerified; }
    public boolean isActive() { return isActive; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public String getBusinessName() { return businessName; }
    public String getBusinessType() { return businessType; }
    public String getTaxId() { return taxId; }
    public String getBusinessAddress() { return businessAddress; }
    public String getBusinessContact() { return businessContact; }
    public String getVerificationStatus() { return verificationStatus; }

    // Setters
    public void setId(Long id) { this.id = id; }
    public void setEmail(String email) { this.email = email; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public void setPhone(String phone) { this.phone = phone; }
    public void setRole(String role) { this.role = role; }
    public void setVerified(boolean verified) { isVerified = verified; }
    public void setActive(boolean active) { isActive = active; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public void setBusinessName(String businessName) { this.businessName = businessName; }
    public void setBusinessType(String businessType) { this.businessType = businessType; }
    public void setTaxId(String taxId) { this.taxId = taxId; }
    public void setBusinessAddress(String businessAddress) { this.businessAddress = businessAddress; }
    public void setBusinessContact(String businessContact) { this.businessContact = businessContact; }
    public void setVerificationStatus(String verificationStatus) { this.verificationStatus = verificationStatus; }
}