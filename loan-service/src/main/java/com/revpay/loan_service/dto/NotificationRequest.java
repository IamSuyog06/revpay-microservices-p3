package com.revpay.loan_service.dto;

public class NotificationRequest {

    private Long userId;
    private String title;
    private String message;
    private String type;

    public NotificationRequest() {}

    public NotificationRequest(Long userId,
                               String title, String message, String type) {
        this.userId = userId;
        this.title = title;
        this.message = message;
        this.type = type;
    }

    // Getters
    public Long getUserId() { return userId; }
    public String getTitle() { return title; }
    public String getMessage() { return message; }
    public String getType() { return type; }

    // Setters
    public void setUserId(Long userId) { this.userId = userId; }
    public void setTitle(String title) { this.title = title; }
    public void setMessage(String message) { this.message = message; }
    public void setType(String type) { this.type = type; }
}