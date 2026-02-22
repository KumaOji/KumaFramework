/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.kuma.boot.common.utils.log.LogUtils
 *  com.kuma.boot.common.utils.servlet.ResponseUtils
 *  jakarta.servlet.ServletException
 *  jakarta.servlet.http.HttpServletRequest
 *  jakarta.servlet.http.HttpServletResponse
 *  org.springframework.security.core.AuthenticationException
 *  org.springframework.security.web.authentication.AuthenticationFailureHandler
 */
package com.kuma.boot.security.spring.authentication.response.failure;

import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.boot.common.utils.servlet.ResponseUtils;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;

public class SocialAuthenticationFailureHandler
implements AuthenticationFailureHandler {
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        LogUtils.error((String)"\u7528\u6237\u8ba4\u8bc1\u5931\u8d25", (Object[])new Object[]{authException});
        ResponseUtils.fail((HttpServletResponse)response, (Object)authException.getMessage());
    }
}

