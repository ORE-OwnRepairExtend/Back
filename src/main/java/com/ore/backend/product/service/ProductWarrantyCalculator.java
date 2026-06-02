package com.ore.backend.product.service;

import com.ore.backend.global.exception.CustomException;
import com.ore.backend.global.exception.ErrorCode;
import com.ore.backend.product.dto.ProductWarrantyInfo;
import com.ore.backend.product.entity.ProductEntity;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Component
public class ProductWarrantyCalculator {

    public ProductWarrantyInfo calculate(ProductEntity product) {
        validateWarrantyInfo(product);

        LocalDate purchaseDate = product.getPurchaseDate();
        Integer warrantyMonths = product.getWarrantyMonth();

        // 보증 만료일 = 구매일 + 보증 개월 수
        LocalDate warrantyEndDate = purchaseDate.plusMonths(warrantyMonths);

        // 현재 날짜를 기준으로 보증 만료일까지 남은 일수 계산
        // 이미 만료된 경우 음수 값 반환
        long remainingDays = ChronoUnit.DAYS.between(
                LocalDate.now(),
                warrantyEndDate
        );

        return ProductWarrantyInfo.builder()
                .purchaseDate(purchaseDate)
                .warrantyMonths(warrantyMonths)
                .warrantyEndDate(warrantyEndDate)
                .remainingDays(remainingDays)
                .build();
    }

    // 보증기간 계산에 필요한 구매일과 보증 개월 수가 모두 있는지 확인
    public boolean canCalculate(ProductEntity product) {
        return product.getPurchaseDate() != null
                && product.getWarrantyMonth() != null;
    }

    // 보증기간 계산에 필요한 정보가 없으면 예외를 발생
    private void validateWarrantyInfo(ProductEntity product) {
        if (!canCalculate(product)) {
            throw new CustomException(ErrorCode.WARRANTY_INFO_MISSING);
        }
    }
}