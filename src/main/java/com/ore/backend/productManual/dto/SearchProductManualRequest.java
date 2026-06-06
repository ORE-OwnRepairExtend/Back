package com.ore.backend.productManual.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@NoArgsConstructor
public class SearchProductManualRequest {

    @NotNull(message = "입력 데이터 ID는 필수입니다.")
    private UUID sourceId;
}