package com.ore.backend.repairReminder.controller;

import com.ore.backend.repairReminder.dto.CreateRepairReminderRequest;
import com.ore.backend.repairReminder.dto.CreateRepairReminderResponse;
import com.ore.backend.repairReminder.service.RepairReminderService;
import com.ore.backend.security.JwtProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/products/{productId}/repair-reminders")
public class RepairReminderController {

    private final RepairReminderService repairReminderService;
    private final JwtProvider jwtProvider;

    @PostMapping
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
}