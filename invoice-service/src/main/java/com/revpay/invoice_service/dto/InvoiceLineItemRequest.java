package com.revpay.invoice_service.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public class InvoiceLineItemRequest {

    @NotBlank(message = "Description is required")
    private String description;

    @NotNull(message = "Quantity is required")
    @Min(value = 1, message = "Quantity must be at least 1")
    private Integer quantity;

    @NotNull(message = "Unit price is required")
    @DecimalMin(value = "0.01",
            message = "Unit price must be greater than 0")
    private BigDecimal unitPrice;

    // Tax percentage 0-100
    private BigDecimal taxPercent = BigDecimal.ZERO;

    public InvoiceLineItemRequest() {}

    // Getters
    public String getDescription() { return description; }
    public Integer getQuantity() { return quantity; }
    public BigDecimal getUnitPrice() { return unitPrice; }
    public BigDecimal getTaxPercent() { return taxPercent; }

    // Setters
    public void setDescription(String description) { this.description = description; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }
    public void setUnitPrice(BigDecimal unitPrice) { this.unitPrice = unitPrice; }
    public void setTaxPercent(BigDecimal taxPercent) { this.taxPercent = taxPercent; }
}