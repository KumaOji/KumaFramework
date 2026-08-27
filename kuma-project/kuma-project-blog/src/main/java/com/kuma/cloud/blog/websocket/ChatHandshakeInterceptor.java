package com.kuma.cloud.blog.websocket;

import com.kuma.cloud.blog.security.BlogJwtAuthenticationConverter;
import com.kuma.cloud.blog.security.BlogUserDetails;
import com.kuma.cloud.blog.security.OAuth2CookieService;
import com.kuma.cloud.blog.service.ChatBlacklistService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.Map;

public class ChatHandshakeInterceptor implements HandshakeInterceptor {

    private final JwtDecoder jwtDecoder;
    private final BlogJwtAuthenticationConverter authenticationConverter;
    private final ChatBlacklistService blacklistService;

    public ChatHandshakeInterceptor(
            JwtDecoder jwtDecoder,
            BlogJwtAuthenticationConverter authenticationConverter,
            ChatBlacklistService blacklistService) {
        this.jwtDecoder = jwtDecoder;
        this.authenticationConverter = authenticationConverter;
        this.blacklistService = blacklistService;
    }

    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response,
                                   WebSocketHandler wsHandler, Map<String, Object> attributes) {
        if (request instanceof ServletServerHttpRequest servletRequest) {
            HttpServletRequest httpRequest = servletRequest.getServletRequest();
            String token = extractCookie(httpRequest, OAuth2CookieService.ACCESS_TOKEN_COOKIE);
            if (token != null) {
                try {
                    BlogUserDetails principal = (BlogUserDetails) authenticationConverter
                            .convert(jwtDecoder.decode(token))
                            .getPrincipal();
                    if (blacklistService.isBlocked(principal.getLoginResponse().getEmail())) {
                        return false;
                    }
                    attributes.put("loginResponse", principal.getLoginResponse());
                } catch (RuntimeException ignored) {
                    // 匿名用户仍可加入公开聊天室；无效 Token 不建立登录身份。
                }
            }
        }
        return true;
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response,
                               WebSocketHandler wsHandler, Exception exception) {
    }

    private String extractCookie(HttpServletRequest request, String name) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) return null;
        for (Cookie cookie : cookies) {
            if (name.equals(cookie.getName())) return cookie.getValue();
        }
        return null;
    }
}
