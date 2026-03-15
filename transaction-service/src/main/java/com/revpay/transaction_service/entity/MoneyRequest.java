package com.revpay.transaction_service.entity;

import com.revpay.transaction_service.enums.MoneyRequestStatus;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "money_requests")
public class MoneyRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // User who is requesting money
    @Column(nullable = false)
    private Long requesterId;

    // User who needs to pay
    @Column(nullable = false)
    private Long requestedFromId;

    private String requesterName;
    private String requestedFromName;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal amount;

    private String purpose;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MoneyRequestStatus status;

    @Column(updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        status = MoneyRequestStatus.PENDING;
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public MoneyRequest() {}

    // Getters
    public Long getId() { return id; }
    public Long getRequesterId() { return requesterId; }
    public Long getRequestedFromId() { return requestedFromId; }
    public String getRequesterName() { return requesterName; }
    public String getRequestedFromName() { return requestedFromName; }
    public BigDecimal getAmount() { return amount; }
    public String getPurpose() { return purpose; }
    public MoneyRequestStatus getStatus() { return status; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }

    // Setters
    public void setId(Long id) { this.id = id; }
    public void setRequesterId(Long requesterId) { this.requesterId = requesterId; }
    public void setRequestedFromId(Long requestedFromId) { this.requestedFromId = requestedFromId; }
    public void setRequesterName(String requesterName) { this.requesterName = requesterName; }
    public void setRequestedFromName(String requestedFromName) { this.requestedFromName = requestedFromName; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
    public void setPurpose(String purpose) { this.purpose = purpose; }
    public void setStatus(MoneyRequestStatus status) { this.status = status; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    @Override
    public String toString() {
        return "MoneyRequest{" +
                "id=" + id +
                ", requesterId=" + requesterId +
                ", requestedFromId=" + requestedFromId +
                ", amount=" + amount +
                ", status=" + status +
                '}';
    }
}