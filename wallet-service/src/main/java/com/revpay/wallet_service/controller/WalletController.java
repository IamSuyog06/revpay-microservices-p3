package com.revpay.wallet_service.controller;

import com.revpay.wallet_service.dto.*;
import com.revpay.wallet_service.service.WalletService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/wallets")
public class WalletController {

    private final WalletService walletService;

    public WalletController(WalletService walletService) {
        this.walletService = walletService;
    }

    // Internal endpoint — called by User Service via Feign
    @PostMapping("/create")
    public ResponseEntity<WalletResponse> createWallet(
            @Valid @RequestBody CreateWalletRequest request) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(walletService.createWallet(request));
    }

    @GetMapping("/{userId}")
    public ResponseEntity<WalletResponse> getWallet(
            @PathVariable Long userId) {
        return ResponseEntity.ok(
                walletService.getWalletByUserId(userId));
    }

    @PostMapping("/{userId}/add-money")
    public ResponseEntity<WalletResponse> addMoney(
            @PathVariable Long userId,
            @Valid @RequestBody AddMoneyRequest request) {
        return ResponseEntity.ok(
                walletService.addMoney(userId, request));
    }

    @PostMapping("/{userId}/withdraw")
    public ResponseEntity<WalletResponse> withdraw(
            @PathVariable Long userId,
            @Valid @RequestBody WithdrawRequest request) {
        return ResponseEntity.ok(
                walletService.withdraw(userId, request));
    }
}