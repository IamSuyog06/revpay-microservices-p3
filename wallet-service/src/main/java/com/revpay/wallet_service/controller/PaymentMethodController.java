package com.revpay.wallet_service.controller;

import com.revpay.wallet_service.dto.PaymentMethodRequest;
import com.revpay.wallet_service.dto.PaymentMethodResponse;
import com.revpay.wallet_service.service.PaymentMethodService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/payment-methods")
public class PaymentMethodController {

    private final PaymentMethodService paymentMethodService;

    public PaymentMethodController(
            PaymentMethodService paymentMethodService) {
        this.paymentMethodService = paymentMethodService;
    }

    @PostMapping("/{userId}")
    public ResponseEntity<PaymentMethodResponse> addCard(
            @PathVariable Long userId,
            @Valid @RequestBody PaymentMethodRequest request) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(paymentMethodService
                        .addPaymentMethod(userId, request));
    }

    @GetMapping("/{userId}")
    public ResponseEntity<List<PaymentMethodResponse>> getCards(
            @PathVariable Long userId) {
        return ResponseEntity.ok(
                paymentMethodService.getPaymentMethods(userId));
    }

    @DeleteMapping("/{userId}/{cardId}")
    public ResponseEntity<String> deleteCard(
            @PathVariable Long userId,
            @PathVariable Long cardId) {
        paymentMethodService.deletePaymentMethod(userId, cardId);
        return ResponseEntity.ok(
                "Payment method deleted successfully");
    }

    @PutMapping("/{userId}/{cardId}/set-default")
    public ResponseEntity<PaymentMethodResponse> setDefault(
            @PathVariable Long userId,
            @PathVariable Long cardId) {
        return ResponseEntity.ok(
                paymentMethodService
                        .setDefaultPaymentMethod(userId, cardId));
    }
}