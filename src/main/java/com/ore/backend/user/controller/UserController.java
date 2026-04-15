package com.ore.backend.user.controller;

import com.ore.backend.user.dto.GoogleLoginRequest;
import com.ore.backend.user.dto.GoogleLoginResponse;
import com.ore.backend.user.dto.RefreshTokenResponse;
import com.ore.backend.user.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
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
    public ResponseEntity<GoogleLoginResponse> googleLogin(
            @RequestBody GoogleLoginRequest request
    ) {

        GoogleLoginResponse result = userService.loginWithGoogle(request);

        ResponseCookie cookie = ResponseCookie.from("refreshToken", result.getRefreshToken())
                .httpOnly(true)
                .path("/")
                .maxAge(1209600)
                .build();

        return ResponseEntity.ok()
                .header("Set-Cookie", cookie.toString())
                .body(result);
    }

    @PostMapping("/auth/refresh")
    public ResponseEntity<RefreshTokenResponse> refresh(
            HttpServletRequest request
    ) {
        RefreshTokenResponse response = userService.refreshAccessToken(request);
        return ResponseEntity.ok(response);
    }
}
