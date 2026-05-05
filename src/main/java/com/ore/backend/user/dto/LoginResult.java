package com.ore.backend.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class LoginResult {
    private GoogleLoginResponse response;
    private String refreshToken;
}