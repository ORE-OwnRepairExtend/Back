package com.ore.backend.repairHistory.service;

import com.ore.backend.global.exception.CustomException;
import com.ore.backend.global.exception.ErrorCode;
import com.ore.backend.global.service.S3ImageStorageService;
import com.ore.backend.product.entity.ProductEntity;
import com.ore.backend.product.repository.ProductRepository;
import com.ore.backend.repairHistory.dto.CreateRepairHistoryResponse;
import com.ore.backend.repairHistory.dto.GetRepairHistoryDetailResponse;
import com.ore.backend.repairHistory.dto.GetRepairHistoryResponse;
import com.ore.backend.repairHistory.entity.RepairHistoryEntity;
import com.ore.backend.repairHistory.repository.RepairHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

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
        // 필수값 검증
        validateRequiredFields(date, title, content, cost);

        // 사용자 제품 조회
        ProductEntity product = productRepository.findByProductIdAndUser_UserId(productId, userId)
                .orElseThrow(() -> new CustomException(ErrorCode.PRODUCT_NOT_FOUND));

        // 수리 이미지 업로드
        String imageKey = uploadImage(productId, image);

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

        String responseImageUrl = imageKey == null
                ? null
                : s3ImageStorageService.generatePresignedUrl(imageKey);

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
        if (image == null || image.isEmpty()) {
            return null;
        }

        return s3ImageStorageService.uploadRepairHistoryImage(productId, image);
    }

    // 특정 제품의 수리 이력 목록 조회
    public List<GetRepairHistoryResponse> getRepairHistories(UUID userId, UUID productId) {
        productRepository.findByProductIdAndUser_UserId(productId, userId)
                .orElseThrow(() -> new CustomException(ErrorCode.PRODUCT_NOT_FOUND));

        return repairHistoryRepository.findByProduct_ProductIdOrderByDateDescCreatedAtDesc(productId)
                .stream()
                .map(GetRepairHistoryResponse::from)
                .toList();
    }

    // 수리 이력 상세 조회
    public GetRepairHistoryDetailResponse getRepairHistoryDetail(
            UUID userId,
            UUID repairId
    ) {
        RepairHistoryEntity repairHistory = repairHistoryRepository
                .findByRepairIdAndProduct_User_UserId(repairId, userId)
                .orElseThrow(() -> new CustomException(ErrorCode.REPAIR_HISTORY_NOT_FOUND));

        String imageUrl = repairHistory.getImageUrl() == null
                ? null
                : s3ImageStorageService.generatePresignedUrl(repairHistory.getImageUrl());

        return GetRepairHistoryDetailResponse.of(repairHistory, imageUrl);
    }
}