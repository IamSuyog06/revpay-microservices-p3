package com.revpay.invoice_service.entity;

import com.revpay.invoice_service.enums.InvoiceStatus;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "invoices")
public class Invoice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String invoiceNumber;

    // Business user who created the invoice
    @Column(nullable = false)
    private Long businessUserId;

    private String businessUserName;

    // Customer details
    @Column(nullable = false)
    private String customerEmail;

    private String customerName;
    private String customerAddress;

    // Customer userId if they are a RevPay user
    private Long customerUserId;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal subtotal = BigDecimal.ZERO;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal totalTax = BigDecimal.ZERO;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal totalAmount = BigDecimal.ZERO;

    private String paymentTerms;

    @Column(nullable = false)
    private LocalDate dueDate;

    private String notes;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private InvoiceStatus status = InvoiceStatus.DRAFT;

    private LocalDateTime paidAt;

    @OneToMany(mappedBy = "invoice",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.EAGER)
    private List<InvoiceLineItem> lineItems =
            new ArrayList<>();

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

    public Invoice() {}

    // Getters
    public Long getId() { return id; }
    public String getInvoiceNumber() { return invoiceNumber; }
    public Long getBusinessUserId() { return businessUserId; }
    public String getBusinessUserName() { return businessUserName; }
    public String getCustomerEmail() { return customerEmail; }
    public String getCustomerName() { return customerName; }
    public String getCustomerAddress() { return customerAddress; }
    public Long getCustomerUserId() { return customerUserId; }
    public BigDecimal getSubtotal() { return subtotal; }
    public BigDecimal getTotalTax() { return totalTax; }
    public BigDecimal getTotalAmount() { return totalAmount; }
    public String getPaymentTerms() { return paymentTerms; }
    public LocalDate getDueDate() { return dueDate; }
    public String getNotes() { return notes; }
    public InvoiceStatus getStatus() { return status; }
    public LocalDateTime getPaidAt() { return paidAt; }
    public List<InvoiceLineItem> getLineItems() { return lineItems; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }

    // Setters
    public void setId(Long id) { this.id = id; }
    public void setInvoiceNumber(String invoiceNumber) { this.invoiceNumber = invoiceNumber; }
    public void setBusinessUserId(Long businessUserId) { this.businessUserId = businessUserId; }
    public void setBusinessUserName(String businessUserName) { this.businessUserName = businessUserName; }
    public void setCustomerEmail(String customerEmail) { this.customerEmail = customerEmail; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }
    public void setCustomerAddress(String customerAddress) { this.customerAddress = customerAddress; }
    public void setCustomerUserId(Long customerUserId) { this.customerUserId = customerUserId; }
    public void setSubtotal(BigDecimal subtotal) { this.subtotal = subtotal; }
    public void setTotalTax(BigDecimal totalTax) { this.totalTax = totalTax; }
    public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }
    public void setPaymentTerms(String paymentTerms) { this.paymentTerms = paymentTerms; }
    public void setDueDate(LocalDate dueDate) { this.dueDate = dueDate; }
    public void setNotes(String notes) { this.notes = notes; }
    public void setStatus(InvoiceStatus status) { this.status = status; }
    public void setPaidAt(LocalDateTime paidAt) { this.paidAt = paidAt; }
    public void setLineItems(List<InvoiceLineItem> lineItems) { this.lineItems = lineItems; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    @Override
    public String toString() {
        return "Invoice{" +
                "invoiceNumber='" + invoiceNumber + '\'' +
                ", businessUserId=" + businessUserId +
                ", customerEmail='" + customerEmail + '\'' +
                ", totalAmount=" + totalAmount +
                ", status=" + status +
                '}';
    }
}