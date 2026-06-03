package com.ore.backend.repairHistory.dto;

import com.ore.backend.repairHistory.entity.RepairHistoryEntity;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
public class GetRepairHistoryResponse {

    private UUID repairId;
    private LocalDate date;
    private String title;
    private Integer cost;
    private LocalDateTime createdAt;

    public static GetRepairHistoryResponse from(RepairHistoryEntity repairHistory) {
        return GetRepairHistoryResponse.builder()
                .repairId(repairHistory.getRepairId())
                .date(repairHistory.getDate())
                .title(repairHistory.getTitle())
                .cost(repairHistory.getCost())
                .createdAt(repairHistory.getCreatedAt())
                .build();
    }
}