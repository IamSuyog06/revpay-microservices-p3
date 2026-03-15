package com.revpay.transaction_service.repository;

import com.revpay.transaction_service.entity.Transaction;
import com.revpay.transaction_service.enums.TransactionStatus;
import com.revpay.transaction_service.enums.TransactionType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface TransactionRepository
        extends JpaRepository<Transaction, Long> {

    Optional<Transaction> findByTransactionId(
            String transactionId);

    // Get all transactions for a user
    // (as sender or receiver)
    @Query("SELECT t FROM Transaction t WHERE " +
            "t.senderUserId = :userId OR " +
            "t.receiverUserId = :userId " +
            "ORDER BY t.createdAt DESC")
    List<Transaction> findAllByUserId(
            @Param("userId") Long userId);

    // Filter transactions
    @Query("SELECT t FROM Transaction t WHERE " +
            "(t.senderUserId = :userId OR " +
            "t.receiverUserId = :userId) " +
            "AND (:type IS NULL OR t.type = :type) " +
            "AND (:status IS NULL OR t.status = :status) " +
            "AND (:fromDate IS NULL OR " +
            "t.createdAt >= :fromDate) " +
            "AND (:toDate IS NULL OR " +
            "t.createdAt <= :toDate) " +
            "AND (:minAmount IS NULL OR " +
            "t.amount >= :minAmount) " +
            "AND (:maxAmount IS NULL OR " +
            "t.amount <= :maxAmount) " +
            "ORDER BY t.createdAt DESC")
    List<Transaction> findWithFilters(
            @Param("userId") Long userId,
            @Param("type") TransactionType type,
            @Param("status") TransactionStatus status,
            @Param("fromDate") LocalDateTime fromDate,
            @Param("toDate") LocalDateTime toDate,
            @Param("minAmount") BigDecimal minAmount,
            @Param("maxAmount") BigDecimal maxAmount);

    // Search by sender/receiver name or transactionId
    @Query("SELECT t FROM Transaction t WHERE " +
            "(t.senderUserId = :userId OR " +
            "t.receiverUserId = :userId) " +
            "AND (LOWER(t.senderName) LIKE " +
            "LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(t.receiverName) LIKE " +
            "LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(t.transactionId) LIKE " +
            "LOWER(CONCAT('%', :keyword, '%'))) " +
            "ORDER BY t.createdAt DESC")
    List<Transaction> searchTransactions(
            @Param("userId") Long userId,
            @Param("keyword") String keyword);
}