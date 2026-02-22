/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.kuma.boot.common.utils.log.LogUtils
 *  com.kuma.boot.common.utils.servlet.ResponseUtils
 *  jakarta.servlet.ServletException
 *  jakarta.servlet.http.HttpServletRequest
 *  jakarta.servlet.http.HttpServletResponse
 *  org.springframework.security.core.Authentication
 *  org.springframework.security.oauth2.core.user.OAuth2User
 *  org.springframework.security.web.authentication.AuthenticationSuccessHandler
 */
package com.kuma.boot.security.spring.authentication.response.success;

import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.boot.common.utils.servlet.ResponseUtils;
import com.kuma.boot.security.spring.oauth2.token.JwtTokenGenerator;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

public class SocialAuthenticationSuccessHandler
implements AuthenticationSuccessHandler {
    private final JwtTokenGenerator jwtTokenGenerator;

    public SocialAuthenticationSuccessHandler(JwtTokenGenerator jwtTokenGenerator) {
        this.jwtTokenGenerator = jwtTokenGenerator;
    }

    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        LogUtils.error((String)"\u7b2c\u4e09\u65b9\u7528\u6237\u8ba4\u8bc1\u6210\u529f", (Object[])new Object[]{authentication});
        ResponseUtils.success((HttpServletResponse)response, (Object)this.jwtTokenGenerator.socialTokenResponse((OAuth2User)authentication.getPrincipal()));
    }
}

