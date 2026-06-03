package com.ore.backend.repairReminder.dto;

import com.ore.backend.repairReminder.entity.RepairReminderEntity;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
public class UpdateRepairReminderResponse {

    private UUID reminderId;
    private String title;
    private LocalDate remindAt;
    private Boolean isDone;
    private LocalDateTime completedAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static UpdateRepairReminderResponse from(RepairReminderEntity reminder) {
        return UpdateRepairReminderResponse.builder()
                .reminderId(reminder.getReminderId())
                .title(reminder.getTitle())
                .remindAt(reminder.getRemindAt())
                .isDone(reminder.getIsDone())
                .completedAt(reminder.getCompletedAt())
                .createdAt(reminder.getCreatedAt())
                .updatedAt(reminder.getUpdatedAt())
                .build();
    }
}