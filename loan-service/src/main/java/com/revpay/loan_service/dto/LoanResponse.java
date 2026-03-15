package com.revpay.loan_service.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class LoanResponse {

    private Long id;
    private Long userId;
    private String userName;
    private BigDecimal loanAmount;
    private String purpose;
    private Integer tenureMonths;
    private BigDecimal interestRate;
    private BigDecimal emiAmount;
    private BigDecimal totalRepayableAmount;
    private BigDecimal remainingAmount;
    private String financialInfo;
    private String status;
    private String rejectionReason;
    private LocalDateTime approvedAt;
    private LocalDateTime rejectedAt;
    private List<DocumentResponse> documents;
    private LocalDateTime createdAt;

    public LoanResponse() {}

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
    public String getStatus() { return status; }
    public String getRejectionReason() { return rejectionReason; }
    public LocalDateTime getApprovedAt() { return approvedAt; }
    public LocalDateTime getRejectedAt() { return rejectedAt; }
    public List<DocumentResponse> getDocuments() { return documents; }
    public LocalDateTime getCreatedAt() { return createdAt; }

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
    public void setStatus(String status) { this.status = status; }
    public void setRejectionReason(String rejectionReason) { this.rejectionReason = rejectionReason; }
    public void setApprovedAt(LocalDateTime approvedAt) { this.approvedAt = approvedAt; }
    public void setRejectedAt(LocalDateTime rejectedAt) { this.rejectedAt = rejectedAt; }
    public void setDocuments(List<DocumentResponse> documents) { this.documents = documents; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}