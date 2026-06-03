package com.ore.backend.repairHistory.dto;

import com.ore.backend.repairHistory.entity.RepairHistoryEntity;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
public class CreateRepairHistoryResponse {

    private UUID repairId;
    private UUID productId;
    private LocalDate date;
    private String title;
    private String content;
    private String serviceCenter;
    private Integer cost;
    private String imageUrl;
    private LocalDateTime createdAt;

    public static CreateRepairHistoryResponse from(
            RepairHistoryEntity repairHistory,
            String imageUrl
    ) {
        return CreateRepairHistoryResponse.builder()
                .repairId(repairHistory.getRepairId())
                .productId(repairHistory.getProduct().getProductId())
                .date(repairHistory.getDate())
                .title(repairHistory.getTitle())
                .content(repairHistory.getContent())
                .serviceCenter(repairHistory.getServiceCenter())
                .cost(repairHistory.getCost())
                .imageUrl(imageUrl)
                .createdAt(repairHistory.getCreatedAt())
                .build();
    }
}