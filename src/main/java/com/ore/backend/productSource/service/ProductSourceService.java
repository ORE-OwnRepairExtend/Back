package com.ore.backend.productSource.service;

import com.ore.backend.global.exception.CustomException;
import com.ore.backend.global.exception.ErrorCode;
import com.ore.backend.global.service.S3ImageStorageService;
import com.ore.backend.productSource.dto.AiOcrResponse;
import com.ore.backend.productSource.dto.CreateProductSourceResponse;
import com.ore.backend.productSource.dto.UpdateProductSourceRequest;
import com.ore.backend.productSource.entity.ProductSourceEntity;
import com.ore.backend.productSource.entity.SourceType;
import com.ore.backend.productSource.repository.ProductSourceRepository;
import com.ore.backend.user.entity.UserEntity;
import com.ore.backend.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProductSourceService {

    private final ProductSourceRepository productSourceRepository;
    private final UserRepository userRepository;
    private final S3ImageStorageService s3ImageStorageService;
    private final AiOcrClient aiOcrClient;

    // 제품 입력 데이터 생성 + AI OCR 분석
    @Transactional
    public CreateProductSourceResponse createProductSource(
            UUID userId,
            MultipartFile image,
            String sourceTypeValue
    ) {
        validateImage(image);

        // 입력 데이터 유형 변환
        SourceType sourceType = SourceType.from(sourceTypeValue);

        // 사용자 조회
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        // S3 업로드 후 object key 저장
        String imageKey = s3ImageStorageService.uploadProductSourceImage(image);

        ProductSourceEntity productSource = ProductSourceEntity.builder()
                .user(user)
                .imageUrl(imageKey)
                .sourceType(sourceType)
                .build();

        ProductSourceEntity savedSource = productSourceRepository.save(productSource);

        // AI 서버가 접근할 수 있는 이미지 조회 URL 생성
        String presignedImageUrl =
                s3ImageStorageService.generatePresignedUrl(savedSource.getImageUrl());

        // AI OCR 분석 요청
        AiOcrResponse aiResponse = aiOcrClient.analyzeImage(
                presignedImageUrl,
                sourceType.name().toLowerCase()
        );

        // AI 분석 결과 저장
        savedSource.updateAiAnalysisResult(
                aiResponse.getOcrText(),
                aiResponse.getModelNumber(),
                aiResponse.getBrandName(),
                aiResponse.getProductName()
        );

        return CreateProductSourceResponse.builder()
                .sourceId(savedSource.getSourceId())
                .imageUrl(presignedImageUrl)
                .ocrText(savedSource.getOcrText())
                .modelNumber(savedSource.getModelNumber())
                .brandName(savedSource.getBrandName())
                .productName(savedSource.getProductName())
                .sourceType(savedSource.getSourceType())
                .createdAt(savedSource.getCreatedAt())
                .build();
    }

    // 이미지 필수 여부 검증
    private void validateImage(MultipartFile image) {

        if (image == null || image.isEmpty()) {
            throw new CustomException(ErrorCode.IMAGE_REQUIRED);
        }
    }

    // OCR 분석 제품 정보 수정
    @Transactional
    public CreateProductSourceResponse updateProductSource(
            UUID userId,
            UUID sourceId,
            UpdateProductSourceRequest request
    ) {
        ProductSourceEntity productSource = productSourceRepository
                .findBySourceIdAndUser_UserId(sourceId, userId)
                .orElseThrow(() -> new CustomException(ErrorCode.PRODUCT_SOURCE_NOT_FOUND));

        productSource.updateExtractedProductInfo(
                request.getBrandName(),
                request.getProductName(),
                request.getModelNumber()
        );

        String presignedImageUrl =
                s3ImageStorageService.generatePresignedUrl(productSource.getImageUrl());

        return CreateProductSourceResponse.builder()
                .sourceId(productSource.getSourceId())
                .imageUrl(presignedImageUrl)
                .ocrText(productSource.getOcrText())
                .modelNumber(productSource.getModelNumber())
                .brandName(productSource.getBrandName())
                .productName(productSource.getProductName())
                .sourceType(productSource.getSourceType())
                .createdAt(productSource.getCreatedAt())
                .build();
    }
}