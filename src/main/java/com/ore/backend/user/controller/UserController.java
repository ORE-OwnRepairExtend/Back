package com.ore.backend.user.controller;

import com.ore.backend.user.dto.*;
import com.ore.backend.user.service.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

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

}
