package com.revpay.notification_service.service;

import com.revpay.notification_service.dto.NotificationPreferenceResponse;
import com.revpay.notification_service.dto.UpdatePreferenceRequest;
import com.revpay.notification_service.entity.NotificationPreference;
import com.revpay.notification_service.enums.NotificationType;
import com.revpay.notification_service.repository.NotificationPreferenceRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class NotificationPreferenceService {

    private static final Logger logger =
            LoggerFactory.getLogger(
                    NotificationPreferenceService.class);

    private final NotificationPreferenceRepository
            preferenceRepository;

    public NotificationPreferenceService(
            NotificationPreferenceRepository
                    preferenceRepository) {
        this.preferenceRepository = preferenceRepository;
    }

    // Initialize preferences for new user
    // All disabled by default
    @Transactional
    public void initializePreferences(Long userId) {
        logger.info("Initializing preferences"
                + " for userId: {}", userId);

        if (preferenceRepository.existsByUserId(userId)) {
            logger.info("Preferences already exist"
                    + " for userId: {}", userId);
            return;
        }

        // Create disabled preference for each type
        Arrays.stream(NotificationType.values())
                .forEach(type -> {
                    NotificationPreference preference =
                            new NotificationPreference(
                                    userId, type, false);
                    preferenceRepository.save(preference);
                });

        logger.info("Preferences initialized"
                + " for userId: {}", userId);
    }

    public List<NotificationPreferenceResponse>
    getPreferences(Long userId) {
        logger.debug("Fetching preferences"
                + " for userId: {}", userId);

        // Initialize if not exists
        if (!preferenceRepository.existsByUserId(userId)) {
            initializePreferences(userId);
        }

        return preferenceRepository
                .findByUserId(userId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public NotificationPreferenceResponse updatePreference(
            Long userId,
            UpdatePreferenceRequest request) {
        logger.info("Updating preference for userId: {},"
                        + " type: {}, enabled: {}",
                userId, request.getType(),
                request.getEnabled());

        NotificationType type =
                NotificationType.valueOf(request.getType());

        Optional<NotificationPreference> existing =
                preferenceRepository
                        .findByUserIdAndType(userId, type);

        NotificationPreference preference;

        if (existing.isPresent()) {
            preference = existing.get();
            preference.setEnabled(request.getEnabled());
        } else {
            preference = new NotificationPreference(
                    userId, type, request.getEnabled());
        }

        NotificationPreference saved =
                preferenceRepository.save(preference);

        logger.info("Preference updated: userId={}"
                        + " type={} enabled={}",
                userId, type, request.getEnabled());

        return mapToResponse(saved);
    }

    @Transactional
    public List<NotificationPreferenceResponse>
    enableAll(Long userId) {
        logger.info("Enabling all notifications"
                + " for userId: {}", userId);

        List<NotificationPreference> preferences =
                preferenceRepository.findByUserId(userId);

        if (preferences.isEmpty()) {
            initializePreferences(userId);
            preferences =
                    preferenceRepository.findByUserId(userId);
        }

        preferences.forEach(p -> p.setEnabled(true));
        preferenceRepository.saveAll(preferences);

        return preferences.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public List<NotificationPreferenceResponse>
    disableAll(Long userId) {
        logger.info("Disabling all notifications"
                + " for userId: {}", userId);

        List<NotificationPreference> preferences =
                preferenceRepository.findByUserId(userId);

        if (preferences.isEmpty()) {
            initializePreferences(userId);
            preferences =
                    preferenceRepository.findByUserId(userId);
        }

        preferences.forEach(p -> p.setEnabled(false));
        preferenceRepository.saveAll(preferences);

        return preferences.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private NotificationPreferenceResponse mapToResponse(
            NotificationPreference p) {
        NotificationPreferenceResponse response =
                new NotificationPreferenceResponse();
        response.setId(p.getId());
        response.setUserId(p.getUserId());
        response.setType(p.getType().name());
        response.setEnabled(p.isEnabled());
        return response;
    }
}