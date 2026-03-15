package com.revpay.loan_service.entity;

import com.revpay.loan_service.enums.RepaymentStatus;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "loan_repayments")
public class LoanRepayment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "loan_id", nullable = false)
    private Loan loan;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal amount;

    @Column(precision = 15, scale = 2)
    private BigDecimal remainingAfterPayment;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RepaymentStatus status;

    @Column(updatable = false)
    private LocalDateTime paidAt;

    @PrePersist
    protected void onCreate() {
        paidAt = LocalDateTime.now();
    }

    public LoanRepayment() {}

    // Getters
    public Long getId() { return id; }
    public Loan getLoan() { return loan; }
    public BigDecimal getAmount() { return amount; }
    public BigDecimal getRemainingAfterPayment() { return remainingAfterPayment; }
    public RepaymentStatus getStatus() { return status; }
    public LocalDateTime getPaidAt() { return paidAt; }

    // Setters
    public void setId(Long id) { this.id = id; }
    public void setLoan(Loan loan) { this.loan = loan; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
    public void setRemainingAfterPayment(BigDecimal remainingAfterPayment) { this.remainingAfterPayment = remainingAfterPayment; }
    public void setStatus(RepaymentStatus status) { this.status = status; }
    public void setPaidAt(LocalDateTime paidAt) { this.paidAt = paidAt; }
}