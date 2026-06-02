package com.ore.backend.notification.repository;

import com.ore.backend.notification.entity.NotificationEntity;
import com.ore.backend.notification.entity.NotificationStatus;
import com.ore.backend.notification.entity.NotificationType;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface NotificationRepository extends JpaRepository<NotificationEntity, UUID> {

    @EntityGraph(attributePaths = {"product", "reminder"})
    List<NotificationEntity> findByUser_UserIdOrderByCreatedAtDesc(UUID userId);

    boolean existsByProduct_ProductIdAndNotificationTypeAndStatus(
            UUID productId,
            NotificationType notificationType,
            NotificationStatus status
    );

    boolean existsByReminder_ReminderIdAndNotificationTypeAndStatus(
            UUID reminderId,
            NotificationType notificationType,
            NotificationStatus status
    );
}