/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  jakarta.servlet.ServletException
 *  jakarta.servlet.http.HttpServletRequest
 *  jakarta.servlet.http.HttpServletResponse
 *  org.springframework.core.convert.converter.Converter
 *  org.springframework.http.HttpMethod
 *  org.springframework.security.authentication.AuthenticationManager
 *  org.springframework.security.authentication.AuthenticationServiceException
 *  org.springframework.security.core.Authentication
 *  org.springframework.security.core.AuthenticationException
 *  org.springframework.security.core.context.SecurityContextHolder
 *  org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter
 *  org.springframework.security.web.servlet.util.matcher.PathPatternRequestMatcher
 *  org.springframework.security.web.util.matcher.RequestMatcher
 *  org.springframework.util.Assert
 */
package com.kuma.boot.security.spring.authentication.login.extension.account;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.core.convert.converter.Converter;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.servlet.util.matcher.PathPatternRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.util.Assert;

public class AccountAuthenticationFilter
extends AbstractAuthenticationProcessingFilter {
    public static final String TTC_SECURITY_EXTENSIONS_ACCOUNT_LOGIN_USERNAME_KEY = "username";
    public static final String TTC_SECURITY_EXTENSIONS_ACCOUNT_LOGIN_PASSWORD_KEY = "password";
    public static final String TTC_SECURITY_EXTENSIONS_ACCOUNT_LOGIN_TYPE_KEY = "type";
    public static final String TTC_SECURITY_EXTENSIONS_ACCOUNT_LOGIN_URL = "/login/account";
    private static final PathPatternRequestMatcher DEFAULT_ANT_PATH_REQUEST_MATCHER = PathPatternRequestMatcher.withDefaults().matcher(HttpMethod.POST, "/login/account");
    private Converter<HttpServletRequest, AccountAuthenticationToken> accountVerificationAuthenticationTokenConverter = new AccountAuthenticationConverter();
    private boolean postOnly = true;

    public AccountAuthenticationFilter() {
        super((RequestMatcher)DEFAULT_ANT_PATH_REQUEST_MATCHER);
    }

    public AccountAuthenticationFilter(AuthenticationManager authenticationManager) {
        super((RequestMatcher)DEFAULT_ANT_PATH_REQUEST_MATCHER, authenticationManager);
    }

    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        if (this.postOnly && !HttpMethod.POST.matches(request.getMethod())) {
            throw new AuthenticationServiceException("Authentication method not supported: " + request.getMethod());
        }
        AccountAuthenticationToken accountAuthenticationToken = (AccountAuthenticationToken)((Object)this.accountVerificationAuthenticationTokenConverter.convert((Object)request));
        if (accountAuthenticationToken != null) {
            this.setDetails(request, accountAuthenticationToken);
        }
        return this.getAuthenticationManager().authenticate((Authentication)accountAuthenticationToken);
    }

    protected void setDetails(HttpServletRequest request, AccountAuthenticationToken authRequest) {
        authRequest.setDetails(this.authenticationDetailsSource.buildDetails((Object)request));
    }

    public void setConverter(Converter<HttpServletRequest, AccountAuthenticationToken> converter) {
        Assert.notNull(converter, (String)"Converter must not be null");
        this.accountVerificationAuthenticationTokenConverter = converter;
    }

    public void setPostOnly(boolean postOnly) {
        this.postOnly = postOnly;
    }

    public void setUsernameParameter(String usernameParameter) {
        Converter<HttpServletRequest, AccountAuthenticationToken> converter = this.accountVerificationAuthenticationTokenConverter;
        if (converter instanceof AccountAuthenticationConverter) {
            AccountAuthenticationConverter accountAuthenticationConverter = (AccountAuthenticationConverter)converter;
            accountAuthenticationConverter.setUsernameParameter(usernameParameter);
        }
    }

    public void setPasswordParameter(String passwordParameter) {
        Converter<HttpServletRequest, AccountAuthenticationToken> converter = this.accountVerificationAuthenticationTokenConverter;
        if (converter instanceof AccountAuthenticationConverter) {
            AccountAuthenticationConverter accountAuthenticationConverter = (AccountAuthenticationConverter)converter;
            accountAuthenticationConverter.setPasswordParameter(passwordParameter);
        }
    }

    public void setTypeParameter(String typeParameter) {
        Converter<HttpServletRequest, AccountAuthenticationToken> converter = this.accountVerificationAuthenticationTokenConverter;
        if (converter instanceof AccountAuthenticationConverter) {
            AccountAuthenticationConverter accountAuthenticationConverter = (AccountAuthenticationConverter)converter;
            accountAuthenticationConverter.setTypeParameter(typeParameter);
        }
    }

    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException, ServletException {
        SecurityContextHolder.clearContext();
        this.getRememberMeServices().loginFail(request, response);
        this.getFailureHandler().onAuthenticationFailure(request, response, failed);
    }
}

