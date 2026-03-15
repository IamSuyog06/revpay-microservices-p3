package com.revpay.invoice_service.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class InvoiceResponse {

    private Long id;
    private String invoiceNumber;
    private Long businessUserId;
    private String businessUserName;
    private String customerEmail;
    private String customerName;
    private String customerAddress;
    private Long customerUserId;
    private BigDecimal subtotal;
    private BigDecimal totalTax;
    private BigDecimal totalAmount;
    private String paymentTerms;
    private LocalDate dueDate;
    private String notes;
    private String status;
    private LocalDateTime paidAt;
    private List<InvoiceLineItemResponse> lineItems;
    private LocalDateTime createdAt;

    public InvoiceResponse() {}

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
    public String getStatus() { return status; }
    public LocalDateTime getPaidAt() { return paidAt; }
    public List<InvoiceLineItemResponse> getLineItems() { return lineItems; }
    public LocalDateTime getCreatedAt() { return createdAt; }

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
    public void setStatus(String status) { this.status = status; }
    public void setPaidAt(LocalDateTime paidAt) { this.paidAt = paidAt; }
    public void setLineItems(List<InvoiceLineItemResponse> lineItems) { this.lineItems = lineItems; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}