/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  jakarta.servlet.http.HttpServletRequest
 *  jakarta.servlet.http.HttpServletResponse
 *  org.springframework.core.convert.converter.Converter
 *  org.springframework.http.HttpMethod
 *  org.springframework.security.authentication.AuthenticationManager
 *  org.springframework.security.authentication.AuthenticationServiceException
 *  org.springframework.security.core.Authentication
 *  org.springframework.security.core.AuthenticationException
 *  org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter
 *  org.springframework.security.web.servlet.util.matcher.PathPatternRequestMatcher
 *  org.springframework.security.web.util.matcher.RequestMatcher
 *  org.springframework.util.Assert
 */
package com.kuma.boot.security.spring.authentication.login.extension.sms;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.convert.converter.Converter;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.servlet.util.matcher.PathPatternRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.util.Assert;

public class SmsAuthenticationFilter
extends AbstractAuthenticationProcessingFilter {
    public static final String SPRING_SECURITY_FORM_PHONE_KEY = "phone";
    public static final String SPRING_SECURITY_FORM_CODE_KEY = "code";
    public static final String SPRING_SECURITY_FORM_TYPE_KEY = "type";
    private static final PathPatternRequestMatcher DEFAULT_ANT_PATH_REQUEST_MATCHER = PathPatternRequestMatcher.withDefaults().matcher(HttpMethod.POST, "/login/sms");
    private String phoneParameter = "phone";
    private String codeParameter = "code";
    private String typeParameter = "type";
    private Converter<HttpServletRequest, SmsAuthenticationToken> captchaAuthenticationTokenConverter = this.defaultConverter();
    private boolean postOnly = true;

    public SmsAuthenticationFilter() {
        super((RequestMatcher)DEFAULT_ANT_PATH_REQUEST_MATCHER);
    }

    public SmsAuthenticationFilter(AuthenticationManager authenticationManager) {
        super((RequestMatcher)DEFAULT_ANT_PATH_REQUEST_MATCHER, authenticationManager);
    }

    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        if (this.postOnly && !HttpMethod.POST.matches(request.getMethod())) {
            throw new AuthenticationServiceException("Authentication method not supported: " + request.getMethod());
        }
        SmsAuthenticationToken authRequest = (SmsAuthenticationToken)((Object)this.captchaAuthenticationTokenConverter.convert((Object)request));
        this.setDetails(request, authRequest);
        return this.getAuthenticationManager().authenticate((Authentication)authRequest);
    }

    private Converter<HttpServletRequest, SmsAuthenticationToken> defaultConverter() {
        return request -> {
            String phone = request.getParameter(this.phoneParameter);
            phone = phone != null ? phone.trim() : "";
            String code = request.getParameter(this.codeParameter);
            code = code != null ? code.trim() : "";
            String type = request.getParameter(this.typeParameter);
            type = type != null ? type.trim() : "";
            return new SmsAuthenticationToken(phone, code, type);
        };
    }

    protected void setDetails(HttpServletRequest request, SmsAuthenticationToken authRequest) {
        authRequest.setDetails(this.authenticationDetailsSource.buildDetails((Object)request));
    }

    public void setPhoneParameter(String phoneParameter) {
        Assert.hasText((String)phoneParameter, (String)"phoneParameter must not be empty or null");
        this.phoneParameter = phoneParameter;
    }

    public void setCodeParameter(String codeParameter) {
        Assert.hasText((String)codeParameter, (String)"codeParameter must not be empty or null");
        this.codeParameter = codeParameter;
    }

    public void setConverter(Converter<HttpServletRequest, SmsAuthenticationToken> converter) {
        Assert.notNull(converter, (String)"Converter must not be null");
        this.captchaAuthenticationTokenConverter = converter;
    }

    public void setPostOnly(boolean postOnly) {
        this.postOnly = postOnly;
    }

    public final String getPhoneParameter() {
        return this.phoneParameter;
    }

    public final String getCodeParameter() {
        return this.codeParameter;
    }

    public String getTypeParameter() {
        return this.typeParameter;
    }

    public void setTypeParameter(String typeParameter) {
        this.typeParameter = typeParameter;
    }

    public Converter<HttpServletRequest, SmsAuthenticationToken> getCaptchaAuthenticationTokenConverter() {
        return this.captchaAuthenticationTokenConverter;
    }

    public void setCaptchaAuthenticationTokenConverter(Converter<HttpServletRequest, SmsAuthenticationToken> captchaAuthenticationTokenConverter) {
        this.captchaAuthenticationTokenConverter = captchaAuthenticationTokenConverter;
    }

    public boolean isPostOnly() {
        return this.postOnly;
    }
}

