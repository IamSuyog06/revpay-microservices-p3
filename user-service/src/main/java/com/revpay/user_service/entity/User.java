package com.revpay.user_service.entity;

import com.revpay.user_service.enums.BusinessVerificationStatus;
import com.revpay.user_service.enums.SecurityQuestion;
import com.revpay.user_service.enums.UserRole;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String fullName;

    @Column(unique = true, length = 10)
    private String phone;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserRole role;

    // Security
    private String transactionPin;

    @Enumerated(EnumType.STRING)
    private SecurityQuestion securityQuestion;

    private String securityAnswer;

    private boolean isVerified = false;
    private boolean isActive = true;

    // Business fields (null for PERSONAL accounts)
    private String businessName;
    private String businessType;

    @Column(unique = true)
    private String taxId;

    private String businessAddress;

    @Column(unique = true)
    private String businessContact;

    @Enumerated(EnumType.STRING)
    private BusinessVerificationStatus verificationStatus;

    @Column(updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (role == UserRole.BUSINESS) {
            verificationStatus =
                    BusinessVerificationStatus.PENDING;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // Constructors
    public User() {}

    // Getters
    public Long getId() { return id; }
    public String getEmail() { return email; }
    public String getPassword() { return password; }
    public String getFullName() { return fullName; }
    public String getPhone() { return phone; }
    public UserRole getRole() { return role; }
    public String getTransactionPin() { return transactionPin; }
    public SecurityQuestion getSecurityQuestion() { return securityQuestion; }
    public String getSecurityAnswer() { return securityAnswer; }
    public boolean isVerified() { return isVerified; }
    public boolean isActive() { return isActive; }
    public String getBusinessName() { return businessName; }
    public String getBusinessType() { return businessType; }
    public String getTaxId() { return taxId; }
    public String getBusinessAddress() { return businessAddress; }
    public String getBusinessContact() { return businessContact; }
    public BusinessVerificationStatus getVerificationStatus() { return verificationStatus; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }

    // Setters
    public void setId(Long id) { this.id = id; }
    public void setEmail(String email) { this.email = email; }
    public void setPassword(String password) { this.password = password; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public void setPhone(String phone) { this.phone = phone; }
    public void setRole(UserRole role) { this.role = role; }
    public void setTransactionPin(String transactionPin) { this.transactionPin = transactionPin; }
    public void setSecurityQuestion(SecurityQuestion securityQuestion) { this.securityQuestion = securityQuestion; }
    public void setSecurityAnswer(String securityAnswer) { this.securityAnswer = securityAnswer; }
    public void setVerified(boolean verified) { isVerified = verified; }
    public void setActive(boolean active) { isActive = active; }
    public void setBusinessName(String businessName) { this.businessName = businessName; }
    public void setBusinessType(String businessType) { this.businessType = businessType; }
    public void setTaxId(String taxId) { this.taxId = taxId; }
    public void setBusinessAddress(String businessAddress) { this.businessAddress = businessAddress; }
    public void setBusinessContact(String businessContact) { this.businessContact = businessContact; }
    public void setVerificationStatus(BusinessVerificationStatus verificationStatus) { this.verificationStatus = verificationStatus; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", email='" + email + '\'' +
                ", fullName='" + fullName + '\'' +
                ", role=" + role +
                ", active=" + isActive +
                ", verified=" + isVerified +
                '}';
    }
}