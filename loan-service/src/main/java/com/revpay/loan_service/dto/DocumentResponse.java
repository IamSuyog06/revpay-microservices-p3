package com.revpay.loan_service.dto;

import java.time.LocalDateTime;

public class DocumentResponse {

    private Long id;
    private String originalFileName;
    private String fileType;
    private Long fileSize;
    private LocalDateTime uploadedAt;

    public DocumentResponse() {}

    // Getters
    public Long getId() { return id; }
    public String getOriginalFileName() { return originalFileName; }
    public String getFileType() { return fileType; }
    public Long getFileSize() { return fileSize; }
    public LocalDateTime getUploadedAt() { return uploadedAt; }

    // Setters
    public void setId(Long id) { this.id = id; }
    public void setOriginalFileName(String originalFileName) { this.originalFileName = originalFileName; }
    public void setFileType(String fileType) { this.fileType = fileType; }
    public void setFileSize(Long fileSize) { this.fileSize = fileSize; }
    public void setUploadedAt(LocalDateTime uploadedAt) { this.uploadedAt = uploadedAt; }
}