package com.revpay.transaction_service.controller;

import com.revpay.transaction_service.dto.SendMoneyRequest;
import com.revpay.transaction_service.dto.TransactionFilterRequest;
import com.revpay.transaction_service.dto.TransactionResponse;
import com.revpay.transaction_service.service.TransactionService;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    private final TransactionService transactionService;

    public TransactionController(
            TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @PostMapping("/{userId}/send")
    public ResponseEntity<TransactionResponse> sendMoney(
            @PathVariable Long userId,
            @Valid @RequestBody SendMoneyRequest request) {
        return ResponseEntity.ok(
                transactionService.sendMoney(userId, request));
    }

    @GetMapping("/{userId}/history")
    public ResponseEntity<List<TransactionResponse>>
    getHistory(@PathVariable Long userId) {
        return ResponseEntity.ok(
                transactionService
                        .getTransactionHistory(userId));
    }

    @PostMapping("/{userId}/filter")
    public ResponseEntity<List<TransactionResponse>>
    filterTransactions(
            @PathVariable Long userId,
            @RequestBody
            TransactionFilterRequest filter) {
        return ResponseEntity.ok(
                transactionService
                        .filterTransactions(userId, filter));
    }

    @GetMapping("/{userId}/search")
    public ResponseEntity<List<TransactionResponse>>
    searchTransactions(
            @PathVariable Long userId,
            @RequestParam String keyword) {
        return ResponseEntity.ok(
                transactionService
                        .searchTransactions(userId, keyword));
    }

    @GetMapping("/{userId}/export")
    public ResponseEntity<byte[]> exportCsv(
            @PathVariable Long userId) {
        byte[] csvData =
                transactionService.exportToCsv(userId);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(
                MediaType.parseMediaType("text/csv"));
        headers.setContentDispositionFormData(
                "attachment",
                "transactions-" + userId + ".csv");

        return ResponseEntity.ok()
                .headers(headers)
                .body(csvData);
    }
}