package com.revpay.loan_service.controller;

import com.revpay.loan_service.dto.*;
import com.revpay.loan_service.service.LoanService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/loans")
public class LoanController {

    private final LoanService loanService;

    public LoanController(LoanService loanService) {
        this.loanService = loanService;
    }

    // Apply for loan
    @PostMapping("/{userId}/apply")
    public ResponseEntity<LoanResponse> applyForLoan(
            @PathVariable Long userId,
            @Valid @RequestBody
            LoanApplicationRequest request) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(loanService.applyForLoan(
                        userId, request));
    }

    // Upload document
    @PostMapping(value = "/{userId}/{loanId}/documents",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<LoanResponse> uploadDocument(
            @PathVariable Long userId,
            @PathVariable Long loanId,
            @RequestParam("file") MultipartFile file)
            throws IOException {
        return ResponseEntity.ok(
                loanService.uploadDocument(
                        userId, loanId, file));
    }

    // Get all loans for user
    @GetMapping("/{userId}")
    public ResponseEntity<List<LoanResponse>> getUserLoans(
            @PathVariable Long userId) {
        return ResponseEntity.ok(
                loanService.getUserLoans(userId));
    }

    // Get single loan
    @GetMapping("/{userId}/{loanId}")
    public ResponseEntity<LoanResponse> getLoanById(
            @PathVariable Long userId,
            @PathVariable Long loanId) {
        return ResponseEntity.ok(
                loanService.getLoanById(userId, loanId));
    }

    // Get EMI schedule
    @GetMapping("/{userId}/{loanId}/emi-schedule")
    public ResponseEntity<List<EMIScheduleResponse>>
    getEmiSchedule(
            @PathVariable Long userId,
            @PathVariable Long loanId) {
        return ResponseEntity.ok(
                loanService.getEmiSchedule(userId, loanId));
    }

    // Make repayment
    @PostMapping("/{userId}/{loanId}/repay")
    public ResponseEntity<RepaymentResponse> makeRepayment(
            @PathVariable Long userId,
            @PathVariable Long loanId,
            @Valid @RequestBody RepaymentRequest request) {
        return ResponseEntity.ok(
                loanService.makeRepayment(
                        userId, loanId, request));
    }

    // Get repayment history
    @GetMapping("/{userId}/{loanId}/repayments")
    public ResponseEntity<List<RepaymentResponse>>
    getRepayments(
            @PathVariable Long userId,
            @PathVariable Long loanId) {
        return ResponseEntity.ok(
                loanService.getRepaymentHistory(
                        userId, loanId));
    }

    // Get loan analytics
    @GetMapping("/{userId}/analytics")
    public ResponseEntity<LoanAnalyticsResponse>
    getAnalytics(@PathVariable Long userId) {
        return ResponseEntity.ok(
                loanService.getLoanAnalytics(userId));
    }

    // Internal endpoints for Admin Service
    @PutMapping("/admin/approve/{loanId}")
    public ResponseEntity<LoanResponse> approveLoan(
            @PathVariable Long loanId) {
        return ResponseEntity.ok(
                loanService.approveLoan(loanId));
    }

    @PutMapping("/admin/reject/{loanId}")
    public ResponseEntity<LoanResponse> rejectLoan(
            @PathVariable Long loanId,
            @RequestParam String reason) {
        return ResponseEntity.ok(
                loanService.rejectLoan(loanId, reason));
    }

    @GetMapping("/admin/pending")
    public ResponseEntity<List<LoanResponse>>
    getPendingLoans() {
        return ResponseEntity.ok(
                loanService.getPendingLoans());
    }
}