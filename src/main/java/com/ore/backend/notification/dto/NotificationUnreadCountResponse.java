package com.ore.backend.notification.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class NotificationUnreadCountResponse {

    private long unreadCount;

    public static NotificationUnreadCountResponse from(long unreadCount) {
        return NotificationUnreadCountResponse.builder()
                .unreadCount(unreadCount)
                .build();
    }
}