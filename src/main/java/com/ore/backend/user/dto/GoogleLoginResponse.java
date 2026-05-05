package com.ore.backend.user.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Getter
@Builder
public class GoogleLoginResponse {

    private String accessToken;
    private UserInfo user;

    @Getter
    @Builder
    public static class UserInfo {
        private UUID userId;
        private String email;
        private String name;
        private String profileImage;
    }
}