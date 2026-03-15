package com.revpay.loan_service.repository;

import com.revpay.loan_service.entity.LoanRepayment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface LoanRepaymentRepository
        extends JpaRepository<LoanRepayment, Long> {

    List<LoanRepayment> findByLoanIdOrderByPaidAtDesc(
            Long loanId);

    @Query("SELECT COALESCE(SUM(r.amount), 0) " +
            "FROM LoanRepayment r " +
            "WHERE r.loan.id = :loanId")
    BigDecimal getTotalRepaidByLoanId(
            @Param("loanId") Long loanId);
}