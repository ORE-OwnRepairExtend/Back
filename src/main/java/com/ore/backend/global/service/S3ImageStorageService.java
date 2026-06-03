package com.ore.backend.global.service;

import com.ore.backend.global.exception.CustomException;
import com.ore.backend.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.exception.SdkClientException;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;

import java.io.IOException;
import java.time.Duration;
import java.util.UUID;
@Slf4j
@Service
@RequiredArgsConstructor
public class S3ImageStorageService {

    private final S3Client s3Client;
    private final S3Presigner s3Presigner;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    // OCR/AI 입력 이미지 업로드
    public String uploadProductSourceImage(MultipartFile image) {
        return upload(image, "product-sources");
    }

    // 사용자 프로필 이미지 업로드
    public String uploadProfileImage(UUID userId, MultipartFile image) {
        return upload(image, "profile-images/" + userId);
    }

    // 수리 이력 이미지 업로드
    public String uploadRepairHistoryImage(UUID productId, MultipartFile image) {
        return upload(image, "repair-histories/" + productId);
    }

    // 제품 대표 이미지 업로드
    public String uploadProductImage(UUID productId, MultipartFile image) {
        return upload(image, "product-images/" + productId);
    }

    private String upload(MultipartFile image, String dirName) {
        log.info("[S3 업로드 시작] dirName={}", dirName);
        if (image == null || image.isEmpty()) {
            log.warn("[S3 업로드 실패] 이미지 없음");
            throw new CustomException(ErrorCode.IMAGE_REQUIRED);
        }

        String contentType = image.getContentType();
        log.info("[S3 업로드 파일 정보] originalFilename={}, contentType={}, size={}",
                image.getOriginalFilename(),
                contentType,
                image.getSize()
        );
        if (contentType == null || !contentType.startsWith("image/")) {
            log.warn("[S3 업로드 실패] 이미지 파일 아님 contentType={}", contentType);
            throw new CustomException(ErrorCode.INVALID_IMAGE_FILE);
        }

        try {
            String originalFilename = image.getOriginalFilename();
            String extension = getExtension(originalFilename);
            String key = dirName + "/" + UUID.randomUUID() + extension;
            log.info("[S3 업로드 key 생성] key={}", key);
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucket)
                    .key(key)
                    .contentType(contentType)
                    .build();

            s3Client.putObject(
                    putObjectRequest,
                    RequestBody.fromBytes(image.getBytes())
            );
            log.info("[S3 업로드 성공] key={}", key);
            return key;

        } catch (IOException | S3Exception | SdkClientException e) {
            log.error("[S3 업로드 실패] dirName={}, message={}", dirName, e.getMessage(), e);
            throw new CustomException(ErrorCode.IMAGE_UPLOAD_FAILED);
        }
    }

    public String generatePresignedUrl(String key) {
        if (key == null || key.isBlank()) {
            return null;
        }

        try {
            // 기존에 URL로 저장된 데이터 대응
            String objectKey = extractKeyIfUrl(key);

            GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(bucket)
                .key(objectKey)
                .build();

            GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                .signatureDuration(Duration.ofMinutes(10))
                .getObjectRequest(getObjectRequest)
                .build();

            return s3Presigner.presignGetObject(presignRequest)
                .url()
                .toString();

        } catch (S3Exception | SdkClientException e) {
            throw new CustomException(ErrorCode.IMAGE_URL_GENERATION_FAILED);
    }
}

    private String extractKeyIfUrl(String value) {
        if (!value.startsWith("http")) {
            return value;
        }

        int amazonawsIndex = value.indexOf(".amazonaws.com/");
        if (amazonawsIndex == -1) {
            return value;
        }

        return value.substring(amazonawsIndex + ".amazonaws.com/".length());
    }

    private String getExtension(String originalFilename) {
        if (originalFilename == null || !originalFilename.contains(".")) {
            return "";
        }

        return originalFilename.substring(originalFilename.lastIndexOf("."));
    }

    public void deleteImage(String key) {
        if (key == null || key.isBlank()) {
            return;
        }

        String objectKey = extractKeyIfUrl(key);

        DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                .bucket(bucket)
                .key(objectKey)
                .build();

        s3Client.deleteObject(deleteObjectRequest);
    }
}