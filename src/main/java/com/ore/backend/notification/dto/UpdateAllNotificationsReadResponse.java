package com.ore.backend.notification.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UpdateAllNotificationsReadResponse {

    private long updatedCount;

    public static UpdateAllNotificationsReadResponse from(long updatedCount) {
        return UpdateAllNotificationsReadResponse.builder()
                .updatedCount(updatedCount)
                .build();
    }
}
