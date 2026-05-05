package com.ore.backend.global.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {

    // 400
    ID_TOKEN_MISSING(HttpStatus.BAD_REQUEST, "ID 토큰이 필요합니다."),
    REFRESH_TOKEN_MISSING(HttpStatus.BAD_REQUEST, "Refresh 토큰이 필요합니다."),
    NO_UPDATE_VALUE(HttpStatus.BAD_REQUEST, "수정할 값이 없습니다."),

    // 401
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "인증 정보가 유효하지 않습니다."),
    INVALID_GOOGLE_TOKEN(HttpStatus.UNAUTHORIZED, "Google 토큰이 유효하지 않습니다."),
    TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, "토큰이 만료되었습니다."),
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "유효하지 않은 토큰입니다."),

    // 404
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다."),

    // 500
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버 오류가 발생했습니다.");

    private final HttpStatus status;
    private final String message;

    ErrorCode(HttpStatus status, String message) {
        this.status = status;
        this.message = message;
    }
}