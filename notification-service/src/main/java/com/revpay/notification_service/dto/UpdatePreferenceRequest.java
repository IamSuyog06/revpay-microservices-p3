package com.revpay.notification_service.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class UpdatePreferenceRequest {

    @NotBlank(message = "Type is required")
    private String type;

    @NotNull(message = "Enabled status is required")
    private Boolean enabled;

    public UpdatePreferenceRequest() {}

    // Getters
    public String getType() { return type; }
    public Boolean getEnabled() { return enabled; }

    // Setters
    public void setType(String type) { this.type = type; }
    public void setEnabled(Boolean enabled) { this.enabled = enabled; }
}