package com.ore.backend.repairReminder.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@NoArgsConstructor
public class UpdateRepairReminderRequest {

    private String title;
    private LocalDate remindAt;

    public boolean hasUpdateValue() {
        return title != null || remindAt != null;
    }
}