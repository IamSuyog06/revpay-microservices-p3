package com.revpay.transaction_service.service;

import com.revpay.transaction_service.client.NotificationServiceClient;
import com.revpay.transaction_service.client.UserServiceClient;
import com.revpay.transaction_service.client.WalletServiceClient;
import com.revpay.transaction_service.dto.*;
import com.revpay.transaction_service.entity.Transaction;
import com.revpay.transaction_service.enums.TransactionStatus;
import com.revpay.transaction_service.enums.TransactionType;
import com.revpay.transaction_service.exception.InsufficientBalanceException;
import com.revpay.transaction_service.repository.TransactionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class TransactionService {

    private static final Logger logger =
            LoggerFactory.getLogger(TransactionService.class);

    private final TransactionRepository transactionRepository;
    private final UserServiceClient userServiceClient;
    private final WalletServiceClient walletServiceClient;
    private final NotificationServiceClient notificationServiceClient;

    public TransactionService(
            TransactionRepository transactionRepository,
            UserServiceClient userServiceClient,
            WalletServiceClient walletServiceClient,
            NotificationServiceClient notificationServiceClient) {
        this.transactionRepository = transactionRepository;
        this.userServiceClient = userServiceClient;
        this.walletServiceClient = walletServiceClient;
        this.notificationServiceClient = notificationServiceClient;
    }

    @Transactional
    public TransactionResponse sendMoney(
            Long senderUserId,
            SendMoneyRequest request) {

        logger.info("Send money request from userId: {}"
                        + " to: {}, amount: {}",
                senderUserId,
                request.getRecipientIdentifier(),
                request.getAmount());

        //Verify PIN
        Boolean pinValid = userServiceClient
                .verifyPin(senderUserId, request.getPin());
        if (!pinValid) {
            logger.warn("Invalid PIN for userId: {}",
                    senderUserId);
            throw new RuntimeException("Invalid PIN");
        }

        //Get sender details
        UserResponse sender = userServiceClient
                .getUserById(senderUserId);

        //Find receiver by email or phone
        UserResponse receiver;
        try {
            receiver = userServiceClient
                    .getUserByEmail(
                            request.getRecipientIdentifier());
        } catch (Exception e) {
            logger.warn("Receiver not found: {}",
                    request.getRecipientIdentifier());
            throw new RuntimeException(
                    "Recipient not found: "
                            + request.getRecipientIdentifier());
        }

        //Cannot send to yourself
        if (senderUserId.equals(receiver.getId())) {
            throw new RuntimeException(
                    "Cannot send money to yourself");
        }

        //Check sender balance
        WalletResponse senderWallet = walletServiceClient
                .getBalance(senderUserId);
        if (senderWallet.getBalance()
                .compareTo(request.getAmount()) < 0) {
            throw new InsufficientBalanceException(
                    "Insufficient balance. Current balance: "
                            + senderWallet.getBalance());
        }

        //Create transaction record (PENDING)
        Transaction transaction = new Transaction();
        transaction.setTransactionId(
                "TXN-" + UUID.randomUUID()
                        .toString().substring(0, 8).toUpperCase());
        transaction.setSenderUserId(senderUserId);
        transaction.setReceiverUserId(receiver.getId());
        transaction.setSenderName(sender.getFullName());
        transaction.setReceiverName(receiver.getFullName());
        transaction.setAmount(request.getAmount());
        transaction.setType(TransactionType.SENT);
        transaction.setStatus(TransactionStatus.PENDING);
        transaction.setNote(request.getNote());
        Transaction saved =
                transactionRepository.save(transaction);

        try {
            //Deduct from sender wallet
            Map<String, Object> deductRequest =
                    new HashMap<>();
            deductRequest.put("userId", senderUserId);
            deductRequest.put("amount", request.getAmount());
            walletServiceClient.deductBalance(deductRequest);

            //Credit to receiver wallet
            Map<String, Object> creditRequest =
                    new HashMap<>();
            creditRequest.put("userId", receiver.getId());
            creditRequest.put("amount", request.getAmount());
            walletServiceClient.creditBalance(creditRequest);

            //Update transaction to SUCCESS
            saved.setStatus(TransactionStatus.SUCCESS);
            transactionRepository.save(saved);

            logger.info("Transaction successful: {}",
                    saved.getTransactionId());

            //Send notifications

            try {
                notificationServiceClient
                        .createNotification(
                                new NotificationRequest(
                                        senderUserId,
                                        "Money Sent",
                                        "You sent ₹" + request.getAmount()
                                                + " to " + receiver.getFullName(),
                                        "TRANSACTION"
                                ));
                notificationServiceClient
                        .createNotification(
                                new NotificationRequest(
                                        receiver.getId(),
                                        "Money Received",
                                        "You received ₹"
                                                + request.getAmount()
                                                + " from " + sender.getFullName(),
                                        "TRANSACTION"
                                ));
            } catch (Exception notifEx) {
                logger.error("Notification failed for"
                                + " transaction: {}. Error: {}",
                        saved.getTransactionId(),
                        notifEx.getMessage());
            }

        } catch (InsufficientBalanceException e) {
            saved.setStatus(TransactionStatus.FAILED);
            saved.setFailureReason(e.getMessage());
            transactionRepository.save(saved);
            throw e;
        } catch (Exception e) {
            //Mark transaction as FAILED
            saved.setStatus(TransactionStatus.FAILED);
            saved.setFailureReason(e.getMessage());
            transactionRepository.save(saved);
            logger.error("Transaction failed: {}. Error: {}",
                    saved.getTransactionId(), e.getMessage());
            throw new RuntimeException(
                    "Transaction failed: " + e.getMessage());
        }

        return mapToTransactionResponse(saved);
    }

    public List<TransactionResponse> getTransactionHistory(
            Long userId) {
        logger.debug("Fetching transaction history"
                + " for userId: {}", userId);
        return transactionRepository
                .findAllByUserId(userId)
                .stream()
                .map(this::mapToTransactionResponse)
                .collect(Collectors.toList());
    }

    public List<TransactionResponse> filterTransactions(
            Long userId,
            TransactionFilterRequest filter) {
        logger.debug("Filtering transactions for userId: {}",
                userId);
        return transactionRepository
                .findWithFilters(
                        userId,
                        filter.getType(),
                        filter.getStatus(),
                        filter.getFromDate(),
                        filter.getToDate(),
                        filter.getMinAmount(),
                        filter.getMaxAmount())
                .stream()
                .map(this::mapToTransactionResponse)
                .collect(Collectors.toList());
    }

    public List<TransactionResponse> searchTransactions(
            Long userId, String keyword) {
        logger.debug("Searching transactions for"
                + " userId: {}, keyword: {}", userId, keyword);
        return transactionRepository
                .searchTransactions(userId, keyword)
                .stream()
                .map(this::mapToTransactionResponse)
                .collect(Collectors.toList());
    }

    public byte[] exportToCsv(Long userId) {
        logger.info("Exporting transactions to CSV"
                + " for userId: {}", userId);

        List<Transaction> transactions =
                transactionRepository.findAllByUserId(userId);

        StringBuilder csv = new StringBuilder();
        csv.append("Transaction ID,Type,Amount,"
                + "Sender,Receiver,Status,Note,Date\n");

        for (Transaction t : transactions) {
            csv.append(t.getTransactionId()).append(",");
            csv.append(t.getType()).append(",");
            csv.append(t.getAmount()).append(",");
            csv.append(t.getSenderName()).append(",");
            csv.append(t.getReceiverName()).append(",");
            csv.append(t.getStatus()).append(",");
            csv.append(t.getNote() != null
                    ? t.getNote() : "").append(",");
            csv.append(t.getCreatedAt()).append("\n");
        }

        return csv.toString().getBytes();
    }

    private TransactionResponse mapToTransactionResponse(
            Transaction t) {
        TransactionResponse response =
                new TransactionResponse();
        response.setId(t.getId());
        response.setTransactionId(t.getTransactionId());
        response.setSenderUserId(t.getSenderUserId());
        response.setReceiverUserId(t.getReceiverUserId());
        response.setSenderName(t.getSenderName());
        response.setReceiverName(t.getReceiverName());
        response.setAmount(t.getAmount());
        response.setType(t.getType().name());
        response.setStatus(t.getStatus().name());
        response.setNote(t.getNote());
        response.setFailureReason(t.getFailureReason());
        response.setCreatedAt(t.getCreatedAt());
        return response;
    }
}