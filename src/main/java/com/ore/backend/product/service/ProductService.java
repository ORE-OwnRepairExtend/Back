package com.ore.backend.product.service;

import com.ore.backend.global.exception.CustomException;
import com.ore.backend.global.exception.ErrorCode;
import com.ore.backend.product.dto.CreateProductRequest;
import com.ore.backend.product.dto.CreateProductResponse;
import com.ore.backend.productCustomManual.entity.ProductCustomManualEntity;
import com.ore.backend.product.entity.ProductEntity;
import com.ore.backend.productModel.entity.ProductModelEntity;
import com.ore.backend.productCustomManual.repository.ProductCustomManualRepository;
import com.ore.backend.productManual.repository.ProductManualRepository;
import com.ore.backend.productModel.repository.ProductModelRepository;
import com.ore.backend.product.repository.ProductRepository;
import com.ore.backend.productSource.entity.ProductSourceEntity;
import com.ore.backend.productSource.repository.ProductSourceRepository;
import com.ore.backend.user.entity.UserEntity;
import com.ore.backend.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductService {

    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final ProductSourceRepository productSourceRepository;
    private final ProductModelRepository productModelRepository;
    private final ProductManualRepository productManualRepository;
    private final ProductCustomManualRepository productCustomManualRepository;

    @Transactional
    public CreateProductResponse createProduct(UUID userId, CreateProductRequest request) {
        UserEntity user = findUser(userId);

        ProductSourceEntity source = null;
        ProductModelEntity model = null;
        boolean officialManualExists = false;

        if (request.getSourceId() != null) {
            source = findProductSource(request.getSourceId());
            validateSourceOwner(source, user);

            Optional<ProductModelEntity> modelOptional = findModelBySource(source);

            if (modelOptional.isPresent()) {
                model = modelOptional.get();
                officialManualExists = productManualRepository.existsByModel(model);
            }
        }

        validateManualRequired(request, officialManualExists);

        ProductEntity product = ProductEntity.builder()
                .user(user)
                .source(source)
                .model(model)
                .name(request.getName())
                .category(request.getCategory())
                .nickname(request.getNickname())
                .purchaseDate(request.getPurchaseDate())
                .warrantyMonth(request.getWarrantyMonths())
                .build();

        ProductEntity savedProduct = productRepository.save(product);

        if (request.hasManual()) {
            ProductCustomManualEntity customManual = ProductCustomManualEntity.builder()
                    .product(savedProduct)
                    .manualContent(request.getManual().getManualContent())
                    .build();

            productCustomManualRepository.save(customManual);
        }

        return CreateProductResponse.from(savedProduct);
    }

    private UserEntity findUser(UUID userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.UNAUTHORIZED));
    }

    private ProductSourceEntity findProductSource(UUID sourceId) {
        return productSourceRepository.findById(sourceId)
                .orElseThrow(() -> new CustomException(ErrorCode.PRODUCT_SOURCE_NOT_FOUND));
    }

    private void validateSourceOwner(ProductSourceEntity source, UserEntity user) {
        if (!source.getUser().getUserId().equals(user.getUserId())) {
            throw new CustomException(ErrorCode.FORBIDDEN_PRODUCT_SOURCE);
        }
    }

    private Optional<ProductModelEntity> findModelBySource(ProductSourceEntity source) {
        String modelNumber = source.getModelNumber();

        if (modelNumber == null || modelNumber.isBlank()) {
            return Optional.empty();
        }

        return productModelRepository.findByModelNumber(modelNumber);
    }

    private void validateManualRequired(CreateProductRequest request, boolean officialManualExists) {
        if (request.getSourceId() == null && !request.hasManual()) {
            throw new CustomException(ErrorCode.REQUIRED_FIELD_MISSING);
        }

        if (request.getSourceId() != null && !officialManualExists && !request.hasManual()) {
            throw new CustomException(ErrorCode.REQUIRED_FIELD_MISSING);
        }
    }
}