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
import com.ore.backend.user.dto.LoginResult;
import com.ore.backend.user.entity.RefreshTokenEntity;
import com.ore.backend.user.repository.RefreshTokenRepository;
import com.ore.backend.user.dto.GetMyInfoResponse;
import com.ore.backend.user.dto.UpdateMyInfoRequest;
import com.ore.backend.user.dto.UpdateMyInfoResponse;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final JwtProvider jwtProvider;
    private final GoogleTokenVerifier googleTokenVerifier;
    private final RefreshTokenRepository refreshTokenRepository;

    @Transactional
    public LoginResult loginWithGoogle(GoogleLoginRequest request) {

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

        // refreshToken db 저장
        RefreshTokenEntity refreshTokenEntity = refreshTokenRepository.findByUser(user)
                .orElseGet(() -> RefreshTokenEntity.builder()
                        .user(user)
                        .refreshToken(refreshToken)
                        .build()
                );

        refreshTokenEntity.updateRefreshToken(refreshToken);
        refreshTokenRepository.save(refreshTokenEntity);

        // 클라이언트 응답 바디
        GoogleLoginResponse response = GoogleLoginResponse.builder()
                .accessToken(accessToken)
                .user(
                        GoogleLoginResponse.UserInfo.builder()
                                .userId(user.getUserId())
                                .email(user.getEmail())
                                .name(user.getName())
                                .profileImage(user.getProfileImage())
                                .build()
                )
                .build();

        // 서버 내부에서만 쓰는 임시 결과 객체
        return LoginResult.builder()
                .response(response)
                .refreshToken(refreshToken)
                .build();
    }

    @Transactional(readOnly = true)
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

        refreshTokenRepository.findByRefreshToken(refreshToken)
                .orElseThrow(() -> new CustomException(ErrorCode.UNAUTHORIZED));

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

    @Transactional
    public void logout(HttpServletRequest request) {

        // 쿠키에서 refreshToken 추출
        String refreshToken = extractRefreshToken(request);

        // refreshToken 없으면 예외 처리
        if (refreshToken == null || refreshToken.isBlank()) {
            throw new CustomException(ErrorCode.REFRESH_TOKEN_MISSING);
        }

        // DB에 저장된 refreshToken인지 확인
        refreshTokenRepository.findByRefreshToken(refreshToken)
                .orElseThrow(() -> new CustomException(ErrorCode.UNAUTHORIZED));

        // refreshToken 무효화 처리
        refreshTokenRepository.deleteByRefreshToken(refreshToken);
    }

    @Transactional(readOnly = true)
    public GetMyInfoResponse getMyInfo(UUID userId) {

        // 사용자 조회
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        // 사용자 정보 응답 생성
        return GetMyInfoResponse.builder()
                .userId(user.getUserId())
                .email(user.getEmail())
                .name(user.getName())
                .profileImage(user.getProfileImage())
                .createdAt(user.getCreatedAt())
                .build();
    }

    @Transactional
    public UpdateMyInfoResponse updateMyInfo(UUID userId, UpdateMyInfoRequest request) {

        // 수정할 값이 없는 경우
        if ((request.getName() == null || request.getName().isBlank())
                && (request.getProfileImage() == null || request.getProfileImage().isBlank())) {
            throw new CustomException(ErrorCode.NO_UPDATE_VALUE);
        }

        // 사용자 조회
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        // 사용자 정보 수정
        user.updateProfile(request.getName(), request.getProfileImage());

        // 수정 결과 응답
        return UpdateMyInfoResponse.builder()
                .userId(user.getUserId())
                .name(user.getName())
                .profileImage(user.getProfileImage())
                .build();
    }
}