package com.revpay.loan_service.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public class LoanApplicationRequest {

    @NotNull(message = "Loan amount is required")
    @DecimalMin(value = "1000.00",
            message = "Minimum loan amount is 1000")
    private BigDecimal loanAmount;

    @NotBlank(message = "Purpose is required")
    private String purpose;

    @NotNull(message = "Tenure is required")
    @Min(value = 1,
            message = "Minimum tenure is 1 month")
    private Integer tenureMonths;

    @NotBlank(message = "Financial info is required")
    private String financialInfo;

    public LoanApplicationRequest() {}

    // Getters
    public BigDecimal getLoanAmount() { return loanAmount; }
    public String getPurpose() { return purpose; }
    public Integer getTenureMonths() { return tenureMonths; }
    public String getFinancialInfo() { return financialInfo; }

    // Setters
    public void setLoanAmount(BigDecimal loanAmount) { this.loanAmount = loanAmount; }
    public void setPurpose(String purpose) { this.purpose = purpose; }
    public void setTenureMonths(Integer tenureMonths) { this.tenureMonths = tenureMonths; }
    public void setFinancialInfo(String financialInfo) { this.financialInfo = financialInfo; }
}