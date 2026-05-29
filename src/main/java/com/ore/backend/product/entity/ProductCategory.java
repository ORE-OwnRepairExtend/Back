package com.ore.backend.product.entity;

public enum ProductCategory {
    MOBILE("모바일"),
    PC_LAPTOP("PC/노트북"),
    KITCHEN("주방"),
    LIVING("생활"),
    VIDEO_AUDIO("영상/음향"),
    ETC("기타");

    private final String displayName;

    ProductCategory(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}