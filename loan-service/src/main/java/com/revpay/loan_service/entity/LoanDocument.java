package com.revpay.loan_service.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "loan_documents")
public class LoanDocument {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "loan_id", nullable = false)
    private Loan loan;

    @Column(nullable = false)
    private String fileName;

    @Column(nullable = false)
    private String originalFileName;

    @Column(nullable = false)
    private String filePath;

    @Column(nullable = false)
    private String fileType;

    private Long fileSize;

    @Column(updatable = false)
    private LocalDateTime uploadedAt;

    @PrePersist
    protected void onCreate() {
        uploadedAt = LocalDateTime.now();
    }

    public LoanDocument() {}

    // Getters
    public Long getId() { return id; }
    public Loan getLoan() { return loan; }
    public String getFileName() { return fileName; }
    public String getOriginalFileName() { return originalFileName; }
    public String getFilePath() { return filePath; }
    public String getFileType() { return fileType; }
    public Long getFileSize() { return fileSize; }
    public LocalDateTime getUploadedAt() { return uploadedAt; }

    // Setters
    public void setId(Long id) { this.id = id; }
    public void setLoan(Loan loan) { this.loan = loan; }
    public void setFileName(String fileName) { this.fileName = fileName; }
    public void setOriginalFileName(String originalFileName) { this.originalFileName = originalFileName; }
    public void setFilePath(String filePath) { this.filePath = filePath; }
    public void setFileType(String fileType) { this.fileType = fileType; }
    public void setFileSize(Long fileSize) { this.fileSize = fileSize; }
    public void setUploadedAt(LocalDateTime uploadedAt) { this.uploadedAt = uploadedAt; }
}