package com.ore.backend.productManual.service;

import com.ore.backend.global.ai.AiClient;
import com.ore.backend.global.exception.CustomException;
import com.ore.backend.global.exception.ErrorCode;
import com.ore.backend.product.entity.ProductEntity;
import com.ore.backend.product.repository.ProductRepository;
import com.ore.backend.productManual.dto.AiProductManualResponse;
import com.ore.backend.productModel.entity.ProductModelEntity;
import com.ore.backend.productManual.dto.ProductManualResponse;
import com.ore.backend.productManual.entity.ProductManualEntity;
import com.ore.backend.productManual.repository.ProductManualRepository;
import com.ore.backend.productModel.repository.ProductModelRepository;
import com.ore.backend.productSource.entity.ProductSourceEntity;
import com.ore.backend.productSource.repository.ProductSourceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductManualService {

    private final ProductRepository productRepository;
    private final ProductManualRepository productManualRepository;
    private final ProductSourceRepository productSourceRepository;
    private final ProductModelRepository productModelRepository;
    private final AiClient aiClient;

    // 제품에 연결된 모델의 공식 매뉴얼 조회
    public ProductManualResponse getProductManual(UUID productId) {
        ProductEntity product = productRepository.findById(productId)
                .orElseThrow(() -> new CustomException(ErrorCode.PRODUCT_NOT_FOUND));

        ProductModelEntity model = product.getModel();

        if (model == null) {
            throw new CustomException(ErrorCode.MANUAL_NOT_FOUND);
        }

        ProductManualEntity manual = productManualRepository.findByModel_ModelId(model.getModelId())
                .orElseThrow(() -> new CustomException(ErrorCode.MANUAL_NOT_FOUND));

        return ProductManualResponse.from(manual);
    }

    // sourceId 기준으로 공식 매뉴얼을 검색하고 저장
    @Transactional
    public ProductManualResponse searchProductManual(UUID userId, UUID sourceId) {
        ProductSourceEntity source = productSourceRepository
                .findBySourceIdAndUser_UserId(sourceId, userId)
                .orElseThrow(() -> new CustomException(ErrorCode.PRODUCT_SOURCE_NOT_FOUND));

        String brandName = source.getBrandName();
        String productName = source.getProductName();
        String modelNumber = source.getModelNumber();

        validateModelNumber(modelNumber);

        // 동일 모델이 이미 있으면 재사용하고, 없으면 새 모델 생성
        ProductModelEntity model = productModelRepository
                .findByModelNumber(modelNumber)
                .orElseGet(() -> productModelRepository.save(
                        ProductModelEntity.builder()
                                .brandName(brandName)
                                .modelNumber(modelNumber)
                                .build()
                ));

        // 기존 매뉴얼이 있으면 반환하고, 없으면 AI 검색 후 저장
        return productManualRepository.findByModel_ModelId(model.getModelId())
                .map(ProductManualResponse::from)
                .orElseGet(() -> saveProductManualFromAi(
                        model,
                        brandName,
                        productName,
                        modelNumber
                ));
    }

    // AI 서버에서 공식 매뉴얼을 검색한 뒤 product_manuals에 저장
    private ProductManualResponse saveProductManualFromAi(
            ProductModelEntity model,
            String brandName,
            String productName,
            String modelNumber
    ) {
        AiProductManualResponse aiResponse = aiClient.searchManual(
                brandName,
                productName,
                modelNumber
        );

        validateAiResponse(aiResponse);

        ProductManualEntity manual = ProductManualEntity.builder()
                .model(model)
                .manualUrl(aiResponse.getManualUrl())
                .manualSummary(aiResponse.getManualSummary())
                .customerCenter(aiResponse.getCustomerCenter())
                .build();

        ProductManualEntity savedManual = productManualRepository.save(manual);

        return ProductManualResponse.from(savedManual);
    }

    // 공식 매뉴얼 검색에 필요한 모델명 검증
    private void validateModelNumber(String modelNumber) {
        if (modelNumber == null || modelNumber.isBlank()) {
            throw new CustomException(ErrorCode.MODEL_NUMBER_REQUIRED);
        }
    }

    // AI 서버 응답에 유효한 매뉴얼 URL이 있는지 검증
    private void validateAiResponse(AiProductManualResponse aiResponse) {
        if (aiResponse == null
                || aiResponse.getManualUrl() == null
                || aiResponse.getManualUrl().isBlank()) {
            throw new CustomException(ErrorCode.MANUAL_NOT_FOUND);
        }
    }
}
