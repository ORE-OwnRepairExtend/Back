package com.ore.backend.user.service;

import com.ore.backend.external.GoogleTokenVerifier;
import com.ore.backend.global.exception.CustomException;
import com.ore.backend.global.exception.ErrorCode;
import com.ore.backend.security.JwtProvider;
import com.ore.backend.user.dto.GoogleLoginRequest;
import com.ore.backend.user.dto.GoogleLoginResponse;
import com.ore.backend.user.entity.UserEntity;
import com.ore.backend.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

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
}