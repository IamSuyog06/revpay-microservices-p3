package com.revpay.transaction_service.dto;

import com.revpay.transaction_service.enums.TransactionStatus;
import com.revpay.transaction_service.enums.TransactionType;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public class TransactionFilterRequest {

    private TransactionType type;
    private TransactionStatus status;
    private LocalDateTime fromDate;
    private LocalDateTime toDate;
    private BigDecimal minAmount;
    private BigDecimal maxAmount;

    public TransactionFilterRequest() {}

    // Getters
    public TransactionType getType() { return type; }
    public TransactionStatus getStatus() { return status; }
    public LocalDateTime getFromDate() { return fromDate; }
    public LocalDateTime getToDate() { return toDate; }
    public BigDecimal getMinAmount() { return minAmount; }
    public BigDecimal getMaxAmount() { return maxAmount; }

    // Setters
    public void setType(TransactionType type) { this.type = type; }
    public void setStatus(TransactionStatus status) { this.status = status; }
    public void setFromDate(LocalDateTime fromDate) { this.fromDate = fromDate; }
    public void setToDate(LocalDateTime toDate) { this.toDate = toDate; }
    public void setMinAmount(BigDecimal minAmount) { this.minAmount = minAmount; }
    public void setMaxAmount(BigDecimal maxAmount) { this.maxAmount = maxAmount; }
}