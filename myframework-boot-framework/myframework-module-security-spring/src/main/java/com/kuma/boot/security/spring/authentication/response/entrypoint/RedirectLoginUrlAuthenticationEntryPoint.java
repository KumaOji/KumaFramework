/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  jakarta.servlet.ServletException
 *  jakarta.servlet.ServletRequest
 *  jakarta.servlet.http.HttpServletRequest
 *  jakarta.servlet.http.HttpServletResponse
 *  org.apache.commons.lang3.StringUtils
 *  org.dromara.hutool.http.server.servlet.ServletUtil
 *  org.springframework.security.core.AuthenticationException
 *  org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint
 */
package com.kuma.boot.security.spring.authentication.response.entrypoint;

import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.dromara.hutool.http.server.servlet.ServletUtil;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;

public class RedirectLoginUrlAuthenticationEntryPoint
extends LoginUrlAuthenticationEntryPoint {
    public RedirectLoginUrlAuthenticationEntryPoint(String loginFormUrl) {
        super(loginFormUrl);
    }

    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        super.commence(request, response, authException);
    }

    protected String buildRedirectUrlToLoginPage(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) {
        String urlToLoginPage = super.buildRedirectUrlToLoginPage(request, response, authException);
        Map paramMap = ServletUtil.getParamMap((ServletRequest)request);
        return urlToLoginPage + "?" + RedirectLoginUrlAuthenticationEntryPoint.getUrlParamsByMap(paramMap);
    }

    public static String getUrlParamsByMap(Map<String, String> map) {
        if (map == null) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, String> entry : map.entrySet()) {
            sb.append(entry.getKey()).append("=").append(entry.getValue());
            sb.append("&");
        }
        String s = sb.toString();
        if (s.endsWith("&")) {
            s = StringUtils.substringBeforeLast((String)s, (String)"&");
        }
        return s;
    }
}

