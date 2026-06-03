package com.ore.backend.product.service;

import com.ore.backend.global.exception.CustomException;
import com.ore.backend.global.exception.ErrorCode;
import com.ore.backend.product.dto.*;
import com.ore.backend.product.entity.ProductCategory;
import com.ore.backend.productUserManual.entity.ProductUserManualEntity;
import com.ore.backend.product.entity.ProductEntity;
import com.ore.backend.productImage.entity.ProductImageEntity;
import com.ore.backend.productImage.repository.ProductImageRepository;
import com.ore.backend.productModel.entity.ProductModelEntity;
import com.ore.backend.productUserManual.repository.ProductUserManualRepository;
import com.ore.backend.productManual.repository.ProductManualRepository;
import com.ore.backend.productModel.repository.ProductModelRepository;
import com.ore.backend.product.repository.ProductRepository;
import com.ore.backend.productSource.entity.ProductSourceEntity;
import com.ore.backend.productSource.repository.ProductSourceRepository;
import com.ore.backend.global.service.S3ImageStorageService;
import com.ore.backend.repairHistory.repository.RepairHistoryRepository;
import com.ore.backend.user.entity.UserEntity;
import com.ore.backend.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductService {

    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final ProductSourceRepository productSourceRepository;
    private final ProductModelRepository productModelRepository;
    private final ProductManualRepository productManualRepository;
    private final ProductUserManualRepository productUserManualRepository;
    private final S3ImageStorageService s3ImageStorageService;
    private final ProductImageRepository productImageRepository;
    private final ProductWarrantyCalculator productWarrantyCalculator;
    private final RepairHistoryRepository repairHistoryRepository;

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

        // 사용자가 확인/수정 후 최종 확정한 제품별 매뉴얼 저장
        if (request.hasManual()) {
            ProductUserManualEntity userManual = ProductUserManualEntity.builder()
                    .product(savedProduct)
                    .manualContent(request.getManual().getManualContent())
                    .build();

            productUserManualRepository.save(userManual);
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

        // 제품 ID 목록 추출
        List<UUID> productIds = products.stream()
                .map(ProductEntity::getProductId)
                .toList();

        // 수리 이력이 존재하는 제품 ID 목록 조회
        Set<UUID> productIdsHavingRepairHistory = productIds.isEmpty()
                ? Set.of()
                : new HashSet<>(repairHistoryRepository.findProductIdsHavingRepairHistory(productIds));

        return products.stream()
                .map(product -> {
                    Long remainingDays = null;

                    if (productWarrantyCalculator.canCalculate(product)) {
                        remainingDays = productWarrantyCalculator.calculate(product).getRemainingDays();
                    }

                    boolean hasRepairHistory = productIdsHavingRepairHistory
                            .contains(product.getProductId());

                    return GetProductResponse.from(
                            product,
                            getProductImageUrl(product.getProductId()),
                            hasRepairHistory,
                            remainingDays
                    );
                })
                .toList();
    }

    // 제품 상세 조회
    @Transactional(readOnly = true)
    public GetProductDetailResponse getProductDetail(UUID userId, UUID productId) {
        ProductEntity product = productRepository.findByProductIdAndUser_UserId(productId, userId)
                .orElseThrow(() -> new CustomException(ErrorCode.PRODUCT_NOT_FOUND));

        String imageUrl = getProductImageUrl(product.getProductId());

        ProductUserManualInfo manual = getProductUserManualInfo(product.getProductId());

        boolean hasRepairHistory = repairHistoryRepository
                .existsByProduct_ProductId(product.getProductId());

        return GetProductDetailResponse.from(product, imageUrl, hasRepairHistory, manual);
    }

    // 사용자가 최종 확정한 제품별 매뉴얼 조회
    private ProductUserManualInfo getProductUserManualInfo(UUID productId) {
        return productUserManualRepository.findByProduct_ProductId(productId)
                .map(ProductUserManualInfo::from)
                .orElse(null);
    }

    // 제품 이미지 URL 조회
    private String getProductImageUrl(UUID productId) {
        return productImageRepository.findByProduct_ProductId(productId)
                .map(ProductImageEntity::getImageUrl)
                .map(s3ImageStorageService::generatePresignedUrl)
                .orElse(null);
    }

    // 제품 정보 수정
    @Transactional
    public UpdateProductResponse updateProduct(
            UUID userId,
            UUID productId,
            String nickname,
            MultipartFile image,
            String categoryValue
    ) {
        ProductEntity product = productRepository.findByProductIdAndUser_UserId(productId, userId)
                .orElseThrow(() -> new CustomException(ErrorCode.PRODUCT_NOT_FOUND));

        ProductCategory category = parseCategory(categoryValue);

        validateUpdateValue(nickname, image, category);

        product.updateProductInfo(nickname, category);

        if (image != null && !image.isEmpty()) {
            updateProductImage(product, image);
        }

        String imageUrl = getProductImageUrl(product.getProductId());

        return UpdateProductResponse.from(product, imageUrl);
    }

    // 수정값 존재 여부 검증
    private void validateUpdateValue(
            String nickname,
            MultipartFile image,
            ProductCategory category
    ) {
        if ((nickname == null || nickname.isBlank())
                && (image == null || image.isEmpty())
                && category == null) {
            throw new CustomException(ErrorCode.NO_UPDATE_VALUE);
        }
    }

    // 카테고리 변환
    private ProductCategory parseCategory(String categoryValue) {
        if (categoryValue == null || categoryValue.isBlank()) {
            return null;
        }

        try {
            return ProductCategory.valueOf(categoryValue);
        } catch (IllegalArgumentException e) {
            throw new CustomException(ErrorCode.INVALID_PRODUCT_CATEGORY);
        }
    }

    // 제품 이미지 저장 또는 교체
    private void updateProductImage(ProductEntity product, MultipartFile image) {
        String imageKey = s3ImageStorageService.uploadProductImage(product.getProductId(), image);

        ProductImageEntity productImage = productImageRepository.findByProduct_ProductId(product.getProductId())
                .map(existingImage -> {
                    existingImage.updateImageUrl(imageKey);
                    return existingImage;
                })
                .orElseGet(() -> productImageRepository.save(
                        ProductImageEntity.builder()
                                .product(product)
                                .imageUrl(imageKey)
                                .build()
                ));
    }

    // 즐겨찾기 상태 변경
    @Transactional
    public UpdateProductFavoriteResponse updateFavorite(
            UUID userId,
            UUID productId,
            Boolean isFavorite
    ) {
        ProductEntity product = productRepository.findByProductIdAndUser_UserId(productId, userId)
                .orElseThrow(() -> new CustomException(ErrorCode.PRODUCT_NOT_FOUND));

        product.updateFavorite(isFavorite);

        return UpdateProductFavoriteResponse.from(product);
    }

    // 제품 보증기간 조회
    @Transactional(readOnly = true)
    public GetProductWarrantyResponse getProductWarranty(UUID userId, UUID productId) {
        ProductEntity product = productRepository.findByProductIdAndUser_UserId(productId, userId)
                .orElseThrow(() -> new CustomException(ErrorCode.PRODUCT_NOT_FOUND));

        ProductWarrantyInfo warrantyInfo = productWarrantyCalculator.calculate(product);

        return GetProductWarrantyResponse.builder()
                .productId(product.getProductId())
                .purchaseDate(warrantyInfo.getPurchaseDate())
                .warrantyMonths(warrantyInfo.getWarrantyMonths())
                .warrantyEndDate(warrantyInfo.getWarrantyEndDate())
                .remainingDays(warrantyInfo.getRemainingDays())
                .build();
    }

    // 제품 삭제
    @Transactional
    public void deleteProduct(UUID userId, UUID productId) {
        ProductEntity product = productRepository.findByProductIdAndUser_UserId(productId, userId)
                .orElseThrow(() -> new CustomException(ErrorCode.PRODUCT_NOT_FOUND));

        // 제품 이미지 삭제
        productImageRepository.deleteByProduct_ProductId(productId);

        // 사용자 입력 매뉴얼 삭제
        productUserManualRepository.deleteByProduct_ProductId(productId);

        // 제품 삭제
        productRepository.delete(product);
    }
}