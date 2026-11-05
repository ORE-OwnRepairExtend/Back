package com.ore.backend.notification.dto;

import com.ore.backend.notification.entity.NotificationEntity;
import com.ore.backend.notification.entity.NotificationStatus;
import com.ore.backend.notification.entity.NotificationType;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
public class GetNotificationResponse {

    private UUID notificationId;

    private UUID productId;

    private String productName;

    private String productNickname;

    private UUID reminderId;

    private NotificationType notificationType;

    private NotificationStatus status;

    private String message;

    private Boolean isRead;

    private String referenceType;

    private UUID referenceId;

    private LocalDateTime createdAt;

    public static GetNotificationResponse from(NotificationEntity notification) {
        return GetNotificationResponse.builder()
                .notificationId(notification.getNotificationId())
                .productId(notification.getProduct() == null
                        ? null
                        : notification.getProduct().getProductId())
                .productName(notification.getProduct() == null
                        ? null
                        : notification.getProduct().getName())
                .productNickname(notification.getProduct() == null
                        ? null
                        : notification.getProduct().getNickname())
                .reminderId(notification.getReminder() == null
                        ? null
                        : notification.getReminder().getReminderId())
                .notificationType(notification.getNotificationType())
                .status(notification.getStatus())
                .message(notification.getMessage())
                .isRead(notification.getIsRead())
                .referenceType(notification.getReferenceType())
                .referenceId(notification.getReferenceId())
                .createdAt(notification.getCreatedAt())
                .build();
    }
}
