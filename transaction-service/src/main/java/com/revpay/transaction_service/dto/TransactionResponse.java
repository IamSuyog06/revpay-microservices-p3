package com.revpay.transaction_service.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class TransactionResponse {

    private Long id;
    private String transactionId;
    private Long senderUserId;
    private Long receiverUserId;
    private String senderName;
    private String receiverName;
    private BigDecimal amount;
    private String type;
    private String status;
    private String note;
    private String failureReason;
    private LocalDateTime createdAt;

    public TransactionResponse() {}

    // Getters
    public Long getId() { return id; }
    public String getTransactionId() { return transactionId; }
    public Long getSenderUserId() { return senderUserId; }
    public Long getReceiverUserId() { return receiverUserId; }
    public String getSenderName() { return senderName; }
    public String getReceiverName() { return receiverName; }
    public BigDecimal getAmount() { return amount; }
    public String getType() { return type; }
    public String getStatus() { return status; }
    public String getNote() { return note; }
    public String getFailureReason() { return failureReason; }
    public LocalDateTime getCreatedAt() { return createdAt; }

    // Setters
    public void setId(Long id) { this.id = id; }
    public void setTransactionId(String transactionId) { this.transactionId = transactionId; }
    public void setSenderUserId(Long senderUserId) { this.senderUserId = senderUserId; }
    public void setReceiverUserId(Long receiverUserId) { this.receiverUserId = receiverUserId; }
    public void setSenderName(String senderName) { this.senderName = senderName; }
    public void setReceiverName(String receiverName) { this.receiverName = receiverName; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
    public void setType(String type) { this.type = type; }
    public void setStatus(String status) { this.status = status; }
    public void setNote(String note) { this.note = note; }
    public void setFailureReason(String failureReason) { this.failureReason = failureReason; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}