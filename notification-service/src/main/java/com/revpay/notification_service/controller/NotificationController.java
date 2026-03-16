package com.revpay.notification_service.controller;

import com.revpay.notification_service.dto.CreateNotificationRequest;
import com.revpay.notification_service.dto.NotificationResponse;
import com.revpay.notification_service.service.NotificationService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(
            NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    // Internal endpoint called by other services
    @PostMapping("/create")
    public ResponseEntity<NotificationResponse> create(
            @Valid @RequestBody
            CreateNotificationRequest request) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(notificationService
                        .createNotification(request));
    }

    // Get all notifications for user
    @GetMapping("/{userId}")
    public ResponseEntity<List<NotificationResponse>>
    getAll(@PathVariable Long userId) {
        return ResponseEntity.ok(
                notificationService
                        .getAllNotifications(userId));
    }

    // Get unread notifications
    @GetMapping("/{userId}/unread")
    public ResponseEntity<List<NotificationResponse>>
    getUnread(@PathVariable Long userId) {
        return ResponseEntity.ok(
                notificationService
                        .getUnreadNotifications(userId));
    }

    // Get notifications by type
    @GetMapping("/{userId}/type/{type}")
    public ResponseEntity<List<NotificationResponse>>
    getByType(
            @PathVariable Long userId,
            @PathVariable String type) {
        return ResponseEntity.ok(
                notificationService.getByType(userId, type));
    }

    // Get unread count
    @GetMapping("/{userId}/count")
    public ResponseEntity<Map<String, Long>> getCount(
            @PathVariable Long userId) {
        return ResponseEntity.ok(Map.of(
                "unreadCount",
                notificationService.getUnreadCount(userId)));
    }

    // Mark single notification as read
    @PutMapping("/{userId}/read/{notificationId}")
    public ResponseEntity<NotificationResponse> markAsRead(
            @PathVariable Long userId,
            @PathVariable Long notificationId) {
        return ResponseEntity.ok(
                notificationService
                        .markAsRead(userId, notificationId));
    }

    // Mark all as read
    @PutMapping("/{userId}/read-all")
    public ResponseEntity<String> markAllAsRead(
            @PathVariable Long userId) {
        notificationService.markAllAsRead(userId);
        return ResponseEntity.ok(
                "All notifications marked as read");
    }

    // Delete notification
    @DeleteMapping("/{userId}/{notificationId}")
    public ResponseEntity<String> delete(
            @PathVariable Long userId,
            @PathVariable Long notificationId) {
        notificationService
                .deleteNotification(userId, notificationId);
        return ResponseEntity.ok(
                "Notification deleted successfully");
    }
}