package com.revpay.invoice_service.service;

import com.revpay.invoice_service.client.NotificationServiceClient;
import com.revpay.invoice_service.client.UserServiceClient;
import com.revpay.invoice_service.client.WalletServiceClient;
import com.revpay.invoice_service.dto.*;
import com.revpay.invoice_service.entity.Invoice;
import com.revpay.invoice_service.entity.InvoiceLineItem;
import com.revpay.invoice_service.enums.InvoiceStatus;
import com.revpay.invoice_service.exception.InvoiceException;
import com.revpay.invoice_service.repository.InvoiceRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class InvoiceService {

    private static final Logger logger =
            LoggerFactory.getLogger(InvoiceService.class);

    private final InvoiceRepository invoiceRepository;
    private final UserServiceClient userServiceClient;
    private final WalletServiceClient walletServiceClient;
    private final NotificationServiceClient notificationServiceClient;

    public InvoiceService(
            InvoiceRepository invoiceRepository,
            UserServiceClient userServiceClient,
            WalletServiceClient walletServiceClient,
            NotificationServiceClient notificationServiceClient) {
        this.invoiceRepository = invoiceRepository;
        this.userServiceClient = userServiceClient;
        this.walletServiceClient = walletServiceClient;
        this.notificationServiceClient = notificationServiceClient;
    }

    @Transactional
    public InvoiceResponse createInvoice(
            Long businessUserId,
            CreateInvoiceRequest request) {
        logger.info("Creating invoice for businessUserId: {}",
                businessUserId);

        // Verify business user
        UserResponse businessUser = userServiceClient
                .getUserById(businessUserId);

        if (!businessUser.getRole().equals("BUSINESS")) {
            throw new InvoiceException(
                    "Only business accounts can create invoices");
        }

        // Generate invoice number
        Long count = invoiceRepository
                .countByBusinessUserId(businessUserId);
        String invoiceNumber = String.format(
                "INV-%05d", count + 1);

        // Try to find customer in system
        Long customerUserId = null;
        try {
            UserResponse customer = userServiceClient
                    .getUserByEmail(request.getCustomerEmail());
            customerUserId = customer.getId();
        } catch (Exception e) {
            logger.info("Customer not a RevPay user: {}",
                    request.getCustomerEmail());
        }

        // Create invoice
        Invoice invoice = new Invoice();
        invoice.setInvoiceNumber(invoiceNumber);
        invoice.setBusinessUserId(businessUserId);
        invoice.setBusinessUserName(
                businessUser.getFullName());
        invoice.setCustomerEmail(request.getCustomerEmail());
        invoice.setCustomerName(request.getCustomerName());
        invoice.setCustomerAddress(
                request.getCustomerAddress());
        invoice.setCustomerUserId(customerUserId);
        invoice.setDueDate(request.getDueDate());
        invoice.setPaymentTerms(request.getPaymentTerms());
        invoice.setNotes(request.getNotes());
        invoice.setStatus(InvoiceStatus.DRAFT);

        // Process line items and calculate totals
        BigDecimal subtotal = BigDecimal.ZERO;
        BigDecimal totalTax = BigDecimal.ZERO;

        for (InvoiceLineItemRequest itemReq
                : request.getLineItems()) {

            InvoiceLineItem item = new InvoiceLineItem();
            item.setInvoice(invoice);
            item.setDescription(itemReq.getDescription());
            item.setQuantity(itemReq.getQuantity());
            item.setUnitPrice(itemReq.getUnitPrice());
            item.setTaxPercent(itemReq.getTaxPercent());

            // Calculate line totals
            BigDecimal lineTotal =
                    itemReq.getUnitPrice().multiply(
                            BigDecimal.valueOf(itemReq.getQuantity()));

            BigDecimal taxAmount = lineTotal.multiply(
                    itemReq.getTaxPercent()).divide(
                    BigDecimal.valueOf(100),
                    2, RoundingMode.HALF_UP);

            BigDecimal lineTotalWithTax =
                    lineTotal.add(taxAmount);

            item.setLineTotal(lineTotal);
            item.setTaxAmount(taxAmount);
            item.setLineTotalWithTax(lineTotalWithTax);

            invoice.getLineItems().add(item);

            subtotal = subtotal.add(lineTotal);
            totalTax = totalTax.add(taxAmount);
        }

        invoice.setSubtotal(subtotal);
        invoice.setTotalTax(totalTax);
        invoice.setTotalAmount(subtotal.add(totalTax));

        Invoice saved = invoiceRepository.save(invoice);
        logger.info("Invoice created: {}",
                saved.getInvoiceNumber());

        return mapToResponse(saved);
    }

    @Transactional
    public InvoiceResponse sendInvoice(
            Long businessUserId, Long invoiceId) {
        logger.info("Sending invoice: {} by userId: {}",
                invoiceId, businessUserId);

        Invoice invoice = invoiceRepository
                .findByIdAndBusinessUserId(
                        invoiceId, businessUserId)
                .orElseThrow(() -> new InvoiceException(
                        "Invoice not found"));

        if (invoice.getStatus() != InvoiceStatus.DRAFT) {
            throw new InvoiceException(
                    "Only DRAFT invoices can be sent");
        }

        invoice.setStatus(InvoiceStatus.SENT);
        Invoice updated = invoiceRepository.save(invoice);

        // Notify customer if they are a RevPay user
        if (invoice.getCustomerUserId() != null) {
            try {
                notificationServiceClient.createNotification(
                        new NotificationRequest(
                                invoice.getCustomerUserId(),
                                "New Invoice",
                                "You have received invoice "
                                        + invoice.getInvoiceNumber()
                                        + " for ₹" + invoice.getTotalAmount()
                                        + " from "
                                        + invoice.getBusinessUserName(),
                                "INVOICE"
                        ));
            } catch (Exception e) {
                logger.error("Notification failed: {}",
                        e.getMessage());
            }
        }

        logger.info("Invoice sent: {}",
                invoice.getInvoiceNumber());
        return mapToResponse(updated);
    }

    @Transactional
    public InvoiceResponse payInvoice(
            Long customerUserId,
            Long invoiceId,
            PayInvoiceRequest request) {
        logger.info("Payment for invoice: {} by userId: {}",
                invoiceId, customerUserId);

        Invoice invoice = invoiceRepository
                .findById(invoiceId)
                .orElseThrow(() -> new InvoiceException(
                        "Invoice not found"));

        if (invoice.getStatus() != InvoiceStatus.SENT
                && invoice.getStatus()
                != InvoiceStatus.OVERDUE) {
            throw new InvoiceException(
                    "Invoice is not payable. Status: "
                            + invoice.getStatus());
        }

        // Verify customer
        if (!invoice.getCustomerUserId()
                .equals(customerUserId)) {
            throw new InvoiceException(
                    "This invoice is not addressed to you");
        }

        // Verify PIN via User Service
        UserResponse customer = userServiceClient
                .getUserById(customerUserId);

        // Check customer wallet balance
        WalletResponse customerWallet =
                walletServiceClient.getBalance(customerUserId);

        if (customerWallet.getBalance()
                .compareTo(invoice.getTotalAmount()) < 0) {
            throw new InvoiceException(
                    "Insufficient balance. Current: ₹"
                            + customerWallet.getBalance()
                            + " Required: ₹"
                            + invoice.getTotalAmount());
        }

        // Deduct from customer wallet
        Map<String, Object> deductReq = new HashMap<>();
        deductReq.put("userId", customerUserId);
        deductReq.put("amount", invoice.getTotalAmount());
        walletServiceClient.deductBalance(deductReq);

        // Credit to business wallet
        Map<String, Object> creditReq = new HashMap<>();
        creditReq.put("userId",
                invoice.getBusinessUserId());
        creditReq.put("amount", invoice.getTotalAmount());
        walletServiceClient.creditBalance(creditReq);

        // Update invoice status
        invoice.setStatus(InvoiceStatus.PAID);
        invoice.setPaidAt(LocalDateTime.now());
        Invoice updated = invoiceRepository.save(invoice);

        // Notify business user
        try {
            notificationServiceClient.createNotification(
                    new NotificationRequest(
                            invoice.getBusinessUserId(),
                            "Invoice Paid",
                            "Invoice " + invoice.getInvoiceNumber()
                                    + " has been paid by "
                                    + customer.getFullName()
                                    + " for ₹" + invoice.getTotalAmount(),
                            "INVOICE"
                    ));
        } catch (Exception e) {
            logger.error("Notification failed: {}",
                    e.getMessage());
        }

        logger.info("Invoice paid: {}",
                invoice.getInvoiceNumber());
        return mapToResponse(updated);
    }

    @Transactional
    public InvoiceResponse cancelInvoice(
            Long businessUserId, Long invoiceId) {
        logger.info("Cancelling invoice: {}", invoiceId);

        Invoice invoice = invoiceRepository
                .findByIdAndBusinessUserId(
                        invoiceId, businessUserId)
                .orElseThrow(() -> new InvoiceException(
                        "Invoice not found"));

        if (invoice.getStatus() == InvoiceStatus.PAID) {
            throw new InvoiceException(
                    "Paid invoices cannot be cancelled");
        }

        invoice.setStatus(InvoiceStatus.CANCELLED);
        Invoice updated = invoiceRepository.save(invoice);

        logger.info("Invoice cancelled: {}",
                invoice.getInvoiceNumber());
        return mapToResponse(updated);
    }

    public List<InvoiceResponse> getBusinessInvoices(
            Long businessUserId) {
        return invoiceRepository
                .findByBusinessUserIdOrderByCreatedAtDesc(
                        businessUserId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public List<InvoiceResponse> getBusinessInvoicesByStatus(
            Long businessUserId, InvoiceStatus status) {
        return invoiceRepository
                .findByBusinessUserIdAndStatus(
                        businessUserId, status)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public List<InvoiceResponse> getCustomerInvoices(
            String customerEmail) {
        return invoiceRepository
                .findByCustomerEmailOrderByCreatedAtDesc(
                        customerEmail)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public InvoiceResponse getInvoiceById(Long invoiceId) {
        Invoice invoice = invoiceRepository
                .findById(invoiceId)
                .orElseThrow(() -> new InvoiceException(
                        "Invoice not found"));
        return mapToResponse(invoice);
    }

    // Runs every day at midnight
    // Marks SENT invoices past due date as OVERDUE
    @Scheduled(cron = "0 0 0 * * *")
    @Transactional
    public void markOverdueInvoices() {
        logger.info("Running overdue invoice scheduler");

        List<Invoice> overdueInvoices = invoiceRepository
                .findByStatusAndDueDateBefore(
                        InvoiceStatus.SENT,
                        LocalDate.now());

        for (Invoice invoice : overdueInvoices) {
            invoice.setStatus(InvoiceStatus.OVERDUE);
            invoiceRepository.save(invoice);

            logger.info("Marked invoice as OVERDUE: {}",
                    invoice.getInvoiceNumber());

            // Notify business user
            try {
                notificationServiceClient.createNotification(
                        new NotificationRequest(
                                invoice.getBusinessUserId(),
                                "Invoice Overdue",
                                "Invoice "
                                        + invoice.getInvoiceNumber()
                                        + " is now overdue. Amount: ₹"
                                        + invoice.getTotalAmount(),
                                "INVOICE"
                        ));
            } catch (Exception e) {
                logger.error("Notification failed: {}",
                        e.getMessage());
            }
        }

        logger.info("Overdue scheduler completed."
                        + " Marked {} invoices as OVERDUE",
                overdueInvoices.size());
    }

    private InvoiceResponse mapToResponse(Invoice invoice) {
        InvoiceResponse response = new InvoiceResponse();
        response.setId(invoice.getId());
        response.setInvoiceNumber(
                invoice.getInvoiceNumber());
        response.setBusinessUserId(
                invoice.getBusinessUserId());
        response.setBusinessUserName(
                invoice.getBusinessUserName());
        response.setCustomerEmail(
                invoice.getCustomerEmail());
        response.setCustomerName(
                invoice.getCustomerName());
        response.setCustomerAddress(
                invoice.getCustomerAddress());
        response.setCustomerUserId(
                invoice.getCustomerUserId());
        response.setSubtotal(invoice.getSubtotal());
        response.setTotalTax(invoice.getTotalTax());
        response.setTotalAmount(invoice.getTotalAmount());
        response.setPaymentTerms(invoice.getPaymentTerms());
        response.setDueDate(invoice.getDueDate());
        response.setNotes(invoice.getNotes());
        response.setStatus(invoice.getStatus().name());
        response.setPaidAt(invoice.getPaidAt());
        response.setCreatedAt(invoice.getCreatedAt());

        List<InvoiceLineItemResponse> itemResponses =
                invoice.getLineItems().stream()
                        .map(this::mapLineItemToResponse)
                        .collect(Collectors.toList());
        response.setLineItems(itemResponses);

        return response;
    }

    private InvoiceLineItemResponse mapLineItemToResponse(
            InvoiceLineItem item) {
        InvoiceLineItemResponse response =
                new InvoiceLineItemResponse();
        response.setId(item.getId());
        response.setDescription(item.getDescription());
        response.setQuantity(item.getQuantity());
        response.setUnitPrice(item.getUnitPrice());
        response.setTaxPercent(item.getTaxPercent());
        response.setLineTotal(item.getLineTotal());
        response.setTaxAmount(item.getTaxAmount());
        response.setLineTotalWithTax(
                item.getLineTotalWithTax());
        return response;
    }
}