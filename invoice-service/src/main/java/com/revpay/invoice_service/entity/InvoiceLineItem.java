package com.revpay.invoice_service.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "invoice_line_items")
public class InvoiceLineItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "invoice_id",
            nullable = false)
    private Invoice invoice;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private Integer quantity;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal unitPrice;

    // Tax percentage e.g. 18.0 for 18% GST
    @Column(nullable = false)
    private BigDecimal taxPercent = BigDecimal.ZERO;

    // Calculated: quantity * unitPrice
    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal lineTotal;

    // Calculated: lineTotal * (taxPercent/100)
    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal taxAmount;

    // Calculated: lineTotal + taxAmount
    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal lineTotalWithTax;

    public InvoiceLineItem() {}

    // Getters
    public Long getId() { return id; }
    public Invoice getInvoice() { return invoice; }
    public String getDescription() { return description; }
    public Integer getQuantity() { return quantity; }
    public BigDecimal getUnitPrice() { return unitPrice; }
    public BigDecimal getTaxPercent() { return taxPercent; }
    public BigDecimal getLineTotal() { return lineTotal; }
    public BigDecimal getTaxAmount() { return taxAmount; }
    public BigDecimal getLineTotalWithTax() { return lineTotalWithTax; }

    // Setters
    public void setId(Long id) { this.id = id; }
    public void setInvoice(Invoice invoice) { this.invoice = invoice; }
    public void setDescription(String description) { this.description = description; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }
    public void setUnitPrice(BigDecimal unitPrice) { this.unitPrice = unitPrice; }
    public void setTaxPercent(BigDecimal taxPercent) { this.taxPercent = taxPercent; }
    public void setLineTotal(BigDecimal lineTotal) { this.lineTotal = lineTotal; }
    public void setTaxAmount(BigDecimal taxAmount) { this.taxAmount = taxAmount; }
    public void setLineTotalWithTax(BigDecimal lineTotalWithTax) { this.lineTotalWithTax = lineTotalWithTax; }
}