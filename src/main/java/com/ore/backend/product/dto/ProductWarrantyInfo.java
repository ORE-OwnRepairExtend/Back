package com.ore.backend.product.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@Builder
public class ProductWarrantyInfo {

    private LocalDate purchaseDate;
    private Integer warrantyMonths;
    private LocalDate warrantyEndDate;
    private long remainingDays;
}
