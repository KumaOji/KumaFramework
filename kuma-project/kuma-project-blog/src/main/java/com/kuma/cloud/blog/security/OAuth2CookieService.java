package com.kuma.cloud.blog.security;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;

@Component
public class OAuth2CookieService {

    public static final String ACCESS_TOKEN_COOKIE = "blog_access_token";
    public static final String REFRESH_TOKEN_COOKIE = "blog_refresh_token";

    private final boolean secure;
    private final Duration refreshTokenMaxAge;

    public OAuth2CookieService(
            @Value("${blog.cookie-secure:false}") boolean secure,
            @Value("${blog.oauth2.refresh-token-max-age:30d}") Duration refreshTokenMaxAge) {
        this.secure = secure;
        this.refreshTokenMaxAge = refreshTokenMaxAge;
    }

    public void writeTokens(HttpServletResponse response, String accessToken, Instant expiresAt,
                            String refreshToken) {
        Duration accessMaxAge = expiresAt == null
                ? Duration.ofMinutes(5)
                : Duration.between(Instant.now(), expiresAt).isNegative()
                    ? Duration.ZERO
                    : Duration.between(Instant.now(), expiresAt);
        addCookie(response, ACCESS_TOKEN_COOKIE, accessToken, accessMaxAge);
        if (refreshToken != null && !refreshToken.isBlank()) {
            addCookie(response, REFRESH_TOKEN_COOKIE, refreshToken, refreshTokenMaxAge);
        }
    }

    public String resolveRefreshToken(HttpServletRequest request) {
        return resolveToken(request, REFRESH_TOKEN_COOKIE);
    }

    public String resolveAccessToken(HttpServletRequest request) {
        return resolveToken(request, ACCESS_TOKEN_COOKIE);
    }

    private String resolveToken(HttpServletRequest request, String cookieName) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            return null;
        }
        for (Cookie cookie : cookies) {
            if (cookieName.equals(cookie.getName())) {
                return cookie.getValue();
            }
        }
        return null;
    }

    public void clear(HttpServletResponse response) {
        addCookie(response, ACCESS_TOKEN_COOKIE, "", Duration.ZERO);
        addCookie(response, REFRESH_TOKEN_COOKIE, "", Duration.ZERO);
    }

    private void addCookie(HttpServletResponse response, String name, String value, Duration maxAge) {
        ResponseCookie cookie = ResponseCookie.from(name, value)
                .httpOnly(true)
                .secure(secure)
                .sameSite("Strict")
                .path("/")
                .maxAge(maxAge)
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }
}
