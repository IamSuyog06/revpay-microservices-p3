package com.revpay.loan_service.dto;

import java.math.BigDecimal;

public class EMIScheduleResponse {

    private Integer installmentNumber;
    private BigDecimal emiAmount;
    private BigDecimal principalComponent;
    private BigDecimal interestComponent;
    private BigDecimal remainingBalance;

    public EMIScheduleResponse() {}

    // Getters
    public Integer getInstallmentNumber() { return installmentNumber; }
    public BigDecimal getEmiAmount() { return emiAmount; }
    public BigDecimal getPrincipalComponent() { return principalComponent; }
    public BigDecimal getInterestComponent() { return interestComponent; }
    public BigDecimal getRemainingBalance() { return remainingBalance; }

    // Setters
    public void setInstallmentNumber(Integer installmentNumber) { this.installmentNumber = installmentNumber; }
    public void setEmiAmount(BigDecimal emiAmount) { this.emiAmount = emiAmount; }
    public void setPrincipalComponent(BigDecimal principalComponent) { this.principalComponent = principalComponent; }
    public void setInterestComponent(BigDecimal interestComponent) { this.interestComponent = interestComponent; }
    public void setRemainingBalance(BigDecimal remainingBalance) { this.remainingBalance = remainingBalance; }
}