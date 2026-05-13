package com.ore.backend.productSource.service;

import com.ore.backend.global.exception.CustomException;
import com.ore.backend.global.exception.ErrorCode;
import com.ore.backend.productSource.dto.CreateProductSourceResponse;
import com.ore.backend.productSource.entity.ProductSourceEntity;
import com.ore.backend.productSource.entity.SourceType;
import com.ore.backend.productSource.repository.ProductSourceRepository;
import com.ore.backend.user.entity.UserEntity;
import com.ore.backend.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProductSourceService {

    private final ProductSourceRepository productSourceRepository;
    private final UserRepository userRepository;
    private final S3ImageStorageService s3ImageStorageService;

    @Transactional
    public CreateProductSourceResponse createProductSource(
            UUID userId,
            MultipartFile image,
            String sourceTypeValue
    ) {
        validateImage(image);

        SourceType sourceType = SourceType.from(sourceTypeValue);

        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        String imageUrl = s3ImageStorageService.upload(image);

        ProductSourceEntity productSource = ProductSourceEntity.builder()
                .user(user)
                .imageUrl(imageUrl)
                .sourceType(sourceType)
                .build();

        ProductSourceEntity savedSource = productSourceRepository.save(productSource);

        return CreateProductSourceResponse.builder()
                .sourceId(savedSource.getSourceId())
                .imageUrl(savedSource.getImageUrl())
                .sourceType(savedSource.getSourceType())
                .createdAt(savedSource.getCreatedAt())
                .build();
    }

    private void validateImage(MultipartFile image) {

        if (image == null || image.isEmpty()) {
            throw new CustomException(ErrorCode.IMAGE_REQUIRED);
        }
    }
}