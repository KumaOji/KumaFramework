/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  com.fasterxml.jackson.databind.ObjectMapper
 *  jakarta.servlet.FilterChain
 *  jakarta.servlet.ServletException
 *  jakarta.servlet.ServletRequest
 *  jakarta.servlet.ServletResponse
 *  jakarta.servlet.http.HttpServletRequest
 *  jakarta.servlet.http.HttpServletResponse
 *  org.springframework.http.HttpStatus
 *  org.springframework.security.authentication.AuthenticationManager
 *  org.springframework.security.core.Authentication
 *  org.springframework.security.core.AuthenticationException
 *  org.springframework.security.web.authentication.AuthenticationConverter
 *  org.springframework.security.web.authentication.AuthenticationFailureHandler
 *  org.springframework.security.web.authentication.AuthenticationSuccessHandler
 *  org.springframework.security.web.util.matcher.RequestMatcher
 *  org.springframework.util.Assert
 *  org.springframework.web.filter.OncePerRequestFilter
 */
package com.kuma.boot.security.spring.authentication.login.extension.mfa;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kuma.boot.security.spring.authentication.login.extension.mfa.authentication.MfaAuthenticationToken;
import com.kuma.boot.security.spring.authentication.login.extension.mfa.context.MfaAuthenticationTokenContextHolder;
import com.kuma.boot.security.spring.authentication.login.extension.mfa.context.MfaTokenContext;
import com.kuma.boot.security.spring.authentication.login.extension.mfa.exception.MfaAuthenticationException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.HashMap;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationConverter;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.util.Assert;
import org.springframework.web.filter.OncePerRequestFilter;

public final class MfaAuthenticationFilter
extends OncePerRequestFilter {
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final AuthenticationManager authenticationManager;
    private final RequestMatcher requestMatcher;
    private AuthenticationConverter authenticationConverter;
    private AuthenticationSuccessHandler authenticationSuccessHandler = this::onAuthenticationSuccess;
    private AuthenticationFailureHandler authenticationFailureHandler = this::onAuthenticationFailure;

    public MfaAuthenticationFilter(AuthenticationManager authenticationManager, RequestMatcher requestMatcher) {
        Assert.notNull((Object)authenticationManager, (String)"authenticationManager cannot be null");
        Assert.notNull((Object)requestMatcher, (String)"requestMatcher cannot be null");
        this.authenticationManager = authenticationManager;
        this.requestMatcher = requestMatcher;
        this.authenticationConverter = new MfaAuthenticationConverter();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        if (!this.requestMatcher.matches(request)) {
            filterChain.doFilter((ServletRequest)request, (ServletResponse)response);
            return;
        }
        try {
            Authentication authentication = this.authenticationConverter.convert(request);
            if (authentication != null) {
                Authentication authenticationResult = this.authenticationManager.authenticate(authentication);
                this.authenticationSuccessHandler.onAuthenticationSuccess(request, response, authenticationResult);
            }
            filterChain.doFilter((ServletRequest)request, (ServletResponse)response);
        }
        catch (AuthenticationException e) {
            this.authenticationFailureHandler.onAuthenticationFailure(request, response, e);
        }
        finally {
            MfaAuthenticationTokenContextHolder.resetMfaTokenContext();
        }
    }

    public void setAuthenticationConverter(AuthenticationConverter authenticationConverter) {
        this.authenticationConverter = authenticationConverter;
    }

    public void setAuthenticationSuccessHandler(AuthenticationSuccessHandler authenticationSuccessHandler) {
        this.authenticationSuccessHandler = authenticationSuccessHandler;
    }

    public void setAuthenticationFailureHandler(AuthenticationFailureHandler authenticationFailureHandler) {
        this.authenticationFailureHandler = authenticationFailureHandler;
    }

    private void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        MfaAuthenticationToken mfaAuthenticationToken = (MfaAuthenticationToken)authentication;
        MfaTokenContext context = new MfaTokenContext(mfaAuthenticationToken.isMfa());
        MfaAuthenticationTokenContextHolder.setMfaTokenContext(context);
    }

    private void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException {
        HashMap<String, Object> responseClaims = new HashMap<String, Object>();
        responseClaims.put("code", HttpStatus.BAD_REQUEST.value());
        if (exception instanceof MfaAuthenticationException) {
            MfaAuthenticationException mfaAuthenticationException = (MfaAuthenticationException)exception;
            responseClaims.put("message", mfaAuthenticationException.getMessage());
        } else {
            responseClaims.put("message", "invalid code");
        }
        try (PrintWriter writer = response.getWriter();){
            ((Writer)writer).write(this.objectMapper.writeValueAsString(responseClaims));
        }
    }
}

