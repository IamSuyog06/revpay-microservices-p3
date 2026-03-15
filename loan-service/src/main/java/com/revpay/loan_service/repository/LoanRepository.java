package com.revpay.loan_service.repository;

import com.revpay.loan_service.entity.Loan;
import com.revpay.loan_service.enums.LoanStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LoanRepository
        extends JpaRepository<Loan, Long> {

    List<Loan> findByUserIdOrderByCreatedAtDesc(
            Long userId);

    List<Loan> findByUserIdAndStatus(
            Long userId, LoanStatus status);

    Optional<Loan> findByIdAndUserId(
            Long id, Long userId);

    // For admin
    List<Loan> findByStatusOrderByCreatedAtDesc(
            LoanStatus status);
}