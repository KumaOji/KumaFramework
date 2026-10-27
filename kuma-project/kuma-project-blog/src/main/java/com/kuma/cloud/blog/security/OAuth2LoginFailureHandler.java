package com.kuma.cloud.blog.security;

import com.kuma.boot.auditlog.core.ServletAuthenticationAuditRecorder;
import lombok.RequiredArgsConstructor;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2LoginFailureHandler implements AuthenticationFailureHandler {

    private final ServletAuthenticationAuditRecorder audit;

    @Override
    public void onAuthenticationFailure(
            HttpServletRequest request, HttpServletResponse response, AuthenticationException exception)
            throws IOException, ServletException {
        audit.record("blog.authentication", null, false, "oauth2_login_failed", request);
        log.warn("OAuth2 login failed: uri={}, type={}", request.getRequestURI(), exception.getClass().getSimpleName());
        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "OAuth2 登录失败");
    }
}
