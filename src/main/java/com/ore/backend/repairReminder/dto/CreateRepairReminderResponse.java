package com.ore.backend.repairReminder.dto;

import com.ore.backend.repairReminder.entity.RepairReminderEntity;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
public class CreateRepairReminderResponse {

    private UUID reminderId;
    private UUID productId;
    private UUID repairId;
    private String title;
    private LocalDate remindAt;
    private Boolean isDone;
    private LocalDate completedDate;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static CreateRepairReminderResponse from(RepairReminderEntity reminder) {
        return CreateRepairReminderResponse.builder()
                .reminderId(reminder.getReminderId())
                .productId(reminder.getProduct().getProductId())
                .repairId(
                        reminder.getRepairHistory() == null
                                ? null
                                : reminder.getRepairHistory().getRepairId()
                )
                .title(reminder.getTitle())
                .remindAt(reminder.getRemindAt())
                .isDone(reminder.getIsDone())
                .completedDate(reminder.getCompletedDate())
                .createdAt(reminder.getCreatedAt())
                .updatedAt(reminder.getUpdatedAt())
                .build();
    }
}