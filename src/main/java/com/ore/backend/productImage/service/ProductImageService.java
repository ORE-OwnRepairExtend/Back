package com.ore.backend.productImage.service;

import com.ore.backend.global.exception.CustomException;
import com.ore.backend.global.exception.ErrorCode;
import com.ore.backend.global.service.S3ImageStorageService;
import com.ore.backend.product.entity.ProductEntity;
import com.ore.backend.product.repository.ProductRepository;
import com.ore.backend.productImage.dto.ProductImageResponse;
import com.ore.backend.productImage.entity.ProductImageEntity;
import com.ore.backend.productImage.repository.ProductImageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductImageService {

    private final ProductRepository productRepository;
    private final ProductImageRepository productImageRepository;
    private final S3ImageStorageService s3ImageStorageService;

    @Transactional
    public ProductImageResponse uploadProductImage(UUID productId, MultipartFile image) {
        ProductEntity product = productRepository.findById(productId)
                .orElseThrow(() -> new CustomException(ErrorCode.PRODUCT_NOT_FOUND));

        if (productImageRepository.existsByProduct_ProductId(productId)) {
            throw new CustomException(ErrorCode.PRODUCT_IMAGE_ALREADY_EXISTS);
        }

        String imageKey = s3ImageStorageService.uploadProductImage(productId, image);

        ProductImageEntity productImage = ProductImageEntity.builder()
                .product(product)
                .imageUrl(imageKey)
                .build();

        ProductImageEntity savedProductImage = productImageRepository.save(productImage);

        String presignedUrl = s3ImageStorageService.generatePresignedUrl(
                savedProductImage.getImageUrl()
        );

        return ProductImageResponse.of(savedProductImage, presignedUrl);
    }

}