package com.revpay.user_service.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public class SetPinRequest {

    @NotBlank(message = "PIN is required")
    @Pattern(
            regexp = "^[0-9]{4,6}$",
            message = "PIN must be 4 to 6 digits"
    )
    private String pin;

    public SetPinRequest() {}

    // Getter
    public String getPin() { return pin; }

    // Setter
    public void setPin(String pin) { this.pin = pin; }
}