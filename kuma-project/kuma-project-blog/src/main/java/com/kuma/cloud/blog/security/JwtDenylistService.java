package com.kuma.cloud.blog.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Duration;
import java.time.Instant;
import java.util.HexFormat;

@Service
public class JwtDenylistService {

    private static final String JWT_KEY_PREFIX = "blog:jwt:deny:";
    private static final String REFRESH_KEY_PREFIX = "blog:refresh:deny:";

    private final StringRedisTemplate redisTemplate;
    private final Duration refreshTokenTtl;

    public JwtDenylistService(
            StringRedisTemplate redisTemplate,
            @Value("${blog.oauth2.refresh-token-max-age:30d}") Duration refreshTokenTtl) {
        this.redisTemplate = redisTemplate;
        this.refreshTokenTtl = refreshTokenTtl;
    }

    public void revoke(Jwt jwt) {
        Instant expiresAt = jwt.getExpiresAt();
        if (expiresAt == null) {
            return;
        }
        Duration ttl = Duration.between(Instant.now(), expiresAt);
        if (ttl.isNegative() || ttl.isZero()) {
            return;
        }
        redisTemplate.opsForValue().set(jwtKey(jwt.getTokenValue()), "1", ttl);
    }

    public boolean isRevoked(Jwt jwt) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(jwtKey(jwt.getTokenValue())));
    }

    /**
     * 将不透明 Refresh Token 加入本地黑名单，TTL 覆盖其最大有效期。
     * UAA 撤销失败时，本地刷新接口仍会拒绝该 Token。
     */
    public void revokeRefreshToken(String refreshToken) {
        if (!StringUtils.hasText(refreshToken)) {
            return;
        }
        redisTemplate.opsForValue().set(refreshKey(refreshToken), "1", refreshTokenTtl);
    }

    public boolean isRefreshTokenRevoked(String refreshToken) {
        if (!StringUtils.hasText(refreshToken)) {
            return false;
        }
        return Boolean.TRUE.equals(redisTemplate.hasKey(refreshKey(refreshToken)));
    }

    private String jwtKey(String token) {
        return JWT_KEY_PREFIX + sha256(token);
    }

    private String refreshKey(String token) {
        return REFRESH_KEY_PREFIX + sha256(token);
    }

    private String sha256(String token) {
        try {
            byte[] digest = MessageDigest.getInstance("SHA-256")
                    .digest(token.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(digest);
        } catch (NoSuchAlgorithmException exception) {
            throw new IllegalStateException("JVM 不支持 SHA-256", exception);
        }
    }
}
