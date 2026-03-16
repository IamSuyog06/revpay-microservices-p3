package com.revpay.notification_service.repository;

import com.revpay.notification_service.entity.NotificationPreference;
import com.revpay.notification_service.enums.NotificationType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface NotificationPreferenceRepository
        extends JpaRepository<NotificationPreference, Long> {

    List<NotificationPreference> findByUserId(Long userId);

    Optional<NotificationPreference> findByUserIdAndType(
            Long userId, NotificationType type);

    boolean existsByUserId(Long userId);
}