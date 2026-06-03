package com.ore.backend.repairReminder.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@NoArgsConstructor
public class UpdateRepairReminderDoneRequest {

    private Boolean isDone;
    private LocalDate completedDate;
}