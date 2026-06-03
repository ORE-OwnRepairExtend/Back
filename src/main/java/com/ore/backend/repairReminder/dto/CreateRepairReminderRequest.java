package com.ore.backend.repairReminder.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.UUID;

@Getter
@NoArgsConstructor
public class CreateRepairReminderRequest {

    private String title;
    private LocalDate remindAt;
    private UUID repairId;
}