package com.ore.backend.user.controller;

import com.ore.backend.security.JwtProvider;
import com.ore.backend.user.dto.*;
import com.ore.backend.user.service.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final JwtProvider jwtProvider;

    @PostMapping("/auth/google")
    public GoogleLoginResponse loginWithGoogle(
            @RequestBody GoogleLoginRequest request,
            HttpServletResponse response
    ) {
        LoginResult loginResult = userService.loginWithGoogle(request);

        Cookie cookie = new Cookie("refreshToken", loginResult.getRefreshToken());
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge(60 * 60 * 24 * 14); // 14일

        response.addCookie(cookie);

        return loginResult.getResponse();
    }

    @PostMapping("/auth/refresh")
    public ResponseEntity<RefreshTokenResponse> refresh(
            HttpServletRequest request
    ) {
        RefreshTokenResponse response = userService.refreshAccessToken(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/auth/logout")
    public ResponseEntity<LogoutResponse> logout(
            HttpServletRequest request,
            HttpServletResponse response
    ) {
        userService.logout(request);

        ResponseCookie deleteCookie = ResponseCookie.from("refreshToken", "")
                .httpOnly(true)
                .secure(false) // 로컬 개발이면 false, 배포 HTTPS면 true
                .path("/")
                .maxAge(0)
                .sameSite("Lax")
                .build();

        response.addHeader("Set-Cookie", deleteCookie.toString());

        return ResponseEntity.ok(new LogoutResponse("로그아웃 완료"));
    }

    @GetMapping("/users/me")
    public ResponseEntity<GetMyInfoResponse> getMyInfo(
            @RequestHeader("Authorization") String authorizationHeader
    ) {
        String accessToken = authorizationHeader.replace("Bearer ", "");

        UUID userId = jwtProvider.getUserId(accessToken);

        GetMyInfoResponse response = userService.getMyInfo(userId);

        return ResponseEntity.ok(response);
    }

    @PatchMapping("/users/me")
    public ResponseEntity<UpdateMyInfoResponse> updateMyInfo(
            @RequestHeader("Authorization") String authorizationHeader,
            @RequestBody UpdateMyInfoRequest request
    ) {
        String accessToken = authorizationHeader.replace("Bearer ", "");

        UUID userId = jwtProvider.getUserId(accessToken);

        UpdateMyInfoResponse response = userService.updateMyInfo(userId, request);

        return ResponseEntity.ok(response);
    }
}
