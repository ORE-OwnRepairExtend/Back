package com.ore.backend.repairReminder.controller;

import com.ore.backend.repairReminder.dto.*;
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

    @PatchMapping("/products/{productId}/repair-reminders/{reminderId}")
    public UpdateRepairReminderResponse updateRepairReminder(
            @RequestHeader("Authorization") String authorizationHeader,
            @PathVariable UUID productId,
            @PathVariable UUID reminderId,
            @RequestBody UpdateRepairReminderRequest request
    ) {
        String accessToken = authorizationHeader.replace("Bearer ", "");
        UUID userId = jwtProvider.getUserId(accessToken);

        return repairReminderService.updateRepairReminder(
                userId,
                productId,
                reminderId,
                request
        );
    }

    @PatchMapping("/products/{productId}/repair-reminders/{reminderId}/done")
    public UpdateRepairReminderDoneResponse updateRepairReminderDone(
            @RequestHeader("Authorization") String authorizationHeader,
            @PathVariable UUID productId,
            @PathVariable UUID reminderId,
            @RequestBody UpdateRepairReminderDoneRequest request
    ) {
        String accessToken = authorizationHeader.replace("Bearer ", "");
        UUID userId = jwtProvider.getUserId(accessToken);

        return repairReminderService.updateRepairReminderDone(
                userId,
                productId,
                reminderId,
                request
        );
    }
}