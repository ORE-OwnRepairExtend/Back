package com.ore.backend.product.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.util.UUID;

@Getter
@Builder
public class GetProductWarrantyResponse {

    private UUID productId;
    private LocalDate purchaseDate;
    private Integer warrantyMonths;
    private LocalDate warrantyEndDate;
    private Long remainingDays;
}