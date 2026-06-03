package com.ore.backend.repairReminder.dto;

import com.ore.backend.repairReminder.entity.RepairReminderEntity;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
public class GetProductRepairReminderResponse {

    private UUID reminderId;
    private String title;
    private LocalDate remindAt;
    private Boolean isDone;
    private LocalDateTime createdAt;

    public static GetProductRepairReminderResponse from(RepairReminderEntity reminder) {
        return GetProductRepairReminderResponse.builder()
                .reminderId(reminder.getReminderId())
                .title(reminder.getTitle())
                .remindAt(reminder.getRemindAt())
                .isDone(reminder.getIsDone())
                .createdAt(reminder.getCreatedAt())
                .build();
    }
}