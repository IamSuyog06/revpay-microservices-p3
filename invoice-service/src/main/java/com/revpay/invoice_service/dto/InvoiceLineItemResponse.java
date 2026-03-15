package com.revpay.invoice_service.dto;

import java.math.BigDecimal;

public class InvoiceLineItemResponse {

    private Long id;
    private String description;
    private Integer quantity;
    private BigDecimal unitPrice;
    private BigDecimal taxPercent;
    private BigDecimal lineTotal;
    private BigDecimal taxAmount;
    private BigDecimal lineTotalWithTax;

    public InvoiceLineItemResponse() {}

    // Getters
    public Long getId() { return id; }
    public String getDescription() { return description; }
    public Integer getQuantity() { return quantity; }
    public BigDecimal getUnitPrice() { return unitPrice; }
    public BigDecimal getTaxPercent() { return taxPercent; }
    public BigDecimal getLineTotal() { return lineTotal; }
    public BigDecimal getTaxAmount() { return taxAmount; }
    public BigDecimal getLineTotalWithTax() { return lineTotalWithTax; }

    // Setters
    public void setId(Long id) { this.id = id; }
    public void setDescription(String description) { this.description = description; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }
    public void setUnitPrice(BigDecimal unitPrice) { this.unitPrice = unitPrice; }
    public void setTaxPercent(BigDecimal taxPercent) { this.taxPercent = taxPercent; }
    public void setLineTotal(BigDecimal lineTotal) { this.lineTotal = lineTotal; }
    public void setTaxAmount(BigDecimal taxAmount) { this.taxAmount = taxAmount; }
    public void setLineTotalWithTax(BigDecimal lineTotalWithTax) { this.lineTotalWithTax = lineTotalWithTax; }
}