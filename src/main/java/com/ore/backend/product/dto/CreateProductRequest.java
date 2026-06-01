package com.ore.backend.product.dto;

import com.ore.backend.product.entity.ProductCategory;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.UUID;

@Getter
@NoArgsConstructor
public class CreateProductRequest {

    private UUID sourceId;

    @NotBlank(message = "제품명은 필수입니다.")
    private String name;

    @NotNull(message = "카테고리는 필수입니다.")
    private ProductCategory category;

    private String nickname;

    private LocalDate purchaseDate;

    @Min(value = 0, message = "보증기간은 0개월 이상이어야 합니다.")
    private Integer warrantyMonths;

    @Valid
    private ManualRequest manual;

    @Getter
    @NoArgsConstructor
    public static class ManualRequest {

        @NotBlank(message = "매뉴얼 내용은 필수입니다.")
        private String manualContent;
    }

    public boolean hasManual() {
        return manual != null
                && manual.getManualContent() != null
                && !manual.getManualContent().isBlank();
    }
}
