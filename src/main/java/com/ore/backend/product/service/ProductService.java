package com.ore.backend.product.service;

import com.ore.backend.global.exception.CustomException;
import com.ore.backend.global.exception.ErrorCode;
import com.ore.backend.product.dto.CreateProductRequest;
import com.ore.backend.product.dto.CreateProductResponse;
import com.ore.backend.product.dto.GetProductResponse;
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

import java.util.List;
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

    // 제품 등록
    @Transactional
    public CreateProductResponse createProduct(UUID userId, CreateProductRequest request) {
        UserEntity user = findUser(userId);

        ProductSourceEntity source = null;
        ProductModelEntity model = null;
        boolean officialManualExists = false;

        // OCR 입력 데이터 확인
        if (request.getSourceId() != null) {
            source = findProductSource(request.getSourceId());
            validateSourceOwner(source, user);

            Optional<ProductModelEntity> modelOptional = findModelBySource(source);

            if (modelOptional.isPresent()) {
                model = modelOptional.get();
                officialManualExists = productManualRepository.existsByModel(model);
            }
        }

        // 매뉴얼 필수 여부 검증
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

        // 사용자 입력 매뉴얼 저장
        if (request.hasManual()) {
            ProductCustomManualEntity customManual = ProductCustomManualEntity.builder()
                    .product(savedProduct)
                    .manualContent(request.getManual().getManualContent())
                    .build();

            productCustomManualRepository.save(customManual);
        }

        return CreateProductResponse.from(savedProduct);
    }

    // 사용자 조회
    private UserEntity findUser(UUID userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.UNAUTHORIZED));
    }

    // 제품 입력 데이터 조회
    private ProductSourceEntity findProductSource(UUID sourceId) {
        return productSourceRepository.findById(sourceId)
                .orElseThrow(() -> new CustomException(ErrorCode.PRODUCT_SOURCE_NOT_FOUND));
    }

    // 입력 데이터 소유자 검증
    private void validateSourceOwner(ProductSourceEntity source, UserEntity user) {
        if (!source.getUser().getUserId().equals(user.getUserId())) {
            throw new CustomException(ErrorCode.FORBIDDEN_PRODUCT_SOURCE);
        }
    }

    // 모델 번호 기반 제품 모델 조회
    private Optional<ProductModelEntity> findModelBySource(ProductSourceEntity source) {
        String modelNumber = source.getModelNumber();

        if (modelNumber == null || modelNumber.isBlank()) {
            return Optional.empty();
        }

        return productModelRepository.findByModelNumber(modelNumber);
    }

    // 매뉴얼 필수 조건 검증
    private void validateManualRequired(CreateProductRequest request, boolean officialManualExists) {
        if (request.getSourceId() == null && !request.hasManual()) {
            throw new CustomException(ErrorCode.REQUIRED_FIELD_MISSING);
        }

        if (request.getSourceId() != null && !officialManualExists && !request.hasManual()) {
            throw new CustomException(ErrorCode.REQUIRED_FIELD_MISSING);
        }
    }

    // 제품 목록 조회
    @Transactional(readOnly = true)
    public List<GetProductResponse> getProducts(UUID userId, Boolean isFavorite) {
        // 즐겨찾기 필터 적용
        List<ProductEntity> products = Boolean.TRUE.equals(isFavorite)
                ? productRepository.findByUser_UserIdAndIsFavoriteTrueOrderByCreatedAtDesc(userId)
                : productRepository.findByUser_UserIdOrderByCreatedAtDesc(userId);

        return products.stream()
                .map(GetProductResponse::from)
                .toList();
    }
}