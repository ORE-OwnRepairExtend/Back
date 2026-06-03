package com.ore.backend.repairReminder.controller;

import com.ore.backend.repairReminder.dto.CreateRepairReminderRequest;
import com.ore.backend.repairReminder.dto.CreateRepairReminderResponse;
import com.ore.backend.repairReminder.dto.GetProductRepairReminderResponse;
import com.ore.backend.repairReminder.dto.GetRepairReminderResponse;
import com.ore.backend.repairReminder.service.RepairReminderService;
import com.ore.backend.security.JwtProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class RepairReminderController {

    private final RepairReminderService repairReminderService;
    private final JwtProvider jwtProvider;

    @PostMapping("/products/{productId}/repair-reminders")
    public CreateRepairReminderResponse createRepairReminder(
            @RequestHeader("Authorization") String authorizationHeader,
            @PathVariable UUID productId,
            @RequestBody CreateRepairReminderRequest request
    ) {
        String accessToken = authorizationHeader.replace("Bearer ", "");
        UUID userId = jwtProvider.getUserId(accessToken);

        return repairReminderService.createRepairReminder(
                userId,
                productId,
                request
        );
    }

    @GetMapping("/repair-reminders")
    public List<GetRepairReminderResponse> getRepairReminders(
            @RequestHeader("Authorization") String authorizationHeader
    ) {
        String accessToken = authorizationHeader.replace("Bearer ", "");
        UUID userId = jwtProvider.getUserId(accessToken);

        return repairReminderService.getRepairReminders(userId);
    }

    @GetMapping("/products/{productId}/repair-reminders")
    public List<GetProductRepairReminderResponse> getProductRepairReminders(
            @RequestHeader("Authorization") String authorizationHeader,
            @PathVariable UUID productId
    ) {
        String accessToken = authorizationHeader.replace("Bearer ", "");
        UUID userId = jwtProvider.getUserId(accessToken);

        return repairReminderService.getProductRepairReminders(userId, productId);
    }
}