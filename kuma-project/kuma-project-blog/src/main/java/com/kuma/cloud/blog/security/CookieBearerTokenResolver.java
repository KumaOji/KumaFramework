package com.kuma.cloud.blog.security;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.oauth2.server.resource.web.BearerTokenResolver;
import org.springframework.security.oauth2.server.resource.web.DefaultBearerTokenResolver;
import org.springframework.util.StringUtils;

/**
 * 同时支持标准 Authorization: Bearer 请求头和浏览器 HttpOnly Cookie。
 */
public class CookieBearerTokenResolver implements BearerTokenResolver {

    private final DefaultBearerTokenResolver delegate = new DefaultBearerTokenResolver();
    private final String cookieName;

    public CookieBearerTokenResolver(String cookieName) {
        this.cookieName = cookieName;
    }

    @Override
    public String resolve(HttpServletRequest request) {
        String bearerToken = delegate.resolve(request);
        if (StringUtils.hasText(bearerToken)) {
            return bearerToken;
        }

        // Access Token 过期后仍需允许刷新和注销端点读取 Refresh Token。
        String servletPath = request.getServletPath();
        if (servletPath.endsWith("/auth/refresh") || servletPath.endsWith("/auth/logout")) {
            return null;
        }

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
}
