package com.ore.backend.notification.dto;

import com.ore.backend.notification.entity.NotificationEntity;
import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Getter
@Builder
public class UpdateNotificationReadResponse {

    private UUID notificationId;
    private Boolean isRead;

    public static UpdateNotificationReadResponse from(NotificationEntity notification) {
        return UpdateNotificationReadResponse.builder()
                .notificationId(notification.getNotificationId())
                .isRead(notification.getIsRead())
                .build();
    }
}
