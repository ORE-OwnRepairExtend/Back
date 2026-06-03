package com.ore.backend.repairReminder.dto;

import com.ore.backend.repairReminder.entity.RepairReminderEntity;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
public class UpdateRepairReminderDoneResponse {

    private UUID reminderId;
    private Boolean isDone;
    private LocalDate completedDate;
    private LocalDateTime updatedAt;

    public static UpdateRepairReminderDoneResponse from(RepairReminderEntity reminder) {
        return UpdateRepairReminderDoneResponse.builder()
                .reminderId(reminder.getReminderId())
                .isDone(reminder.getIsDone())
                .completedDate(reminder.getCompletedDate())
                .updatedAt(reminder.getUpdatedAt())
                .build();
    }
}