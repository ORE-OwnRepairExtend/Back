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
    private String content;
    private String serviceCenter;
    private Integer cost;
    private String imageUrl;
    private Boolean isEditable;
    private LocalDateTime createdAt;

    public static GetRepairHistoryResponse of(
            RepairHistoryEntity repairHistory,
            String imageUrl,
            boolean isEditable
    ) {
        return GetRepairHistoryResponse.builder()
                .repairId(repairHistory.getRepairId())
                .date(repairHistory.getDate())
                .title(repairHistory.getTitle())
                .content(repairHistory.getContent())
                .serviceCenter(repairHistory.getServiceCenter())
                .cost(repairHistory.getCost())
                .imageUrl(imageUrl)
                .isEditable(isEditable)
                .createdAt(repairHistory.getCreatedAt())
                .build();
    }
}
