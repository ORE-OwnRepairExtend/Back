package com.ore.backend.global.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {

    // 400
    ID_TOKEN_MISSING(HttpStatus.BAD_REQUEST, "ID 토큰이 필요합니다."),
    REFRESH_TOKEN_MISSING(HttpStatus.BAD_REQUEST, "Refresh 토큰이 필요합니다."),
    NO_UPDATE_VALUE(HttpStatus.BAD_REQUEST, "수정할 값이 없습니다."),
    IMAGE_REQUIRED(HttpStatus.BAD_REQUEST, "이미지 파일이 필요합니다."),
    INVALID_IMAGE_FILE(HttpStatus.BAD_REQUEST, "이미지 파일만 업로드할 수 있습니다."),
    INVALID_SOURCE_TYPE(HttpStatus.BAD_REQUEST, "sourceType이 올바르지 않습니다."),
    REQUIRED_FIELD_MISSING(HttpStatus.BAD_REQUEST, "필수 입력값이 누락되었습니다."),
    INVALID_PRODUCT_CATEGORY(HttpStatus.BAD_REQUEST, "유효하지 않은 제품 카테고리입니다."),
    WARRANTY_INFO_MISSING(HttpStatus.BAD_REQUEST, "보증기간 계산에 필요한 정보가 없습니다."),

    // 401
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "인증 정보가 유효하지 않습니다."),
    INVALID_GOOGLE_TOKEN(HttpStatus.UNAUTHORIZED, "Google 토큰이 유효하지 않습니다."),
    TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, "토큰이 만료되었습니다."),
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "유효하지 않은 토큰입니다."),

    // 403
    FORBIDDEN_PRODUCT_SOURCE(HttpStatus.FORBIDDEN, "해당 입력 데이터에 접근할 권한이 없습니다."),

    // 404
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다."),
    PRODUCT_SOURCE_NOT_FOUND(HttpStatus.NOT_FOUND, "제품 입력 데이터를 찾을 수 없습니다."),
    PRODUCT_NOT_FOUND(HttpStatus.NOT_FOUND, "제품을 찾을 수 없습니다."),
    // 404
    IMAGE_NOT_FOUND(HttpStatus.NOT_FOUND, "등록된 제품 이미지가 없습니다."),

    // 409
    PRODUCT_IMAGE_ALREADY_EXISTS(HttpStatus.CONFLICT, "이미 등록된 제품 이미지가 있습니다."),

    // 500
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버 오류가 발생했습니다."),
    IMAGE_UPLOAD_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "이미지 업로드 중 오류가 발생했습니다."),
    IMAGE_URL_GENERATION_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "이미지 접근 URL 생성에 실패했습니다.");

    private final HttpStatus status;
    private final String message;

    ErrorCode(HttpStatus status, String message) {
        this.status = status;
        this.message = message;
    }
}