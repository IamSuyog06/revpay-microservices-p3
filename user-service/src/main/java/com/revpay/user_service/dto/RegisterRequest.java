package com.revpay.user_service.dto;

import com.revpay.user_service.enums.SecurityQuestion;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class RegisterRequest {

    @NotBlank(message = "Full name is required")
    private String fullName;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    @NotBlank(message = "Phone number is required")
    @Pattern(
            regexp = "^[0-9]{10}$",
            message = "Phone number must be exactly 10 digits"
    )
    private String phone;

    @NotBlank(message = "Password is required")
    @Size(min = 6, message = "Password must be at least 6 characters")
    private String password;

    @NotBlank(message = "Role is required")
    private String role; // "PERSONAL" or "BUSINESS"

    // Security question
    private SecurityQuestion securityQuestion;

    @NotBlank(message = "Security answer is required")
    private String securityAnswer;

    // Business fields (only required if role = BUSINESS)
    private String businessName;
    private String businessType;
    private String taxId;
    private String businessAddress;

    @Pattern(
            regexp = "^[0-9]{10}$",
            message = "Business contact must be exactly 10 digits"
    )
    private String businessContact;

    // Constructors
    public RegisterRequest() {}

    // Getters
    public String getFullName() { return fullName; }
    public String getEmail() { return email; }
    public String getPhone() { return phone; }
    public String getPassword() { return password; }
    public String getRole() { return role; }
    public SecurityQuestion getSecurityQuestion() { return securityQuestion; }
    public String getSecurityAnswer() { return securityAnswer; }
    public String getBusinessName() { return businessName; }
    public String getBusinessType() { return businessType; }
    public String getTaxId() { return taxId; }
    public String getBusinessAddress() { return businessAddress; }
    public String getBusinessContact() { return businessContact; }

    // Setters
    public void setFullName(String fullName) { this.fullName = fullName; }
    public void setEmail(String email) { this.email = email; }
    public void setPhone(String phone) { this.phone = phone; }
    public void setPassword(String password) { this.password = password; }
    public void setRole(String role) { this.role = role; }
    public void setSecurityQuestion(SecurityQuestion securityQuestion) { this.securityQuestion = securityQuestion; }
    public void setSecurityAnswer(String securityAnswer) { this.securityAnswer = securityAnswer; }
    public void setBusinessName(String businessName) { this.businessName = businessName; }
    public void setBusinessType(String businessType) { this.businessType = businessType; }
    public void setTaxId(String taxId) { this.taxId = taxId; }
    public void setBusinessAddress(String businessAddress) { this.businessAddress = businessAddress; }
    public void setBusinessContact(String businessContact) { this.businessContact = businessContact; }
}