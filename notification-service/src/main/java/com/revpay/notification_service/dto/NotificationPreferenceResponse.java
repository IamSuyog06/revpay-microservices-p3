package com.revpay.notification_service.dto;

public class NotificationPreferenceResponse {

    private Long id;
    private Long userId;
    private String type;
    private boolean enabled;

    public NotificationPreferenceResponse() {}

    // Getters
    public Long getId() { return id; }
    public Long getUserId() { return userId; }
    public String getType() { return type; }
    public boolean isEnabled() { return enabled; }

    // Setters
    public void setId(Long id) { this.id = id; }
    public void setUserId(Long userId) { this.userId = userId; }
    public void setType(String type) { this.type = type; }
    public void setEnabled(boolean enabled) { this.enabled = enabled; }
}