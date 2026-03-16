package com.revpay.notification_service.controller;

import com.revpay.notification_service.dto.NotificationPreferenceResponse;
import com.revpay.notification_service.dto.UpdatePreferenceRequest;
import com.revpay.notification_service.service.NotificationPreferenceService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications/preferences")
public class NotificationPreferenceController {

    private final NotificationPreferenceService
            preferenceService;

    public NotificationPreferenceController(
            NotificationPreferenceService
                    preferenceService) {
        this.preferenceService = preferenceService;
    }

    // Get all preferences for user
    @GetMapping("/{userId}")
    public ResponseEntity<List<NotificationPreferenceResponse>>
    getPreferences(@PathVariable Long userId) {
        return ResponseEntity.ok(
                preferenceService.getPreferences(userId));
    }

    // Update single preference
    @PutMapping("/{userId}")
    public ResponseEntity<NotificationPreferenceResponse>
    updatePreference(
            @PathVariable Long userId,
            @Valid @RequestBody
            UpdatePreferenceRequest request) {
        return ResponseEntity.ok(
                preferenceService
                        .updatePreference(userId, request));
    }

    // Enable all notifications
    @PutMapping("/{userId}/enable-all")
    public ResponseEntity<List<NotificationPreferenceResponse>>
    enableAll(@PathVariable Long userId) {
        return ResponseEntity.ok(
                preferenceService.enableAll(userId));
    }

    // Disable all notifications
    @PutMapping("/{userId}/disable-all")
    public ResponseEntity<List<NotificationPreferenceResponse>>
    disableAll(@PathVariable Long userId) {
        return ResponseEntity.ok(
                preferenceService.disableAll(userId));
    }

    // Called by User Service when user registers
    @PostMapping("/{userId}/init")
    public ResponseEntity<String> initPreferences(
            @PathVariable Long userId) {
        preferenceService.initializePreferences(userId);
        return ResponseEntity.ok(
                "Preferences initialized");
    }
}