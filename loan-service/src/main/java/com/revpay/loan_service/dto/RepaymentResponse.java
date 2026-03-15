package com.revpay.loan_service.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class RepaymentResponse {

    private Long id;
    private Long loanId;
    private BigDecimal amount;
    private BigDecimal remainingAfterPayment;
    private String status;
    private LocalDateTime paidAt;

    public RepaymentResponse() {}

    // Getters
    public Long getId() { return id; }
    public Long getLoanId() { return loanId; }
    public BigDecimal getAmount() { return amount; }
    public BigDecimal getRemainingAfterPayment() { return remainingAfterPayment; }
    public String getStatus() { return status; }
    public LocalDateTime getPaidAt() { return paidAt; }

    // Setters
    public void setId(Long id) { this.id = id; }
    public void setLoanId(Long loanId) { this.loanId = loanId; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
    public void setRemainingAfterPayment(BigDecimal remainingAfterPayment) { this.remainingAfterPayment = remainingAfterPayment; }
    public void setStatus(String status) { this.status = status; }
    public void setPaidAt(LocalDateTime paidAt) { this.paidAt = paidAt; }
}