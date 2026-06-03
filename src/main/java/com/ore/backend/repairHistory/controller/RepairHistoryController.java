package com.ore.backend.repairHistory.controller;

import com.ore.backend.repairHistory.dto.CreateRepairHistoryResponse;
import com.ore.backend.repairHistory.dto.GetRepairHistoryResponse;
import com.ore.backend.repairHistory.service.RepairHistoryService;
import com.ore.backend.security.JwtProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/products/{productId}/repairs")
public class RepairHistoryController {

    private final RepairHistoryService repairHistoryService;
    private final JwtProvider jwtProvider;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public CreateRepairHistoryResponse createRepairHistory(
            @RequestHeader("Authorization") String authorizationHeader,
            @PathVariable UUID productId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam String title,
            @RequestParam String content,
            @RequestParam(required = false) String serviceCenter,
            @RequestParam Integer cost,
            @RequestPart(value = "image", required = false) MultipartFile image
    ) {
        String accessToken = authorizationHeader.replace("Bearer ", "");

        UUID userId = jwtProvider.getUserId(accessToken);

        return repairHistoryService.createRepairHistory(
                userId,
                productId,
                date,
                title,
                content,
                serviceCenter,
                cost,
                image
        );
    }

    @GetMapping
    public List<GetRepairHistoryResponse> getRepairHistories(
            @RequestHeader("Authorization") String authorizationHeader,
            @PathVariable UUID productId
    ) {
        String accessToken = authorizationHeader.replace("Bearer ", "");
        UUID userId = jwtProvider.getUserId(accessToken);

        return repairHistoryService.getRepairHistories(userId, productId);
    }
}