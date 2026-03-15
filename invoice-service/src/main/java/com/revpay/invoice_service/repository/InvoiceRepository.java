package com.revpay.invoice_service.repository;

import com.revpay.invoice_service.entity.Invoice;
import com.revpay.invoice_service.enums.InvoiceStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface InvoiceRepository
        extends JpaRepository<Invoice, Long> {

    // Business user's invoices
    List<Invoice> findByBusinessUserIdOrderByCreatedAtDesc(
            Long businessUserId);

    // Business user's invoices by status
    List<Invoice> findByBusinessUserIdAndStatus(
            Long businessUserId, InvoiceStatus status);

    // Customer's received invoices
    List<Invoice> findByCustomerEmailOrderByCreatedAtDesc(
            String customerEmail);

    // Customer's received invoices by status
    List<Invoice> findByCustomerEmailAndStatus(
            String customerEmail, InvoiceStatus status);

    Optional<Invoice> findByInvoiceNumber(
            String invoiceNumber);

    Optional<Invoice> findByIdAndBusinessUserId(
            Long id, Long businessUserId);

    // For overdue scheduler
    List<Invoice> findByStatusAndDueDateBefore(
            InvoiceStatus status, LocalDate date);

    // Get next invoice number sequence
    Long countByBusinessUserId(Long businessUserId);
}