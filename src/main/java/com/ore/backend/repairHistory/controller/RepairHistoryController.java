package com.ore.backend.repairHistory.controller;

import com.ore.backend.repairHistory.dto.CreateRepairHistoryResponse;
import com.ore.backend.repairHistory.service.RepairHistoryService;
import com.ore.backend.security.JwtProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.UUID;
@Slf4j
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
        System.out.println("🔥🔥🔥 수리이력 컨트롤러 진입");
        System.out.println("🔥 productId = " + productId);
        System.out.println("🔥 image = " + (image == null ? "null" : image.getOriginalFilename()));

        log.info("[수리이력 등록 요청] productId={}, date={}, title={}, content={}, serviceCenter={}, cost={}",
                productId, date, title, content, serviceCenter, cost);

        log.info("[수리이력 이미지 확인] imageNull={}, imageEmpty={}, originalFilename={}, contentType={}, size={}",
                image == null,
                image != null && image.isEmpty(),
                image != null ? image.getOriginalFilename() : null,
                image != null ? image.getContentType() : null,
                image != null ? image.getSize() : null
        );

        String accessToken = authorizationHeader.replace("Bearer ", "");

        UUID userId = jwtProvider.getUserId(accessToken);
        log.info("[수리이력 사용자 확인] userId={}", userId);
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
}