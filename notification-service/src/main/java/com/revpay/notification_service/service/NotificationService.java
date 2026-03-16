package com.revpay.notification_service.service;

import com.revpay.notification_service.dto.CreateNotificationRequest;
import com.revpay.notification_service.dto.NotificationResponse;
import com.revpay.notification_service.entity.Notification;
import com.revpay.notification_service.entity.NotificationPreference;
import com.revpay.notification_service.enums.NotificationType;
import com.revpay.notification_service.exception.NotificationException;
import com.revpay.notification_service.repository.NotificationPreferenceRepository;
import com.revpay.notification_service.repository.NotificationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class NotificationService {

    private static final Logger logger =
            LoggerFactory.getLogger(NotificationService.class);

    private final NotificationRepository
            notificationRepository;
    private final NotificationPreferenceRepository
            preferenceRepository;

    public NotificationService(
            NotificationRepository notificationRepository,
            NotificationPreferenceRepository
                    preferenceRepository) {
        this.notificationRepository = notificationRepository;
        this.preferenceRepository = preferenceRepository;
    }

    // Called by other services via Feign
    public NotificationResponse createNotification(
            CreateNotificationRequest request) {
        logger.info("Creating notification for"
                        + " userId: {}, type: {}",
                request.getUserId(), request.getType());

        NotificationType type;
        try {
            type = NotificationType
                    .valueOf(request.getType());
        } catch (IllegalArgumentException e) {
            logger.warn("Invalid notification type: {}",
                    request.getType());
            type = NotificationType.GENERAL;
        }

        // Check user preference for this type
        // If preference exists and is disabled
        // → skip notification
        Optional<NotificationPreference> preference =
                preferenceRepository.findByUserIdAndType(
                        request.getUserId(), type);

        if (preference.isPresent()
                && !preference.get().isEnabled()) {
            logger.info("Notification skipped for"
                            + " userId: {} type: {} (disabled)",
                    request.getUserId(), type);
            // Return empty response
            // without saving notification
            NotificationResponse skipped =
                    new NotificationResponse();
            skipped.setUserId(request.getUserId());
            skipped.setType(type.name());
            skipped.setTitle(request.getTitle());
            skipped.setMessage(request.getMessage());
            return skipped;
        }

        // Save notification
        Notification notification = new Notification(
                request.getUserId(),
                request.getTitle(),
                request.getMessage(),
                type
        );

        Notification saved =
                notificationRepository.save(notification);

        logger.info("Notification created: id={}",
                saved.getId());
        return mapToResponse(saved);
    }

    public List<NotificationResponse> getAllNotifications(
            Long userId) {
        logger.debug("Fetching all notifications"
                + " for userId: {}", userId);
        return notificationRepository
                .findByUserIdOrderByCreatedAtDesc(userId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public List<NotificationResponse> getUnreadNotifications(
            Long userId) {
        logger.debug("Fetching unread notifications"
                + " for userId: {}", userId);
        return notificationRepository
                .findByUserIdAndIsReadFalseOrderByCreatedAtDesc(
                        userId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public List<NotificationResponse> getByType(
            Long userId, String type) {
        NotificationType notificationType =
                NotificationType.valueOf(type);
        return notificationRepository
                .findByUserIdAndTypeOrderByCreatedAtDesc(
                        userId, notificationType)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public Long getUnreadCount(Long userId) {
        return notificationRepository
                .countByUserIdAndIsReadFalse(userId);
    }

    @Transactional
    public NotificationResponse markAsRead(
            Long userId, Long notificationId) {
        logger.info("Marking notification: {} as read",
                notificationId);

        Notification notification = notificationRepository
                .findById(notificationId)
                .orElseThrow(() -> new NotificationException(
                        "Notification not found"));

        if (!notification.getUserId().equals(userId)) {
            throw new NotificationException(
                    "Notification does not belong to user");
        }

        notification.setRead(true);
        Notification updated =
                notificationRepository.save(notification);
        return mapToResponse(updated);
    }

    @Transactional
    public void markAllAsRead(Long userId) {
        logger.info("Marking all notifications as read"
                + " for userId: {}", userId);
        notificationRepository.markAllAsRead(userId);
    }

    @Transactional
    public void deleteNotification(
            Long userId, Long notificationId) {
        logger.info("Deleting notification: {}",
                notificationId);

        Notification notification = notificationRepository
                .findById(notificationId)
                .orElseThrow(() -> new NotificationException(
                        "Notification not found"));

        if (!notification.getUserId().equals(userId)) {
            throw new NotificationException(
                    "Notification does not belong to user");
        }

        notificationRepository.delete(notification);
        logger.info("Notification deleted: {}",
                notificationId);
    }

    private NotificationResponse mapToResponse(
            Notification n) {
        NotificationResponse response =
                new NotificationResponse();
        response.setId(n.getId());
        response.setUserId(n.getUserId());
        response.setTitle(n.getTitle());
        response.setMessage(n.getMessage());
        response.setType(n.getType().name());
        response.setRead(n.isRead());
        response.setCreatedAt(n.getCreatedAt());
        return response;
    }
}