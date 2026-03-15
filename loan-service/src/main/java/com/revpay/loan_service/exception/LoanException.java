package com.revpay.loan_service.exception;

public class LoanException extends RuntimeException {
    public LoanException(String message) {
        super(message);
    }
}