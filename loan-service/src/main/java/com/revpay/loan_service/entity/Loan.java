package com.revpay.loan_service.entity;

import com.revpay.loan_service.enums.LoanStatus;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "loans")
public class Loan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long userId;

    private String userName;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal loanAmount;

    @Column(nullable = false)
    private String purpose;

    // Tenure in months
    @Column(nullable = false)
    private Integer tenureMonths;

    // Fixed 10% per annum
    @Column(nullable = false, precision = 5, scale = 2)
    private BigDecimal interestRate =
            new BigDecimal("10.00");

    // Calculated EMI using reducing balance
    @Column(precision = 15, scale = 2)
    private BigDecimal emiAmount;

    // Total amount to be repaid
    @Column(precision = 15, scale = 2)
    private BigDecimal totalRepayableAmount;

    // Remaining principal
    @Column(precision = 15, scale = 2)
    private BigDecimal remainingAmount;

    @Column(nullable = false)
    private String financialInfo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private LoanStatus status = LoanStatus.PENDING;

    private String rejectionReason;

    private LocalDateTime approvedAt;
    private LocalDateTime rejectedAt;

    @OneToMany(mappedBy = "loan",
            cascade = CascadeType.ALL,
            fetch = FetchType.LAZY)
    private List<LoanDocument> documents =
            new ArrayList<>();

    @OneToMany(mappedBy = "loan",
            cascade = CascadeType.ALL,
            fetch = FetchType.LAZY)
    private List<LoanRepayment> repayments =
            new ArrayList<>();

    @Column(updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public Loan() {}

    // Getters
    public Long getId() { return id; }
    public Long getUserId() { return userId; }
    public String getUserName() { return userName; }
    public BigDecimal getLoanAmount() { return loanAmount; }
    public String getPurpose() { return purpose; }
    public Integer getTenureMonths() { return tenureMonths; }
    public BigDecimal getInterestRate() { return interestRate; }
    public BigDecimal getEmiAmount() { return emiAmount; }
    public BigDecimal getTotalRepayableAmount() { return totalRepayableAmount; }
    public BigDecimal getRemainingAmount() { return remainingAmount; }
    public String getFinancialInfo() { return financialInfo; }
    public LoanStatus getStatus() { return status; }
    public String getRejectionReason() { return rejectionReason; }
    public LocalDateTime getApprovedAt() { return approvedAt; }
    public LocalDateTime getRejectedAt() { return rejectedAt; }
    public List<LoanDocument> getDocuments() { return documents; }
    public List<LoanRepayment> getRepayments() { return repayments; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }

    // Setters
    public void setId(Long id) { this.id = id; }
    public void setUserId(Long userId) { this.userId = userId; }
    public void setUserName(String userName) { this.userName = userName; }
    public void setLoanAmount(BigDecimal loanAmount) { this.loanAmount = loanAmount; }
    public void setPurpose(String purpose) { this.purpose = purpose; }
    public void setTenureMonths(Integer tenureMonths) { this.tenureMonths = tenureMonths; }
    public void setInterestRate(BigDecimal interestRate) { this.interestRate = interestRate; }
    public void setEmiAmount(BigDecimal emiAmount) { this.emiAmount = emiAmount; }
    public void setTotalRepayableAmount(BigDecimal totalRepayableAmount) { this.totalRepayableAmount = totalRepayableAmount; }
    public void setRemainingAmount(BigDecimal remainingAmount) { this.remainingAmount = remainingAmount; }
    public void setFinancialInfo(String financialInfo) { this.financialInfo = financialInfo; }
    public void setStatus(LoanStatus status) { this.status = status; }
    public void setRejectionReason(String rejectionReason) { this.rejectionReason = rejectionReason; }
    public void setApprovedAt(LocalDateTime approvedAt) { this.approvedAt = approvedAt; }
    public void setRejectedAt(LocalDateTime rejectedAt) { this.rejectedAt = rejectedAt; }
    public void setDocuments(List<LoanDocument> documents) { this.documents = documents; }
    public void setRepayments(List<LoanRepayment> repayments) { this.repayments = repayments; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    @Override
    public String toString() {
        return "Loan{" +
                "id=" + id +
                ", userId=" + userId +
                ", loanAmount=" + loanAmount +
                ", status=" + status +
                '}';
    }
}