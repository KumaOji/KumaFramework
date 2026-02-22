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
package com.kuma.boot.security.spring.authentication.login.extension.captcha;

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

public class CaptchaAuthenticationFilter
extends AbstractAuthenticationProcessingFilter {
    public static final String TTC_SECURITY_EXTENSIONS_CAPTCHA_LOGIN_USERNAME_KEY = "username";
    public static final String TTC_SECURITY_EXTENSIONS_CAPTCHA_LOGIN_PASSWORD_KEY = "password";
    public static final String TTC_SECURITY_EXTENSIONS_CAPTCHA_LOGIN_VERIFICATION_CODE_KEY = "verification_code";
    public static final String TTC_SECURITY_EXTENSIONS_CAPTCHA_LOGIN_TYPE_KEY = "type";
    public static final String TTC_SECURITY_EXTENSIONS_CAPTCHA_LOGIN_URL = "/login/captcha";
    private static final PathPatternRequestMatcher DEFAULT_ANT_PATH_REQUEST_MATCHER = PathPatternRequestMatcher.withDefaults().matcher(HttpMethod.POST, "/login/captcha");
    private Converter<HttpServletRequest, CaptchaAuthenticationToken> captchaAuthenticationTokenConverter = new CaptchaAuthenticationConverter();
    private boolean postOnly = true;

    public CaptchaAuthenticationFilter() {
        super((RequestMatcher)DEFAULT_ANT_PATH_REQUEST_MATCHER);
    }

    public CaptchaAuthenticationFilter(AuthenticationManager authenticationManager) {
        super((RequestMatcher)DEFAULT_ANT_PATH_REQUEST_MATCHER, authenticationManager);
    }

    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        if (this.postOnly && !HttpMethod.POST.matches(request.getMethod())) {
            throw new AuthenticationServiceException("Authentication method not supported: " + request.getMethod());
        }
        CaptchaAuthenticationToken captchaAuthenticationToken = (CaptchaAuthenticationToken)((Object)this.captchaAuthenticationTokenConverter.convert((Object)request));
        if (captchaAuthenticationToken != null) {
            this.setDetails(request, captchaAuthenticationToken);
        }
        return this.getAuthenticationManager().authenticate((Authentication)captchaAuthenticationToken);
    }

    protected void setDetails(HttpServletRequest request, CaptchaAuthenticationToken captchaAuthenticationToken) {
        captchaAuthenticationToken.setDetails(this.authenticationDetailsSource.buildDetails((Object)request));
    }

    public void setConverter(Converter<HttpServletRequest, CaptchaAuthenticationToken> converter) {
        Assert.notNull(converter, (String)"Converter must not be null");
        this.captchaAuthenticationTokenConverter = converter;
    }

    public void setPostOnly(boolean postOnly) {
        this.postOnly = postOnly;
    }

    public void setUsernameParameter(String usernameParameter) {
        Converter<HttpServletRequest, CaptchaAuthenticationToken> converter = this.captchaAuthenticationTokenConverter;
        if (converter instanceof CaptchaAuthenticationConverter) {
            CaptchaAuthenticationConverter captchaAuthenticationConverter = (CaptchaAuthenticationConverter)converter;
            captchaAuthenticationConverter.setUsernameParameter(usernameParameter);
        }
    }

    public void setPasswordParameter(String passwordParameter) {
        Converter<HttpServletRequest, CaptchaAuthenticationToken> converter = this.captchaAuthenticationTokenConverter;
        if (converter instanceof CaptchaAuthenticationConverter) {
            CaptchaAuthenticationConverter captchaAuthenticationConverter = (CaptchaAuthenticationConverter)converter;
            captchaAuthenticationConverter.setPasswordParameter(passwordParameter);
        }
    }

    public void setTypeParameter(String typeParameter) {
        Converter<HttpServletRequest, CaptchaAuthenticationToken> converter = this.captchaAuthenticationTokenConverter;
        if (converter instanceof CaptchaAuthenticationConverter) {
            CaptchaAuthenticationConverter captchaAuthenticationConverter = (CaptchaAuthenticationConverter)converter;
            captchaAuthenticationConverter.setTypeParameter(typeParameter);
        }
    }

    public void setVerificationCodeParameter(String verificationCodeParameter) {
        Converter<HttpServletRequest, CaptchaAuthenticationToken> converter = this.captchaAuthenticationTokenConverter;
        if (converter instanceof CaptchaAuthenticationConverter) {
            CaptchaAuthenticationConverter captchaAuthenticationConverter = (CaptchaAuthenticationConverter)converter;
            captchaAuthenticationConverter.setVerificationCodeParameter(verificationCodeParameter);
        }
    }
}

