package com.ore.backend.productManual.service;

import com.ore.backend.global.exception.CustomException;
import com.ore.backend.global.exception.ErrorCode;
import com.ore.backend.product.entity.ProductEntity;
import com.ore.backend.product.repository.ProductRepository;
import com.ore.backend.productModel.entity.ProductModelEntity;
import com.ore.backend.productManual.dto.ProductManualResponse;
import com.ore.backend.productManual.entity.ProductManualEntity;
import com.ore.backend.productManual.repository.ProductManualRepository;
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
}
