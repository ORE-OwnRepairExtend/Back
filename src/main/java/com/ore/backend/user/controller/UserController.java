package com.ore.backend.user.controller;

import com.ore.backend.security.JwtProvider;
import com.ore.backend.user.dto.*;
import com.ore.backend.user.service.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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

        ResponseCookie refreshCookie = ResponseCookie.from("refreshToken", loginResult.getRefreshToken())
                .httpOnly(true)
                .secure(true)
                .sameSite("None")
                .path("/")
                .maxAge(60 * 60 * 24 * 14)
                .build();

        response.addHeader("Set-Cookie", refreshCookie.toString());

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
                .secure(true)
                .sameSite("None")
                .path("/")
                .maxAge(0)
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

    @PatchMapping(
            value = "/users/me",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<UpdateMyInfoResponse> updateMyInfo(
            @RequestHeader("Authorization") String authorizationHeader,
            @RequestParam(required = false) String name,
            @RequestPart(required = false) MultipartFile profileImage
    ) {
        String accessToken = authorizationHeader.replace("Bearer ", "");

        UUID userId = jwtProvider.getUserId(accessToken);

        UpdateMyInfoResponse response = userService.updateMyInfo(userId, name, profileImage);

        return ResponseEntity.ok(response);
    }
}
