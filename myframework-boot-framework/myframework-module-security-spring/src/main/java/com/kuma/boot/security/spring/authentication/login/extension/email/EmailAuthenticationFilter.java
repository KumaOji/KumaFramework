/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  jakarta.servlet.ServletException
 *  jakarta.servlet.http.HttpServletRequest
 *  jakarta.servlet.http.HttpServletResponse
 *  org.dromara.hutool.core.text.StrUtil
 *  org.springframework.core.convert.converter.Converter
 *  org.springframework.http.HttpMethod
 *  org.springframework.security.authentication.AuthenticationManager
 *  org.springframework.security.authentication.AuthenticationServiceException
 *  org.springframework.security.core.Authentication
 *  org.springframework.security.core.AuthenticationException
 *  org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter
 *  org.springframework.security.web.servlet.util.matcher.PathPatternRequestMatcher
 *  org.springframework.security.web.util.matcher.RequestMatcher
 */
package com.kuma.boot.security.spring.authentication.login.extension.email;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.dromara.hutool.core.text.StrUtil;
import org.springframework.core.convert.converter.Converter;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.servlet.util.matcher.PathPatternRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

public class EmailAuthenticationFilter
extends AbstractAuthenticationProcessingFilter {
    public static final String TTC_SECURITY_EXTENSIONS_EMAIL_LOGIN_EMAIL_KEY = "email";
    public static final String TTC_SECURITY_EXTENSIONS_EMAIL_LOGIN_EMAIL_CODE_KEY = "emailCode";
    public static final String TTC_SECURITY_EXTENSIONS_EMAIL_LOGIN_URL = "/login/email";
    private boolean postOnly = true;
    private Converter<HttpServletRequest, EmailAuthenticationToken> emailAuthenticationTokenConverter = new EmailAuthenticationConverter();
    private static final PathPatternRequestMatcher DEFAULT_ANT_PATH_REQUEST_MATCHER = PathPatternRequestMatcher.withDefaults().matcher(HttpMethod.POST, "/login/email");

    public EmailAuthenticationFilter() {
        super((RequestMatcher)DEFAULT_ANT_PATH_REQUEST_MATCHER);
    }

    public EmailAuthenticationFilter(AuthenticationManager authenticationManager) {
        super((RequestMatcher)DEFAULT_ANT_PATH_REQUEST_MATCHER, authenticationManager);
    }

    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException, IOException, ServletException {
        if (this.postOnly && !HttpMethod.POST.matches(request.getMethod())) {
            throw new AuthenticationServiceException("Authentication method not supported: " + request.getMethod());
        }
        EmailAuthenticationToken emailAuthenticationToken = (EmailAuthenticationToken)((Object)this.emailAuthenticationTokenConverter.convert((Object)request));
        this.setDetails(request, emailAuthenticationToken);
        return this.getAuthenticationManager().authenticate((Authentication)emailAuthenticationToken);
    }

    public void setDetails(HttpServletRequest request, EmailAuthenticationToken token) {
        token.setDetails(this.authenticationDetailsSource.buildDetails((Object)request));
    }

    private void checkEmailCode(String emailCode) {
        String verifyCode = "123456";
        if (StrUtil.isEmpty((CharSequence)verifyCode)) {
            throw new AuthenticationServiceException("\u8bf7\u91cd\u65b0\u7533\u8bf7\u9a8c\u8bc1\u7801!");
        }
        if (!verifyCode.equalsIgnoreCase(emailCode)) {
            throw new AuthenticationServiceException("\u9a8c\u8bc1\u7801\u9519\u8bef!");
        }
    }

    public boolean isPostOnly() {
        return this.postOnly;
    }

    public void setPostOnly(boolean postOnly) {
        this.postOnly = postOnly;
    }

    public Converter<HttpServletRequest, EmailAuthenticationToken> getEmailAuthenticationTokenConverter() {
        return this.emailAuthenticationTokenConverter;
    }

    public void setEmailAuthenticationTokenConverter(Converter<HttpServletRequest, EmailAuthenticationToken> emailAuthenticationTokenConverter) {
        this.emailAuthenticationTokenConverter = emailAuthenticationTokenConverter;
    }

    public void setEmailParameter(String emailParameter) {
        Converter<HttpServletRequest, EmailAuthenticationToken> converter = this.emailAuthenticationTokenConverter;
        if (converter instanceof EmailAuthenticationConverter) {
            EmailAuthenticationConverter emailAuthenticationConverter = (EmailAuthenticationConverter)converter;
            emailAuthenticationConverter.setEmailParameter(emailParameter);
        }
    }

    public void setEmailCodeParameter(String emailCodeParameter) {
        Converter<HttpServletRequest, EmailAuthenticationToken> converter = this.emailAuthenticationTokenConverter;
        if (converter instanceof EmailAuthenticationConverter) {
            EmailAuthenticationConverter emailAuthenticationConverter = (EmailAuthenticationConverter)converter;
            emailAuthenticationConverter.setEmailCodeParameter(emailCodeParameter);
        }
    }
}

