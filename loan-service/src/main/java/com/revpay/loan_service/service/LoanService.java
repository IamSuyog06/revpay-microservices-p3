package com.revpay.loan_service.service;

import com.revpay.loan_service.client.NotificationServiceClient;
import com.revpay.loan_service.client.UserServiceClient;
import com.revpay.loan_service.client.WalletServiceClient;
import com.revpay.loan_service.dto.*;
import com.revpay.loan_service.entity.Loan;
import com.revpay.loan_service.entity.LoanDocument;
import com.revpay.loan_service.entity.LoanRepayment;
import com.revpay.loan_service.enums.LoanStatus;
import com.revpay.loan_service.enums.RepaymentStatus;
import com.revpay.loan_service.exception.LoanException;
import com.revpay.loan_service.repository.LoanDocumentRepository;
import com.revpay.loan_service.repository.LoanRepaymentRepository;
import com.revpay.loan_service.repository.LoanRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class LoanService {

    private static final Logger logger =
            LoggerFactory.getLogger(LoanService.class);

    // Fixed interest rate 10% per annum
    private static final BigDecimal ANNUAL_INTEREST_RATE =
            new BigDecimal("10.00");

    @Value("${loan.document.upload-dir}")
    private String uploadDir;

    private final LoanRepository loanRepository;
    private final LoanDocumentRepository documentRepository;
    private final LoanRepaymentRepository repaymentRepository;
    private final UserServiceClient userServiceClient;
    private final WalletServiceClient walletServiceClient;
    private final NotificationServiceClient notificationServiceClient;

    public LoanService(
            LoanRepository loanRepository,
            LoanDocumentRepository documentRepository,
            LoanRepaymentRepository repaymentRepository,
            UserServiceClient userServiceClient,
            WalletServiceClient walletServiceClient,
            NotificationServiceClient notificationServiceClient) {
        this.loanRepository = loanRepository;
        this.documentRepository = documentRepository;
        this.repaymentRepository = repaymentRepository;
        this.userServiceClient = userServiceClient;
        this.walletServiceClient = walletServiceClient;
        this.notificationServiceClient =
                notificationServiceClient;
    }

    @Transactional
    public LoanResponse applyForLoan(
            Long userId,
            LoanApplicationRequest request) {
        logger.info("Loan application from userId: {},"
                + " amount: {}", userId, request.getLoanAmount());

        // Verify business user
        UserResponse user =
                userServiceClient.getUserById(userId);

        if (!user.getRole().equals("BUSINESS")) {
            throw new LoanException(
                    "Only business accounts can apply for loans");
        }

        // Calculate EMI using reducing balance formula
        // Monthly rate = annual rate / 12 / 100
        BigDecimal monthlyRate = ANNUAL_INTEREST_RATE
                .divide(BigDecimal.valueOf(1200),
                        10, RoundingMode.HALF_UP);

        // EMI = P * r * (1+r)^n / ((1+r)^n - 1)
        BigDecimal onePlusR = BigDecimal.ONE.add(monthlyRate);
        BigDecimal onePlusRPowN = onePlusR.pow(
                request.getTenureMonths(),
                new MathContext(10));

        BigDecimal emi = request.getLoanAmount()
                .multiply(monthlyRate)
                .multiply(onePlusRPowN)
                .divide(onePlusRPowN.subtract(BigDecimal.ONE),
                        2, RoundingMode.HALF_UP);

        BigDecimal totalRepayable = emi.multiply(
                        BigDecimal.valueOf(request.getTenureMonths()))
                .setScale(2, RoundingMode.HALF_UP);

        // Create loan
        Loan loan = new Loan();
        loan.setUserId(userId);
        loan.setUserName(user.getFullName());
        loan.setLoanAmount(request.getLoanAmount());
        loan.setPurpose(request.getPurpose());
        loan.setTenureMonths(request.getTenureMonths());
        loan.setInterestRate(ANNUAL_INTEREST_RATE);
        loan.setEmiAmount(emi);
        loan.setTotalRepayableAmount(totalRepayable);
        loan.setRemainingAmount(totalRepayable);
        loan.setFinancialInfo(request.getFinancialInfo());
        loan.setStatus(LoanStatus.PENDING);

        Loan saved = loanRepository.save(loan);

        logger.info("Loan application submitted: {}",
                saved.getId());

        // Notify admin (notification service)
        try {
            notificationServiceClient.createNotification(
                    new NotificationRequest(
                            userId,
                            "Loan Application Submitted",
                            "Your loan application for ₹"
                                    + request.getLoanAmount()
                                    + " has been submitted successfully",
                            "LOAN"
                    ));
        } catch (Exception e) {
            logger.error("Notification failed: {}",
                    e.getMessage());
        }

        return mapToLoanResponse(saved);
    }

    @Transactional
    public LoanResponse uploadDocument(
            Long userId,
            Long loanId,
            MultipartFile file) throws IOException {
        logger.info("Uploading document for loanId: {}",
                loanId);

        Loan loan = loanRepository
                .findByIdAndUserId(loanId, userId)
                .orElseThrow(() -> new LoanException(
                        "Loan not found"));

        if (loan.getStatus() != LoanStatus.PENDING) {
            throw new LoanException(
                    "Documents can only be uploaded for"
                            + " PENDING loans");
        }

        // Create upload directory if not exists
        Path uploadPath = Paths.get(uploadDir);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        // Generate unique filename
        String fileName = UUID.randomUUID().toString()
                + "_" + file.getOriginalFilename();
        Path filePath = uploadPath.resolve(fileName);

        // Save file
        Files.copy(file.getInputStream(), filePath,
                StandardCopyOption.REPLACE_EXISTING);

        // Save document record
        LoanDocument document = new LoanDocument();
        document.setLoan(loan);
        document.setFileName(fileName);
        document.setOriginalFileName(
                file.getOriginalFilename());
        document.setFilePath(filePath.toString());
        document.setFileType(file.getContentType());
        document.setFileSize(file.getSize());
        documentRepository.save(document);

        logger.info("Document uploaded for loanId: {}",
                loanId);

        return mapToLoanResponse(loan);
    }

    public List<LoanResponse> getUserLoans(Long userId) {
        return loanRepository
                .findByUserIdOrderByCreatedAtDesc(userId)
                .stream()
                .map(this::mapToLoanResponse)
                .collect(Collectors.toList());
    }

    public LoanResponse getLoanById(
            Long userId, Long loanId) {
        Loan loan = loanRepository
                .findByIdAndUserId(loanId, userId)
                .orElseThrow(() -> new LoanException(
                        "Loan not found"));
        return mapToLoanResponse(loan);
    }

    public List<EMIScheduleResponse> getEmiSchedule(
            Long userId, Long loanId) {
        logger.debug("Generating EMI schedule for"
                + " loanId: {}", loanId);

        Loan loan = loanRepository
                .findByIdAndUserId(loanId, userId)
                .orElseThrow(() -> new LoanException(
                        "Loan not found"));

        if (loan.getStatus() != LoanStatus.ACTIVE
                && loan.getStatus() != LoanStatus.CLOSED) {
            throw new LoanException(
                    "EMI schedule available only for"
                            + " approved loans");
        }

        List<EMIScheduleResponse> schedule =
                new ArrayList<>();

        BigDecimal monthlyRate = ANNUAL_INTEREST_RATE
                .divide(BigDecimal.valueOf(1200),
                        10, RoundingMode.HALF_UP);

        BigDecimal balance = loan.getLoanAmount();

        for (int i = 1;
             i <= loan.getTenureMonths(); i++) {
            BigDecimal interestComponent =
                    balance.multiply(monthlyRate)
                            .setScale(2, RoundingMode.HALF_UP);

            BigDecimal principalComponent =
                    loan.getEmiAmount()
                            .subtract(interestComponent)
                            .setScale(2, RoundingMode.HALF_UP);

            balance = balance
                    .subtract(principalComponent)
                    .setScale(2, RoundingMode.HALF_UP);

            if (balance.compareTo(BigDecimal.ZERO) < 0) {
                balance = BigDecimal.ZERO;
            }

            EMIScheduleResponse entry =
                    new EMIScheduleResponse();
            entry.setInstallmentNumber(i);
            entry.setEmiAmount(loan.getEmiAmount());
            entry.setInterestComponent(interestComponent);
            entry.setPrincipalComponent(principalComponent);
            entry.setRemainingBalance(balance);

            schedule.add(entry);
        }

        return schedule;
    }

    @Transactional
    public RepaymentResponse makeRepayment(
            Long userId,
            Long loanId,
            RepaymentRequest request) {
        logger.info("Repayment for loanId: {},"
                + " amount: {}", loanId, request.getAmount());

        Loan loan = loanRepository
                .findByIdAndUserId(loanId, userId)
                .orElseThrow(() -> new LoanException(
                        "Loan not found"));

        if (loan.getStatus() != LoanStatus.ACTIVE) {
            throw new LoanException(
                    "Repayments can only be made"
                            + " for ACTIVE loans");
        }

        if (request.getAmount().compareTo(
                loan.getRemainingAmount()) > 0) {
            throw new LoanException(
                    "Repayment amount exceeds remaining"
                            + " balance of ₹" + loan.getRemainingAmount());
        }

        // Verify PIN
        Boolean pinValid = userServiceClient
                .verifyPin(userId, request.getPin());
        if (!pinValid) {
            throw new LoanException("Invalid PIN");
        }

        // Check wallet balance
        WalletResponse wallet =
                walletServiceClient.getBalance(userId);
        if (wallet.getBalance().compareTo(
                request.getAmount()) < 0) {
            throw new LoanException(
                    "Insufficient wallet balance");
        }

        // Deduct from wallet
        Map<String, Object> deductReq = new HashMap<>();
        deductReq.put("userId", userId);
        deductReq.put("amount", request.getAmount());
        walletServiceClient.deductBalance(deductReq);

        // Update remaining amount
        BigDecimal newRemaining = loan.getRemainingAmount()
                .subtract(request.getAmount())
                .setScale(2, RoundingMode.HALF_UP);

        loan.setRemainingAmount(newRemaining);

        // If fully paid, close the loan
        if (newRemaining.compareTo(BigDecimal.ZERO) == 0) {
            loan.setStatus(LoanStatus.CLOSED);
            logger.info("Loan fully repaid and closed: {}",
                    loanId);
        }

        loanRepository.save(loan);

        // Save repayment record
        LoanRepayment repayment = new LoanRepayment();
        repayment.setLoan(loan);
        repayment.setAmount(request.getAmount());
        repayment.setRemainingAfterPayment(newRemaining);
        repayment.setStatus(RepaymentStatus.PAID);
        LoanRepayment savedRepayment =
                repaymentRepository.save(repayment);

        // Notify user
        try {
            notificationServiceClient.createNotification(
                    new NotificationRequest(
                            userId,
                            "Loan Repayment Successful",
                            "Repayment of ₹" + request.getAmount()
                                    + " made. Remaining: ₹" + newRemaining,
                            "LOAN"
                    ));
        } catch (Exception e) {
            logger.error("Notification failed: {}",
                    e.getMessage());
        }

        return mapToRepaymentResponse(
                savedRepayment, loanId);
    }

    public List<RepaymentResponse> getRepaymentHistory(
            Long userId, Long loanId) {
        // Verify loan belongs to user
        loanRepository.findByIdAndUserId(loanId, userId)
                .orElseThrow(() -> new LoanException(
                        "Loan not found"));

        return repaymentRepository
                .findByLoanIdOrderByPaidAtDesc(loanId)
                .stream()
                .map(r -> mapToRepaymentResponse(r, loanId))
                .collect(Collectors.toList());
    }

    public LoanAnalyticsResponse getLoanAnalytics(
            Long userId) {
        logger.debug("Getting loan analytics for"
                + " userId: {}", userId);

        List<Loan> allLoans =
                loanRepository.findByUserIdOrderByCreatedAtDesc(
                        userId);

        LoanAnalyticsResponse analytics =
                new LoanAnalyticsResponse();

        analytics.setTotalLoans(allLoans.size());
        analytics.setActiveLoans((int) allLoans.stream()
                .filter(l -> l.getStatus() == LoanStatus.ACTIVE)
                .count());
        analytics.setPendingLoans((int) allLoans.stream()
                .filter(l -> l.getStatus() == LoanStatus.PENDING)
                .count());
        analytics.setClosedLoans((int) allLoans.stream()
                .filter(l -> l.getStatus() == LoanStatus.CLOSED)
                .count());
        analytics.setRejectedLoans((int) allLoans.stream()
                .filter(l -> l.getStatus() == LoanStatus.REJECTED)
                .count());

        BigDecimal totalBorrowed = allLoans.stream()
                .filter(l -> l.getStatus() == LoanStatus.ACTIVE
                        || l.getStatus() == LoanStatus.CLOSED)
                .map(Loan::getLoanAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalOutstanding = allLoans.stream()
                .filter(l -> l.getStatus() == LoanStatus.ACTIVE)
                .map(Loan::getRemainingAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalRepaid =
                totalBorrowed.subtract(totalOutstanding);

        analytics.setTotalBorrowed(totalBorrowed);
        analytics.setTotalRepaid(totalRepaid);
        analytics.setTotalOutstanding(totalOutstanding);

        return analytics;
    }

    // Called by Admin Service to approve loan
    @Transactional
    public LoanResponse approveLoan(Long loanId) {
        logger.info("Approving loan: {}", loanId);

        Loan loan = loanRepository.findById(loanId)
                .orElseThrow(() -> new LoanException(
                        "Loan not found"));

        if (loan.getStatus() != LoanStatus.PENDING) {
            throw new LoanException(
                    "Only PENDING loans can be approved");
        }

        loan.setStatus(LoanStatus.ACTIVE);
        loan.setApprovedAt(LocalDateTime.now());
        Loan updated = loanRepository.save(loan);

        // Notify user
        try {
            notificationServiceClient.createNotification(
                    new NotificationRequest(
                            loan.getUserId(),
                            "Loan Approved",
                            "Your loan application for ₹"
                                    + loan.getLoanAmount()
                                    + " has been approved",
                            "LOAN"
                    ));
        } catch (Exception e) {
            logger.error("Notification failed: {}",
                    e.getMessage());
        }

        return mapToLoanResponse(updated);
    }

    // Called by Admin Service to reject loan
    @Transactional
    public LoanResponse rejectLoan(
            Long loanId, String reason) {
        logger.info("Rejecting loan: {}", loanId);

        Loan loan = loanRepository.findById(loanId)
                .orElseThrow(() -> new LoanException(
                        "Loan not found"));

        if (loan.getStatus() != LoanStatus.PENDING) {
            throw new LoanException(
                    "Only PENDING loans can be rejected");
        }

        loan.setStatus(LoanStatus.REJECTED);
        loan.setRejectionReason(reason);
        loan.setRejectedAt(LocalDateTime.now());
        Loan updated = loanRepository.save(loan);

        // Notify user
        try {
            notificationServiceClient.createNotification(
                    new NotificationRequest(
                            loan.getUserId(),
                            "Loan Rejected",
                            "Your loan application for ₹"
                                    + loan.getLoanAmount()
                                    + " has been rejected. Reason: " + reason,
                            "LOAN"
                    ));
        } catch (Exception e) {
            logger.error("Notification failed: {}",
                    e.getMessage());
        }

        return mapToLoanResponse(updated);
    }

    // Get all pending loans (for Admin)
    public List<LoanResponse> getPendingLoans() {
        return loanRepository
                .findByStatusOrderByCreatedAtDesc(
                        LoanStatus.PENDING)
                .stream()
                .map(this::mapToLoanResponse)
                .collect(Collectors.toList());
    }

    private LoanResponse mapToLoanResponse(Loan loan) {
        LoanResponse response = new LoanResponse();
        response.setId(loan.getId());
        response.setUserId(loan.getUserId());
        response.setUserName(loan.getUserName());
        response.setLoanAmount(loan.getLoanAmount());
        response.setPurpose(loan.getPurpose());
        response.setTenureMonths(loan.getTenureMonths());
        response.setInterestRate(loan.getInterestRate());
        response.setEmiAmount(loan.getEmiAmount());
        response.setTotalRepayableAmount(
                loan.getTotalRepayableAmount());
        response.setRemainingAmount(
                loan.getRemainingAmount());
        response.setFinancialInfo(loan.getFinancialInfo());
        response.setStatus(loan.getStatus().name());
        response.setRejectionReason(
                loan.getRejectionReason());
        response.setApprovedAt(loan.getApprovedAt());
        response.setRejectedAt(loan.getRejectedAt());
        response.setCreatedAt(loan.getCreatedAt());

        // Map documents
        List<DocumentResponse> docs =
                documentRepository
                        .findByLoanId(loan.getId())
                        .stream()
                        .map(this::mapToDocResponse)
                        .collect(Collectors.toList());
        response.setDocuments(docs);

        return response;
    }

    private DocumentResponse mapToDocResponse(
            LoanDocument doc) {
        DocumentResponse response = new DocumentResponse();
        response.setId(doc.getId());
        response.setOriginalFileName(
                doc.getOriginalFileName());
        response.setFileType(doc.getFileType());
        response.setFileSize(doc.getFileSize());
        response.setUploadedAt(doc.getUploadedAt());
        return response;
    }

    private RepaymentResponse mapToRepaymentResponse(
            LoanRepayment repayment, Long loanId) {
        RepaymentResponse response = new RepaymentResponse();
        response.setId(repayment.getId());
        response.setLoanId(loanId);
        response.setAmount(repayment.getAmount());
        response.setRemainingAfterPayment(
                repayment.getRemainingAfterPayment());
        response.setStatus(repayment.getStatus().name());
        response.setPaidAt(repayment.getPaidAt());
        return response;
    }
}