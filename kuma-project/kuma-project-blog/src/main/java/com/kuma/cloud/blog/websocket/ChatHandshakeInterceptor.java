package com.kuma.cloud.blog.websocket;

import com.kuma.cloud.blog.domain.vo.LoginVO;
import com.kuma.cloud.blog.service.ChatBlacklistService;
import com.kuma.cloud.blog.service.TokenService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.Map;

public class ChatHandshakeInterceptor implements HandshakeInterceptor {

    private static final String TOKEN_COOKIE = "blog_token";

    private final TokenService tokenService;
    private final ChatBlacklistService blacklistService;

    public ChatHandshakeInterceptor(TokenService tokenService, ChatBlacklistService blacklistService) {
        this.tokenService = tokenService;
        this.blacklistService = blacklistService;
    }

    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response,
                                   WebSocketHandler wsHandler, Map<String, Object> attributes) {
        if (request instanceof ServletServerHttpRequest servletRequest) {
            HttpServletRequest httpRequest = servletRequest.getServletRequest();
            String token = extractCookie(httpRequest, TOKEN_COOKIE);
            if (token != null) {
                LoginVO lr = tokenService.getLoginResponseByToken(token);
                if (lr != null && lr.getUserId() != null) {
                    if (blacklistService.isBlocked(lr.getEmail())) {
                        return false;
                    }
                    attributes.put("loginResponse", lr);
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
