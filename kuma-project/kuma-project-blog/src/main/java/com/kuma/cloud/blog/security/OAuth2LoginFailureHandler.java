package com.kuma.cloud.blog.security;

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
public class OAuth2LoginFailureHandler implements AuthenticationFailureHandler {

    @Override
    public void onAuthenticationFailure(
            HttpServletRequest request, HttpServletResponse response, AuthenticationException exception)
            throws IOException, ServletException {
        log.error(
                "OAuth2 登录失败: uri={}, query={}, reason={}",
                request.getRequestURI(),
                request.getQueryString(),
                exception.getMessage(),
                exception);
        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "OAuth2 登录失败");
    }
}
