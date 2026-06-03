package com.ore.backend.repairHistory.service;

import com.ore.backend.global.exception.CustomException;
import com.ore.backend.global.exception.ErrorCode;
import com.ore.backend.global.service.S3ImageStorageService;
import com.ore.backend.product.entity.ProductEntity;
import com.ore.backend.product.repository.ProductRepository;
import com.ore.backend.repairHistory.dto.CreateRepairHistoryResponse;
import com.ore.backend.repairHistory.entity.RepairHistoryEntity;
import com.ore.backend.repairHistory.repository.RepairHistoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.UUID;
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RepairHistoryService {

    private final ProductRepository productRepository;
    private final RepairHistoryRepository repairHistoryRepository;
    private final S3ImageStorageService s3ImageStorageService;

    // 수리 이력 등록
    @Transactional
    public CreateRepairHistoryResponse createRepairHistory(
            UUID userId,
            UUID productId,
            LocalDate date,
            String title,
            String content,
            String serviceCenter,
            Integer cost,
            MultipartFile image
    ) {
        log.info("[수리이력 등록 시작] userId={}, productId={}", userId, productId);

        // 필수값 검증
        validateRequiredFields(date, title, content, cost);
        log.info("[수리이력 필수값 검증 완료]");
        // 사용자 제품 조회
        ProductEntity product = productRepository.findByProductIdAndUser_UserId(productId, userId)
                .orElseThrow(() -> new CustomException(ErrorCode.PRODUCT_NOT_FOUND));
        log.info("[수리이력 제품 조회 완료] productId={}", product.getProductId());

        // 수리 이미지 업로드
        String imageKey = uploadImage(productId, image);
        log.info("[수리이력 이미지 업로드 결과] imageKey={}", imageKey);

        // 수리 이력 저장
        RepairHistoryEntity repairHistory = RepairHistoryEntity.builder()
                .product(product)
                .date(date)
                .title(title)
                .content(content)
                .serviceCenter(serviceCenter)
                .cost(cost)
                .imageUrl(imageKey)
                .build();

        RepairHistoryEntity savedRepairHistory = repairHistoryRepository.save(repairHistory);
        log.info("[수리이력 저장 완료] repairId={}", savedRepairHistory.getRepairId());

        String responseImageUrl = imageKey == null
                ? null
                : s3ImageStorageService.generatePresignedUrl(imageKey);
        log.info("[수리이력 응답 URL 생성 완료] hasImageUrl={}", responseImageUrl != null);

        return CreateRepairHistoryResponse.from(savedRepairHistory, responseImageUrl);
    }

    // 필수 입력값 검증
    private void validateRequiredFields(
            LocalDate date,
            String title,
            String content,
            Integer cost
    ) {
        if (date == null
                || title == null || title.isBlank()
                || content == null || content.isBlank()
                || cost == null) {
            throw new CustomException(ErrorCode.REQUIRED_FIELD_MISSING);
        }

        if (cost < 0) {
            throw new CustomException(ErrorCode.REQUIRED_FIELD_MISSING);
        }
    }

    // 이미지 업로드
    private String uploadImage(UUID productId, MultipartFile image) {
        log.info("[수리이력 이미지 업로드 진입] imageNull={}, imageEmpty={}",
                image == null,
                image != null && image.isEmpty()
        );
        if (image == null || image.isEmpty()) {
            return null;
        }

        return s3ImageStorageService.uploadRepairHistoryImage(productId, image);
    }
}