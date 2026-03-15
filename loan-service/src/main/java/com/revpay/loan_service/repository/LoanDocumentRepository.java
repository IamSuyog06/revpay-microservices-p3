package com.revpay.loan_service.repository;

import com.revpay.loan_service.entity.LoanDocument;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LoanDocumentRepository
        extends JpaRepository<LoanDocument, Long> {

    List<LoanDocument> findByLoanId(Long loanId);
}