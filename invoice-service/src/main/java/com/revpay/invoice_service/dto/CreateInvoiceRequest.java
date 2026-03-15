package com.revpay.invoice_service.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.List;

public class CreateInvoiceRequest {

    @NotBlank(message = "Customer email is required")
    @Email(message = "Invalid customer email")
    private String customerEmail;

    private String customerName;
    private String customerAddress;

    @NotNull(message = "Due date is required")
    private LocalDate dueDate;

    private String paymentTerms;
    private String notes;

    @NotEmpty(message = "At least one line item required")
    @Valid
    private List<InvoiceLineItemRequest> lineItems;

    public CreateInvoiceRequest() {}

    // Getters
    public String getCustomerEmail() { return customerEmail; }
    public String getCustomerName() { return customerName; }
    public String getCustomerAddress() { return customerAddress; }
    public LocalDate getDueDate() { return dueDate; }
    public String getPaymentTerms() { return paymentTerms; }
    public String getNotes() { return notes; }
    public List<InvoiceLineItemRequest> getLineItems() { return lineItems; }

    // Setters
    public void setCustomerEmail(String customerEmail) { this.customerEmail = customerEmail; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }
    public void setCustomerAddress(String customerAddress) { this.customerAddress = customerAddress; }
    public void setDueDate(LocalDate dueDate) { this.dueDate = dueDate; }
    public void setPaymentTerms(String paymentTerms) { this.paymentTerms = paymentTerms; }
    public void setNotes(String notes) { this.notes = notes; }
    public void setLineItems(List<InvoiceLineItemRequest> lineItems) { this.lineItems = lineItems; }
}