package com.ore.backend.repairHistory.dto;

import com.ore.backend.repairHistory.entity.RepairHistoryEntity;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
public class GetRepairHistoryDetailResponse {

    private UUID repairId;
    private String title;
    private LocalDate date;
    private String content;
    private Integer cost;
    private String serviceCenter;
    private String imageUrl;
    private LocalDateTime createdAt;

    public static GetRepairHistoryDetailResponse of(
            RepairHistoryEntity repairHistory,
            String imageUrl
    ) {
        return GetRepairHistoryDetailResponse.builder()
                .repairId(repairHistory.getRepairId())
                .title(repairHistory.getTitle())
                .date(repairHistory.getDate())
                .content(repairHistory.getContent())
                .cost(repairHistory.getCost())
                .serviceCenter(repairHistory.getServiceCenter())
                .imageUrl(imageUrl)
                .createdAt(repairHistory.getCreatedAt())
                .build();
    }
}