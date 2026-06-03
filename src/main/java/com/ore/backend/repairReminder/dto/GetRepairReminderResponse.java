package com.ore.backend.repairReminder.dto;

import com.ore.backend.repairReminder.entity.RepairReminderEntity;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
public class GetRepairReminderResponse {

    private UUID reminderId;
    private UUID productId;
    private String productName;
    private String title;
    private LocalDate remindAt;
    private Boolean isDone;
    private LocalDateTime completedAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static GetRepairReminderResponse from(RepairReminderEntity reminder) {
        return GetRepairReminderResponse.builder()
                .reminderId(reminder.getReminderId())
                .productId(reminder.getProduct().getProductId())
                .productName(reminder.getProduct().getName())
                .title(reminder.getTitle())
                .remindAt(reminder.getRemindAt())
                .isDone(reminder.getIsDone())
                .completedAt(reminder.getCompletedAt())
                .createdAt(reminder.getCreatedAt())
                .updatedAt(reminder.getUpdatedAt())
                .build();
    }
}