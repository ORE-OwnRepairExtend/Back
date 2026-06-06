package com.ore.backend.productImage.service;

import com.ore.backend.global.exception.CustomException;
import com.ore.backend.global.exception.ErrorCode;
import com.ore.backend.global.s3.S3ImageStorageService;
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

    // 제품 대표 이미지 최초 등록
    @Transactional
    public ProductImageResponse uploadProductImage(UUID productId, MultipartFile image) {
        ProductEntity product = productRepository.findById(productId)
                .orElseThrow(() -> new CustomException(ErrorCode.PRODUCT_NOT_FOUND));

        // 제품당 대표 이미지는 1개만 등록 가능
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

    // 제품 대표 이미지 수정
    @Transactional
    public ProductImageResponse updateProductImage(UUID productId, MultipartFile image) {
        productRepository.findById(productId)
                .orElseThrow(() -> new CustomException(ErrorCode.PRODUCT_NOT_FOUND));

        ProductImageEntity productImage = productImageRepository.findByProduct_ProductId(productId)
                .orElseThrow(() -> new CustomException(ErrorCode.IMAGE_NOT_FOUND));

        String oldImageKey = productImage.getImageUrl();

        String newImageKey = s3ImageStorageService.uploadProductImage(productId, image);

        productImage.updateImageUrl(newImageKey);

        // 제품 대표 이미지 수정
        s3ImageStorageService.deleteImage(oldImageKey);

        String presignedUrl = s3ImageStorageService.generatePresignedUrl(newImageKey);

        return ProductImageResponse.of(productImage, presignedUrl);
    }

    // 제품 대표 이미지 삭제
    @Transactional
    public void deleteProductImage(UUID productId) {
        productRepository.findById(productId)
                .orElseThrow(() -> new CustomException(ErrorCode.PRODUCT_NOT_FOUND));

        ProductImageEntity productImage = productImageRepository.findByProduct_ProductId(productId)
                .orElseThrow(() -> new CustomException(ErrorCode.IMAGE_NOT_FOUND));

        String imageKey = productImage.getImageUrl();

        // S3 이미지 삭제 후 DB 데이터 삭제
        s3ImageStorageService.deleteImage(imageKey);
        productImageRepository.delete(productImage);
    }
}