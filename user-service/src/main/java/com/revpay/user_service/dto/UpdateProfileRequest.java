package com.revpay.user_service.dto;

import jakarta.validation.constraints.Pattern;

public class UpdateProfileRequest {

    private String fullName;

    @Pattern(
            regexp = "^[0-9]{10}$",
            message = "Phone number must be exactly 10 digits"
    )
    private String phone;

    // Business fields
    private String businessName;
    private String businessType;
    private String businessAddress;

    @Pattern(
            regexp = "^[0-9]{10}$",
            message = "Business contact must be exactly 10 digits"
    )
    private String businessContact;

    public UpdateProfileRequest() {}

    // Getters
    public String getFullName() { return fullName; }
    public String getPhone() { return phone; }
    public String getBusinessName() { return businessName; }
    public String getBusinessType() { return businessType; }
    public String getBusinessAddress() { return businessAddress; }
    public String getBusinessContact() { return businessContact; }

    // Setters
    public void setFullName(String fullName) { this.fullName = fullName; }
    public void setPhone(String phone) { this.phone = phone; }
    public void setBusinessName(String businessName) { this.businessName = businessName; }
    public void setBusinessType(String businessType) { this.businessType = businessType; }
    public void setBusinessAddress(String businessAddress) { this.businessAddress = businessAddress; }
    public void setBusinessContact(String businessContact) { this.businessContact = businessContact; }
}