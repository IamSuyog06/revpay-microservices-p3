package com.revpay.wallet_service.controller;

import com.revpay.wallet_service.dto.InternalWalletRequest;
import com.revpay.wallet_service.dto.WalletResponse;
import com.revpay.wallet_service.service.WalletService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/wallets/internal")
public class InternalWalletController {

    private final WalletService walletService;

    public InternalWalletController(
            WalletService walletService) {
        this.walletService = walletService;
    }

    // Called by Transaction Service to deduct from sender
    @PostMapping("/deduct")
    public ResponseEntity<WalletResponse> deduct(
            @Valid @RequestBody
            InternalWalletRequest request) {
        return ResponseEntity.ok(
                walletService.deductBalance(request));
    }

    // Called by Transaction Service to credit receiver
    @PostMapping("/credit")
    public ResponseEntity<WalletResponse> credit(
            @Valid @RequestBody
            InternalWalletRequest request) {
        return ResponseEntity.ok(
                walletService.creditBalance(request));
    }

    // Called by Transaction Service to check balance
    @GetMapping("/balance/{userId}")
    public ResponseEntity<WalletResponse> getBalance(
            @PathVariable Long userId) {
        return ResponseEntity.ok(
                walletService.getWalletByUserId(userId));
    }
}
