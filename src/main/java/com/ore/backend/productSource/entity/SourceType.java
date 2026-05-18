package com.ore.backend.productSource.entity;

import com.ore.backend.global.exception.CustomException;
import com.ore.backend.global.exception.ErrorCode;

public enum SourceType {
    RECEIPT,
    SMS,
    STICKER,
    MANUAL;

    public static SourceType from(String value) {
        try {
            return SourceType.valueOf(value.toUpperCase());
        } catch (Exception e) {
            throw new CustomException(ErrorCode.INVALID_SOURCE_TYPE);
        }
    }
}
