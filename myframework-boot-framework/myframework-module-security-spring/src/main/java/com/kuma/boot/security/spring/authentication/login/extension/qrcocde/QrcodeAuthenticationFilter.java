/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  jakarta.servlet.http.HttpServletRequest
 *  jakarta.servlet.http.HttpServletResponse
 *  org.dromara.hutool.core.text.StrUtil
 *  org.springframework.core.convert.converter.Converter
 *  org.springframework.http.HttpMethod
 *  org.springframework.security.authentication.AuthenticationCredentialsNotFoundException
 *  org.springframework.security.authentication.AuthenticationManager
 *  org.springframework.security.authentication.AuthenticationServiceException
 *  org.springframework.security.core.Authentication
 *  org.springframework.security.core.AuthenticationException
 *  org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter
 *  org.springframework.security.web.servlet.util.matcher.PathPatternRequestMatcher
 *  org.springframework.security.web.util.matcher.RequestMatcher
 *  org.springframework.util.Assert
 */
package com.kuma.boot.security.spring.authentication.login.extension.qrcocde;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.dromara.hutool.core.text.StrUtil;
import org.springframework.core.convert.converter.Converter;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.servlet.util.matcher.PathPatternRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.util.Assert;

public class QrcodeAuthenticationFilter
extends AbstractAuthenticationProcessingFilter {
    public static final String SPRING_SECURITY_FORM_UUID_KEY = "uuid";
    private static final PathPatternRequestMatcher DEFAULT_ANT_PATH_REQUEST_MATCHER = PathPatternRequestMatcher.withDefaults().matcher(HttpMethod.POST, "/login/qrcode");
    private String uuidParameter = "uuid";
    private Converter<HttpServletRequest, QrcodeAuthenticationToken> qrcodeAuthenticationTokenConverter = this.defaultConverter();
    private boolean postOnly = true;

    public QrcodeAuthenticationFilter() {
        super((RequestMatcher)DEFAULT_ANT_PATH_REQUEST_MATCHER);
    }

    public QrcodeAuthenticationFilter(AuthenticationManager authenticationManager) {
        super((RequestMatcher)DEFAULT_ANT_PATH_REQUEST_MATCHER, authenticationManager);
    }

    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        if (this.postOnly && !HttpMethod.POST.matches(request.getMethod())) {
            throw new AuthenticationServiceException("Authentication method not supported: " + request.getMethod());
        }
        QrcodeAuthenticationToken authRequest = (QrcodeAuthenticationToken)((Object)this.qrcodeAuthenticationTokenConverter.convert((Object)request));
        this.setDetails(request, authRequest);
        return this.getAuthenticationManager().authenticate((Authentication)authRequest);
    }

    private Converter<HttpServletRequest, QrcodeAuthenticationToken> defaultConverter() {
        return request -> {
            String username = request.getParameter(this.uuidParameter);
            username = username != null ? username.trim() : "";
            String authorization = request.getHeader("Authorization");
            if (StrUtil.isBlank((CharSequence)authorization)) {
                throw new AuthenticationCredentialsNotFoundException("");
            }
            return new QrcodeAuthenticationToken(username, "");
        };
    }

    protected void setDetails(HttpServletRequest request, QrcodeAuthenticationToken authRequest) {
        authRequest.setDetails(this.authenticationDetailsSource.buildDetails((Object)request));
    }

    public void setUsernameParameter(String usernameParameter) {
        Assert.hasText((String)usernameParameter, (String)"Username parameter must not be empty or null");
    }

    public void setPasswordParameter(String passwordParameter) {
        Assert.hasText((String)passwordParameter, (String)"Password parameter must not be empty or null");
    }

    public void setConverter(Converter<HttpServletRequest, QrcodeAuthenticationToken> converter) {
        Assert.notNull(converter, (String)"Converter must not be null");
        this.qrcodeAuthenticationTokenConverter = converter;
    }

    public void setPostOnly(boolean postOnly) {
        this.postOnly = postOnly;
    }
}

