package com.revpay.invoice_service.controller;

import com.revpay.invoice_service.dto.CreateInvoiceRequest;
import com.revpay.invoice_service.dto.InvoiceResponse;
import com.revpay.invoice_service.dto.PayInvoiceRequest;
import com.revpay.invoice_service.enums.InvoiceStatus;
import com.revpay.invoice_service.service.InvoiceService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/invoices")
public class InvoiceController {

    private final InvoiceService invoiceService;

    public InvoiceController(
            InvoiceService invoiceService) {
        this.invoiceService = invoiceService;
    }

    // Business creates invoice
    @PostMapping("/{businessUserId}/create")
    public ResponseEntity<InvoiceResponse> createInvoice(
            @PathVariable Long businessUserId,
            @Valid @RequestBody
            CreateInvoiceRequest request) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(invoiceService.createInvoice(
                        businessUserId, request));
    }

    // Business sends invoice to customer
    @PutMapping("/{businessUserId}/send/{invoiceId}")
    public ResponseEntity<InvoiceResponse> sendInvoice(
            @PathVariable Long businessUserId,
            @PathVariable Long invoiceId) {
        return ResponseEntity.ok(
                invoiceService.sendInvoice(
                        businessUserId, invoiceId));
    }

    // Customer pays invoice
    @PostMapping("/{customerUserId}/pay/{invoiceId}")
    public ResponseEntity<InvoiceResponse> payInvoice(
            @PathVariable Long customerUserId,
            @PathVariable Long invoiceId,
            @Valid @RequestBody
            PayInvoiceRequest request) {
        return ResponseEntity.ok(
                invoiceService.payInvoice(
                        customerUserId, invoiceId, request));
    }

    // Business cancels invoice
    @PutMapping("/{businessUserId}/cancel/{invoiceId}")
    public ResponseEntity<InvoiceResponse> cancelInvoice(
            @PathVariable Long businessUserId,
            @PathVariable Long invoiceId) {
        return ResponseEntity.ok(
                invoiceService.cancelInvoice(
                        businessUserId, invoiceId));
    }

    // Business views all their invoices
    @GetMapping("/{businessUserId}/business")
    public ResponseEntity<List<InvoiceResponse>>
    getBusinessInvoices(
            @PathVariable Long businessUserId) {
        return ResponseEntity.ok(
                invoiceService.getBusinessInvoices(
                        businessUserId));
    }

    // Business views invoices by status
    @GetMapping("/{businessUserId}/business/status/{status}")
    public ResponseEntity<List<InvoiceResponse>>
    getByStatus(
            @PathVariable Long businessUserId,
            @PathVariable InvoiceStatus status) {
        return ResponseEntity.ok(
                invoiceService.getBusinessInvoicesByStatus(
                        businessUserId, status));
    }

    // Customer views invoices sent to them
    @GetMapping("/customer/{email}")
    public ResponseEntity<List<InvoiceResponse>>
    getCustomerInvoices(
            @PathVariable String email) {
        return ResponseEntity.ok(
                invoiceService.getCustomerInvoices(email));
    }

    // Get single invoice by ID
    @GetMapping("/{invoiceId}")
    public ResponseEntity<InvoiceResponse> getById(
            @PathVariable Long invoiceId) {
        return ResponseEntity.ok(
                invoiceService.getInvoiceById(invoiceId));
    }
}