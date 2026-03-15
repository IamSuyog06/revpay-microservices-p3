package com.revpay.loan_service.dto;

import java.math.BigDecimal;

public class LoanAnalyticsResponse {

    private Integer totalLoans;
    private Integer activeLoans;
    private Integer pendingLoans;
    private Integer closedLoans;
    private Integer rejectedLoans;
    private BigDecimal totalBorrowed;
    private BigDecimal totalRepaid;
    private BigDecimal totalOutstanding;

    public LoanAnalyticsResponse() {}

    // Getters
    public Integer getTotalLoans() { return totalLoans; }
    public Integer getActiveLoans() { return activeLoans; }
    public Integer getPendingLoans() { return pendingLoans; }
    public Integer getClosedLoans() { return closedLoans; }
    public Integer getRejectedLoans() { return rejectedLoans; }
    public BigDecimal getTotalBorrowed() { return totalBorrowed; }
    public BigDecimal getTotalRepaid() { return totalRepaid; }
    public BigDecimal getTotalOutstanding() { return totalOutstanding; }

    // Setters
    public void setTotalLoans(Integer totalLoans) { this.totalLoans = totalLoans; }
    public void setActiveLoans(Integer activeLoans) { this.activeLoans = activeLoans; }
    public void setPendingLoans(Integer pendingLoans) { this.pendingLoans = pendingLoans; }
    public void setClosedLoans(Integer closedLoans) { this.closedLoans = closedLoans; }
    public void setRejectedLoans(Integer rejectedLoans) { this.rejectedLoans = rejectedLoans; }
    public void setTotalBorrowed(BigDecimal totalBorrowed) { this.totalBorrowed = totalBorrowed; }
    public void setTotalRepaid(BigDecimal totalRepaid) { this.totalRepaid = totalRepaid; }
    public void setTotalOutstanding(BigDecimal totalOutstanding) { this.totalOutstanding = totalOutstanding; }
}