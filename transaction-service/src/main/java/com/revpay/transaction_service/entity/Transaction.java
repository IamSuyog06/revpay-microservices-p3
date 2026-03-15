package com.revpay.transaction_service.entity;

import com.revpay.transaction_service.enums.TransactionStatus;
import com.revpay.transaction_service.enums.TransactionType;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "transactions")
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String transactionId;

    @Column(nullable = false)
    private Long senderUserId;

    @Column(nullable = false)
    private Long receiverUserId;

    // Store names for display purposes
    private String senderName;
    private String receiverName;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransactionType type;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransactionStatus status;

    private String note;

    private String failureReason;

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

    public Transaction() {}

    // Getters
    public Long getId() { return id; }
    public String getTransactionId() { return transactionId; }
    public Long getSenderUserId() { return senderUserId; }
    public Long getReceiverUserId() { return receiverUserId; }
    public String getSenderName() { return senderName; }
    public String getReceiverName() { return receiverName; }
    public BigDecimal getAmount() { return amount; }
    public TransactionType getType() { return type; }
    public TransactionStatus getStatus() { return status; }
    public String getNote() { return note; }
    public String getFailureReason() { return failureReason; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }

    // Setters
    public void setId(Long id) { this.id = id; }
    public void setTransactionId(String transactionId) { this.transactionId = transactionId; }
    public void setSenderUserId(Long senderUserId) { this.senderUserId = senderUserId; }
    public void setReceiverUserId(Long receiverUserId) { this.receiverUserId = receiverUserId; }
    public void setSenderName(String senderName) { this.senderName = senderName; }
    public void setReceiverName(String receiverName) { this.receiverName = receiverName; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
    public void setType(TransactionType type) { this.type = type; }
    public void setStatus(TransactionStatus status) { this.status = status; }
    public void setNote(String note) { this.note = note; }
    public void setFailureReason(String failureReason) { this.failureReason = failureReason; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    @Override
    public String toString() {
        return "Transaction{" +
                "transactionId='" + transactionId + '\'' +
                ", senderUserId=" + senderUserId +
                ", receiverUserId=" + receiverUserId +
                ", amount=" + amount +
                ", type=" + type +
                ", status=" + status +
                '}';
    }
}