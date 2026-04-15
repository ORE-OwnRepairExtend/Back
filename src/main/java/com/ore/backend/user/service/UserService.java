package com.ore.backend.user.service;

import com.ore.backend.external.GoogleTokenVerifier;
import com.ore.backend.global.exception.CustomException;
import com.ore.backend.global.exception.ErrorCode;
import com.ore.backend.security.JwtProvider;
import com.ore.backend.user.dto.GoogleLoginRequest;
import com.ore.backend.user.dto.GoogleLoginResponse;
import com.ore.backend.user.dto.RefreshTokenResponse;
import com.ore.backend.user.entity.UserEntity;
import com.ore.backend.user.repository.UserRepository;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final JwtProvider jwtProvider;
    private final GoogleTokenVerifier googleTokenVerifier;

    public GoogleLoginResponse loginWithGoogle(GoogleLoginRequest request) {

        // idToken 검증
        if (request.getIdToken() == null || request.getIdToken().isBlank()) {
            throw new CustomException(ErrorCode.ID_TOKEN_MISSING);
        }

        // Google 토큰 검증
        GoogleTokenVerifier.GoogleUserInfo googleUser = googleTokenVerifier.verify(request.getIdToken());

        // 사용자 조회 or 생성
        UserEntity user = userRepository.findByProviderId(googleUser.getProviderId())
                .orElseGet(() -> userRepository.save(
                        UserEntity.builder()
                                .email(googleUser.getEmail())
                                .name(googleUser.getName())
                                .providerId(googleUser.getProviderId())
                                .profileImage(googleUser.getProfileImage())
                                .createdAt(LocalDateTime.now())
                                .build()
                ));

        // JWT 발급
        String accessToken = jwtProvider.createAccessToken(user.getUserId());
        String refreshToken = jwtProvider.createRefreshToken(user.getUserId());

        // 응답 생성
        return GoogleLoginResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .user(
                        GoogleLoginResponse.UserInfo.builder()
                                .userId(user.getUserId())
                                .email(user.getEmail())
                                .name(user.getName())
                                .profileImage(user.getProfileImage())
                                .build()
                )
                .build();
    }

    public RefreshTokenResponse refreshAccessToken(HttpServletRequest request) {

        String refreshToken = extractRefreshToken(request);

        if (refreshToken == null || refreshToken.isBlank()) {
            throw new CustomException(ErrorCode.REFRESH_TOKEN_MISSING);
        }

        // refreshToken 검증
        try {
            jwtProvider.validateToken(refreshToken);
            jwtProvider.validateRefreshToken(refreshToken);
        } catch (CustomException e) {
            throw new CustomException(ErrorCode.UNAUTHORIZED);
        }

        UUID userId = jwtProvider.getUserId(refreshToken);

        // accessToken 재발급
        String newAccessToken = jwtProvider.createAccessToken(userId);

        return new RefreshTokenResponse(newAccessToken);
    }

    private String extractRefreshToken(HttpServletRequest request) {

        if (request.getCookies() == null) {
            return null;
        }

        for (Cookie cookie : request.getCookies()) {
            if ("refreshToken".equals(cookie.getName())) {
                return cookie.getValue();
            }
        }

        return null;
    }
}