package com.ore.backend.security;

import com.ore.backend.global.exception.CustomException;
import com.ore.backend.global.exception.ErrorCode;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.UUID;

@Component
public class JwtProvider {

    @Value("${jwt.secret}")
    private String secret;

    private SecretKey key;

    private final long ACCESS_TOKEN_EXPIRE = 1000 * 60 * 60;
    private final long REFRESH_TOKEN_EXPIRE = 1000 * 60 * 60 * 24 * 14;

    @PostConstruct
    public void init() {
        key = Keys.hmacShaKeyFor(secret.getBytes());
    }

    public String createAccessToken(UUID userId) {
        return createToken(userId, ACCESS_TOKEN_EXPIRE, "access");
    }

    public String createRefreshToken(UUID userId) {
        return createToken(userId, REFRESH_TOKEN_EXPIRE, "refresh");
    }

    private String createToken(UUID userId, long expireTime, String type) {
        Date now = new Date();

        return Jwts.builder()
                .setSubject(userId.toString())
                .claim("type", type)
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + expireTime))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public UUID getUserId(String token) {
        return UUID.fromString(parseClaims(token).getSubject());
    }

    public void validateToken(String token) {
        try {
            parseClaims(token);
        } catch (ExpiredJwtException e) {
            throw new CustomException(ErrorCode.TOKEN_EXPIRED);
        } catch (JwtException e) {
            throw new CustomException(ErrorCode.INVALID_TOKEN);
        }
    }

    // refreshToken 전용 검증
    public void validateRefreshToken(String token) {
        Claims claims = parseClaims(token);

        String type = claims.get("type", String.class);

        if (!"refresh".equals(type)) {
            throw new CustomException(ErrorCode.INVALID_TOKEN);
        }
    }

    private Claims parseClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}