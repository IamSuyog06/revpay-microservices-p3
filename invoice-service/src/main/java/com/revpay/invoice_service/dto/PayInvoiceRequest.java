package com.revpay.invoice_service.dto;

import jakarta.validation.constraints.NotBlank;

public class PayInvoiceRequest {

    @NotBlank(message = "PIN is required")
    private String pin;

    public PayInvoiceRequest() {}

    // Getter
    public String getPin() { return pin; }

    // Setter
    public void setPin(String pin) { this.pin = pin; }
}