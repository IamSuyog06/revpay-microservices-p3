package com.revpay.transaction_service.service;

import com.revpay.transaction_service.client.NotificationServiceClient;
import com.revpay.transaction_service.client.UserServiceClient;
import com.revpay.transaction_service.client.WalletServiceClient;
import com.revpay.transaction_service.dto.*;
import com.revpay.transaction_service.entity.MoneyRequest;
import com.revpay.transaction_service.entity.Transaction;
import com.revpay.transaction_service.enums.MoneyRequestStatus;
import com.revpay.transaction_service.enums.TransactionStatus;
import com.revpay.transaction_service.enums.TransactionType;
import com.revpay.transaction_service.exception.InsufficientBalanceException;
import com.revpay.transaction_service.repository.MoneyRequestRepository;
import com.revpay.transaction_service.repository.TransactionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class MoneyRequestService {

    private static final Logger logger =
            LoggerFactory.getLogger(MoneyRequestService.class);

    private final MoneyRequestRepository moneyRequestRepository;
    private final TransactionRepository transactionRepository;
    private final UserServiceClient userServiceClient;
    private final WalletServiceClient walletServiceClient;
    private final NotificationServiceClient notificationServiceClient;

    public MoneyRequestService(
            MoneyRequestRepository moneyRequestRepository,
            TransactionRepository transactionRepository,
            UserServiceClient userServiceClient,
            WalletServiceClient walletServiceClient,
            NotificationServiceClient notificationServiceClient) {
        this.moneyRequestRepository = moneyRequestRepository;
        this.transactionRepository = transactionRepository;
        this.userServiceClient = userServiceClient;
        this.walletServiceClient = walletServiceClient;
        this.notificationServiceClient = notificationServiceClient;
    }

    public MoneyRequestResponse createRequest(
            Long requesterId,
            MoneyRequestDTO dto) {
        logger.info("Creating money request from"
                        + " userId: {} to: {}, amount: {}",
                requesterId,
                dto.getRecipientIdentifier(),
                dto.getAmount());

        // Get requester details
        UserResponse requester = userServiceClient
                .getUserById(requesterId);

        // Find target user
        UserResponse requestedFrom;
        try {
            requestedFrom = userServiceClient
                    .getUserByEmail(
                            dto.getRecipientIdentifier());
        } catch (Exception e) {
            throw new RuntimeException(
                    "User not found: "
                            + dto.getRecipientIdentifier());
        }

        // Cannot request from yourself
        if (requesterId.equals(requestedFrom.getId())) {
            throw new RuntimeException(
                    "Cannot request money from yourself");
        }

        MoneyRequest moneyRequest = new MoneyRequest();
        moneyRequest.setRequesterId(requesterId);
        moneyRequest.setRequestedFromId(
                requestedFrom.getId());
        moneyRequest.setRequesterName(
                requester.getFullName());
        moneyRequest.setRequestedFromName(
                requestedFrom.getFullName());
        moneyRequest.setAmount(dto.getAmount());
        moneyRequest.setPurpose(dto.getPurpose());

        MoneyRequest saved =
                moneyRequestRepository.save(moneyRequest);

        // Notify the person being requested
        try {
            notificationServiceClient.createNotification(
                    new NotificationRequest(
                            requestedFrom.getId(),
                            "Money Request",
                            requester.getFullName()
                                    + " requested ₹" + dto.getAmount(),
                            "MONEY_REQUEST"
                    ));
        } catch (Exception e) {
            logger.error("Notification failed: {}",
                    e.getMessage());
        }

        logger.info("Money request created: {}", saved.getId());
        return mapToResponse(saved);
    }

    public List<MoneyRequestResponse> getIncomingRequests(
            Long userId) {
        return moneyRequestRepository
                .findByRequestedFromIdOrderByCreatedAtDesc(userId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public List<MoneyRequestResponse> getOutgoingRequests(
            Long userId) {
        return moneyRequestRepository
                .findByRequesterIdOrderByCreatedAtDesc(userId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public MoneyRequestResponse acceptRequest(
            Long userId, Long requestId, String pin) {
        logger.info("Accepting money request: {}"
                + " by userId: {}", requestId, userId);

        MoneyRequest request = moneyRequestRepository
                .findByIdAndRequestedFromId(requestId, userId)
                .orElseThrow(() -> new RuntimeException(
                        "Money request not found"));

        if (request.getStatus()
                != MoneyRequestStatus.PENDING) {
            throw new RuntimeException(
                    "Request is no longer pending");
        }

        // Verify PIN
        Boolean pinValid = userServiceClient
                .verifyPin(userId, pin);
        if (!pinValid) {
            throw new RuntimeException("Invalid PIN");
        }

        // Check balance
        WalletResponse wallet = walletServiceClient
                .getBalance(userId);
        if (wallet.getBalance()
                .compareTo(request.getAmount()) < 0) {
            throw new InsufficientBalanceException(
                    "Insufficient balance. Current balance: "
                            + wallet.getBalance());
        }

        // Deduct from payer
        Map<String, Object> deductReq = new HashMap<>();
        deductReq.put("userId", userId);
        deductReq.put("amount", request.getAmount());
        walletServiceClient.deductBalance(deductReq);

        // Credit to requester
        Map<String, Object> creditReq = new HashMap<>();
        creditReq.put("userId", request.getRequesterId());
        creditReq.put("amount", request.getAmount());
        walletServiceClient.creditBalance(creditReq);

        // Save transaction record
        Transaction transaction = new Transaction();
        transaction.setTransactionId(
                "TXN-" + UUID.randomUUID()
                        .toString().substring(0, 8).toUpperCase());
        transaction.setSenderUserId(userId);
        transaction.setReceiverUserId(
                request.getRequesterId());
        transaction.setSenderName(
                request.getRequestedFromName());
        transaction.setReceiverName(
                request.getRequesterName());
        transaction.setAmount(request.getAmount());
        transaction.setType(TransactionType.SENT);
        transaction.setStatus(TransactionStatus.SUCCESS);
        transaction.setNote("Money request payment");
        transactionRepository.save(transaction);

        // Update request status
        request.setStatus(MoneyRequestStatus.ACCEPTED);
        MoneyRequest updated =
                moneyRequestRepository.save(request);

        // Notify both parties
        try {
            notificationServiceClient.createNotification(
                    new NotificationRequest(
                            request.getRequesterId(),
                            "Request Accepted",
                            request.getRequestedFromName()
                                    + " accepted your request of ₹"
                                    + request.getAmount(),
                            "MONEY_REQUEST"
                    ));
        } catch (Exception e) {
            logger.error("Notification failed: {}",
                    e.getMessage());
        }

        return mapToResponse(updated);
    }

    public MoneyRequestResponse declineRequest(
            Long userId, Long requestId) {
        logger.info("Declining money request: {}", requestId);

        MoneyRequest request = moneyRequestRepository
                .findByIdAndRequestedFromId(requestId, userId)
                .orElseThrow(() -> new RuntimeException(
                        "Money request not found"));

        if (request.getStatus()
                != MoneyRequestStatus.PENDING) {
            throw new RuntimeException(
                    "Request is no longer pending");
        }

        request.setStatus(MoneyRequestStatus.DECLINED);
        MoneyRequest updated =
                moneyRequestRepository.save(request);

        // Notify requester
        try {
            notificationServiceClient.createNotification(
                    new NotificationRequest(
                            request.getRequesterId(),
                            "Request Declined",
                            request.getRequestedFromName()
                                    + " declined your request of ₹"
                                    + request.getAmount(),
                            "MONEY_REQUEST"
                    ));
        } catch (Exception e) {
            logger.error("Notification failed: {}",
                    e.getMessage());
        }

        return mapToResponse(updated);
    }

    public MoneyRequestResponse cancelRequest(
            Long userId, Long requestId) {
        logger.info("Cancelling money request: {}", requestId);

        MoneyRequest request = moneyRequestRepository
                .findByIdAndRequesterId(requestId, userId)
                .orElseThrow(() -> new RuntimeException(
                        "Money request not found"));

        if (request.getStatus()
                != MoneyRequestStatus.PENDING) {
            throw new RuntimeException(
                    "Only pending requests can be cancelled");
        }

        request.setStatus(MoneyRequestStatus.CANCELLED);
        MoneyRequest updated =
                moneyRequestRepository.save(request);

        logger.info("Money request cancelled: {}", requestId);
        return mapToResponse(updated);
    }

    private MoneyRequestResponse mapToResponse(
            MoneyRequest r) {
        MoneyRequestResponse response =
                new MoneyRequestResponse();
        response.setId(r.getId());
        response.setRequesterId(r.getRequesterId());
        response.setRequestedFromId(r.getRequestedFromId());
        response.setRequesterName(r.getRequesterName());
        response.setRequestedFromName(
                r.getRequestedFromName());
        response.setAmount(r.getAmount());
        response.setPurpose(r.getPurpose());
        response.setStatus(r.getStatus().name());
        response.setCreatedAt(r.getCreatedAt());
        return response;
    }
}