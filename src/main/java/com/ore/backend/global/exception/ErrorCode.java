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
    INVALID_REMIND_DATE(HttpStatus.BAD_REQUEST, "알림 날짜는 오늘 이후로 입력해야 합니다."),
    INVALID_REPAIR_DATE(HttpStatus.BAD_REQUEST, "수리일은 오늘 또는 이전 날짜만 입력할 수 있습니다."),
    IMAGE_ANALYSIS_FAILED(HttpStatus.BAD_REQUEST, "이미지 분석에 실패했습니다."),
    MANUAL_SEARCH_FAILED(HttpStatus.BAD_REQUEST, "매뉴얼 검색에 실패했습니다."),
    MODEL_NUMBER_REQUIRED(HttpStatus.BAD_REQUEST, "모델명이 필요합니다."),
    CHAT_MESSAGE_EMPTY(HttpStatus.BAD_REQUEST, "질문을 이해할 수 없습니다."),
    FAMILY_INVITATION_FIELDS_REQUIRED(HttpStatus.BAD_REQUEST, "초대 대상 이메일과 호칭은 필수입니다."),
    CANNOT_INVITE_SELF(HttpStatus.BAD_REQUEST, "본인에게는 가족 초대를 보낼 수 없습니다."),
    FAMILY_INVITATION_ALREADY_PROCESSED(HttpStatus.BAD_REQUEST, "이미 처리된 가족 초대입니다."),
    INVALID_FAMILY_INVITATION_STATUS(HttpStatus.BAD_REQUEST, "가족 초대 상태가 올바르지 않습니다."),

    // 401
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "인증 정보가 유효하지 않습니다."),
    INVALID_GOOGLE_TOKEN(HttpStatus.UNAUTHORIZED, "Google 토큰이 유효하지 않습니다."),
    TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, "토큰이 만료되었습니다."),
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "유효하지 않은 토큰입니다."),

    // 403
    FORBIDDEN_PRODUCT_SOURCE(HttpStatus.FORBIDDEN, "해당 입력 데이터에 접근할 권한이 없습니다."),
    FORBIDDEN_FAMILY_INVITATION(HttpStatus.FORBIDDEN, "해당 가족 초대를 처리할 권한이 없습니다."),

    // 404
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다."),
    PRODUCT_SOURCE_NOT_FOUND(HttpStatus.NOT_FOUND, "제품 입력 데이터를 찾을 수 없습니다."),
    PRODUCT_NOT_FOUND(HttpStatus.NOT_FOUND, "제품을 찾을 수 없습니다."),
    REPAIR_HISTORY_NOT_FOUND(HttpStatus.NOT_FOUND, "수리 이력을 찾을 수 없습니다."),
    IMAGE_NOT_FOUND(HttpStatus.NOT_FOUND, "등록된 제품 이미지가 없습니다."),
    MANUAL_NOT_FOUND(HttpStatus.NOT_FOUND, "매뉴얼을 찾을 수 없습니다."),
    NOTIFICATION_NOT_FOUND(HttpStatus.NOT_FOUND, "알림을 찾을 수 없습니다."),
    REPAIR_REMINDER_NOT_FOUND(HttpStatus.NOT_FOUND, "예정 정보를 찾을 수 없습니다."),
    CHAT_SESSION_NOT_FOUND(HttpStatus.NOT_FOUND, "채팅 세션을 찾을 수 없습니다."),
    INVITEE_NOT_FOUND(HttpStatus.NOT_FOUND, "초대할 사용자를 찾을 수 없습니다."),
    FAMILY_INVITATION_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 가족 초대를 찾을 수 없습니다."),
    FAMILY_MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "가족 구성원을 찾을 수 없습니다."),

    // 409
    PRODUCT_IMAGE_ALREADY_EXISTS(HttpStatus.CONFLICT, "이미 등록된 제품 이미지가 있습니다."),
    ALREADY_FAMILY_MEMBER(HttpStatus.CONFLICT, "이미 가족으로 등록된 사용자입니다."),
    PENDING_FAMILY_INVITATION_EXISTS(HttpStatus.CONFLICT, "이미 처리 대기 중인 가족 초대가 있습니다."),

    // 500
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버 오류가 발생했습니다."),
    IMAGE_UPLOAD_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "이미지 업로드 중 오류가 발생했습니다."),
    IMAGE_URL_GENERATION_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "이미지 접근 URL 생성에 실패했습니다."),
    AI_RESPONSE_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "AI 응답 생성 중 오류가 발생했습니다.");

    private final HttpStatus status;
    private final String message;

    ErrorCode(HttpStatus status, String message) {
        this.status = status;
        this.message = message;
    }
}
