package com.revpay.transaction_service.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class MoneyRequestResponse {

    private Long id;
    private Long requesterId;
    private Long requestedFromId;
    private String requesterName;
    private String requestedFromName;
    private BigDecimal amount;
    private String purpose;
    private String status;
    private LocalDateTime createdAt;

    public MoneyRequestResponse() {}

    // Getters
    public Long getId() { return id; }
    public Long getRequesterId() { return requesterId; }
    public Long getRequestedFromId() { return requestedFromId; }
    public String getRequesterName() { return requesterName; }
    public String getRequestedFromName() { return requestedFromName; }
    public BigDecimal getAmount() { return amount; }
    public String getPurpose() { return purpose; }
    public String getStatus() { return status; }
    public LocalDateTime getCreatedAt() { return createdAt; }

    // Setters
    public void setId(Long id) { this.id = id; }
    public void setRequesterId(Long requesterId) { this.requesterId = requesterId; }
    public void setRequestedFromId(Long requestedFromId) { this.requestedFromId = requestedFromId; }
    public void setRequesterName(String requesterName) { this.requesterName = requesterName; }
    public void setRequestedFromName(String requestedFromName) { this.requestedFromName = requestedFromName; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
    public void setPurpose(String purpose) { this.purpose = purpose; }
    public void setStatus(String status) { this.status = status; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}