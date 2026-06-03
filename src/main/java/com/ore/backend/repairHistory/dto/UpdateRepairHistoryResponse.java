package com.ore.backend.repairHistory.dto;

import com.ore.backend.repairHistory.entity.RepairHistoryEntity;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.util.UUID;

@Getter
@Builder
public class UpdateRepairHistoryResponse {

    private UUID repairId;
    private String title;
    private LocalDate date;
    private String content;
    private String serviceCenter;
    private Integer cost;
    private String imageUrl;

    public static UpdateRepairHistoryResponse of(
            RepairHistoryEntity repairHistory,
            String imageUrl
    ) {
        return UpdateRepairHistoryResponse.builder()
                .repairId(repairHistory.getRepairId())
                .title(repairHistory.getTitle())
                .date(repairHistory.getDate())
                .content(repairHistory.getContent())
                .serviceCenter(repairHistory.getServiceCenter())
                .cost(repairHistory.getCost())
                .imageUrl(imageUrl)
                .build();
    }
}