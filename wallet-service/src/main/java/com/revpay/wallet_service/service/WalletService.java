package com.revpay.wallet_service.service;

import com.revpay.wallet_service.client.UserServiceClient;
import com.revpay.wallet_service.dto.*;
import com.revpay.wallet_service.entity.PaymentMethod;
import com.revpay.wallet_service.entity.Wallet;
import com.revpay.wallet_service.exception.InsufficientBalanceException;
import com.revpay.wallet_service.repository.PaymentMethodRepository;
import com.revpay.wallet_service.repository.WalletRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
public class WalletService {

    private static final Logger logger =
            LoggerFactory.getLogger(WalletService.class);

    private final WalletRepository walletRepository;
    private final PaymentMethodRepository paymentMethodRepository;
    private final UserServiceClient userServiceClient;

    public WalletService(
            WalletRepository walletRepository,
            PaymentMethodRepository paymentMethodRepository,
            UserServiceClient userServiceClient) {
        this.walletRepository = walletRepository;
        this.paymentMethodRepository = paymentMethodRepository;
        this.userServiceClient = userServiceClient;
    }

    // Called by User Service via Feign after registration
    public WalletResponse createWallet(
            CreateWalletRequest request) {
        logger.info("Creating wallet for userId: {}",
                request.getUserId());

        if (walletRepository.existsByUserId(
                request.getUserId())) {
            logger.warn("Wallet already exists for userId: {}",
                    request.getUserId());
            throw new RuntimeException(
                    "Wallet already exists for user: "
                            + request.getUserId());
        }

        Wallet wallet = new Wallet(request.getUserId());
        Wallet saved = walletRepository.save(wallet);

        logger.info("Wallet created successfully for userId: {}",
                request.getUserId());
        return mapToWalletResponse(saved);
    }

    public WalletResponse getWalletByUserId(Long userId) {
        logger.debug("Fetching wallet for userId: {}", userId);
        Wallet wallet = walletRepository
                .findByUserId(userId)
                .orElseThrow(() -> new RuntimeException(
                        "Wallet not found for user: " + userId));
        return mapToWalletResponse(wallet);
    }

    @Transactional
    public WalletResponse addMoney(Long userId,
                                   AddMoneyRequest request) {
        logger.info("Adding money for userId: {}, amount: {}",
                userId, request.getAmount());

        // Verify PIN via Feign call to User Service
        Boolean pinValid = userServiceClient
                .verifyPin(userId, request.getPin());

        if (!pinValid) {
            logger.warn("Invalid PIN for userId: {}", userId);
            throw new RuntimeException("Invalid PIN");
        }

        // Verify payment method belongs to user
        PaymentMethod paymentMethod = paymentMethodRepository
                .findByIdAndUserId(
                        request.getPaymentMethodId(), userId)
                .orElseThrow(() -> new RuntimeException(
                        "Payment method not found"));

        if (!paymentMethod.isActive()) {
            throw new RuntimeException(
                    "Payment method is not active");
        }

        // Add money to wallet
        Wallet wallet = walletRepository
                .findByUserId(userId)
                .orElseThrow(() -> new RuntimeException(
                        "Wallet not found for user: " + userId));

        wallet.setBalance(
                wallet.getBalance().add(request.getAmount()));
        Wallet updated = walletRepository.save(wallet);

        logger.info("Money added successfully for userId: {},"
                + " new balance: {}", userId, updated.getBalance());
        return mapToWalletResponse(updated);
    }

    @Transactional
    public WalletResponse withdraw(Long userId,
                                   WithdrawRequest request) {
        logger.info("Withdrawing money for userId: {},"
                + " amount: {}", userId, request.getAmount());

        // Verify PIN
        Boolean pinValid = userServiceClient
                .verifyPin(userId, request.getPin());

        if (!pinValid) {
            logger.warn("Invalid PIN for userId: {}", userId);
            throw new RuntimeException("Invalid PIN");
        }

        Wallet wallet = walletRepository
                .findByUserId(userId)
                .orElseThrow(() -> new RuntimeException(
                        "Wallet not found for user: " + userId));

        // Check sufficient balance
        if (wallet.getBalance().compareTo(
                request.getAmount()) < 0) {
            logger.warn("Insufficient balance for userId: {}",
                    userId);
            throw new InsufficientBalanceException(
                    "Insufficient balance. Current balance: "
                            + wallet.getBalance());
        }

        wallet.setBalance(
                wallet.getBalance().subtract(request.getAmount()));
        Wallet updated = walletRepository.save(wallet);

        logger.info("Withdrawal successful for userId: {},"
                + " new balance: {}", userId, updated.getBalance());
        return mapToWalletResponse(updated);
    }

    private WalletResponse mapToWalletResponse(Wallet wallet) {
        WalletResponse response = new WalletResponse();
        response.setId(wallet.getId());
        response.setUserId(wallet.getUserId());
        response.setBalance(wallet.getBalance());
        response.setActive(wallet.isActive());
        response.setCreatedAt(wallet.getCreatedAt());
        return response;
    }

    // Called internally by Transaction Service
// No PIN verification needed here
// PIN already verified in Transaction Service
    @Transactional
    public WalletResponse deductBalance(
            InternalWalletRequest request) {
        logger.info("Internal deduct: userId: {},"
                        + " amount: {}",
                request.getUserId(), request.getAmount());

        Wallet wallet = walletRepository
                .findByUserId(request.getUserId())
                .orElseThrow(() -> new RuntimeException(
                        "Wallet not found for user: "
                                + request.getUserId()));

        if (wallet.getBalance().compareTo(
                request.getAmount()) < 0) {
            logger.warn("Insufficient balance for"
                    + " userId: {}", request.getUserId());
            throw new InsufficientBalanceException(
                    "Insufficient balance. Current balance: "
                            + wallet.getBalance());
        }

        wallet.setBalance(wallet.getBalance()
                .subtract(request.getAmount()));
        Wallet updated = walletRepository.save(wallet);

        logger.info("Internal deduct successful:"
                        + " userId: {}, new balance: {}",
                request.getUserId(), updated.getBalance());
        return mapToWalletResponse(updated);
    }

    @Transactional
    public WalletResponse creditBalance(
            InternalWalletRequest request) {
        logger.info("Internal credit: userId: {},"
                        + " amount: {}",
                request.getUserId(), request.getAmount());

        Wallet wallet = walletRepository
                .findByUserId(request.getUserId())
                .orElseThrow(() -> new RuntimeException(
                        "Wallet not found for user: "
                                + request.getUserId()));

        wallet.setBalance(wallet.getBalance()
                .add(request.getAmount()));
        Wallet updated = walletRepository.save(wallet);

        logger.info("Internal credit successful:"
                        + " userId: {}, new balance: {}",
                request.getUserId(), updated.getBalance());
        return mapToWalletResponse(updated);
    }
}