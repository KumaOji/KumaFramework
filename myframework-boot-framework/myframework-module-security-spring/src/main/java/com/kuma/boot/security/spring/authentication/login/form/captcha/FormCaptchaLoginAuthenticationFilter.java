/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  jakarta.servlet.http.HttpServletRequest
 *  jakarta.servlet.http.HttpServletResponse
 *  org.apache.commons.lang3.StringUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.security.authentication.AuthenticationManager
 *  org.springframework.security.authentication.AuthenticationServiceException
 *  org.springframework.security.core.Authentication
 *  org.springframework.security.core.AuthenticationException
 *  org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
 */
package com.kuma.boot.security.spring.authentication.login.form.captcha;

import com.kuma.boot.security.spring.utils.SymmetricUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

public class FormCaptchaLoginAuthenticationFilter
extends UsernamePasswordAuthenticationFilter {
    private boolean postOnly = true;
    private static final Logger log = LoggerFactory.getLogger(FormCaptchaLoginAuthenticationFilter.class);

    public FormCaptchaLoginAuthenticationFilter() {
    }

    public FormCaptchaLoginAuthenticationFilter(AuthenticationManager authenticationManager) {
        super(authenticationManager);
    }

    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        if (this.postOnly && !"POST".equals(request.getMethod())) {
            throw new AuthenticationServiceException("Authentication method not supported: " + request.getMethod());
        }
        FormCaptchaLoginAuthenticationToken formCaptchaLoginAuthenticationToken = this.getAuthenticationToken(request);
        this.setDetails(request, formCaptchaLoginAuthenticationToken);
        return this.getAuthenticationManager().authenticate((Authentication)formCaptchaLoginAuthenticationToken);
    }

    private FormCaptchaLoginAuthenticationToken getAuthenticationToken(HttpServletRequest request) {
        String username = this.obtainUsername(request);
        String password = this.obtainPassword(request);
        String key = request.getParameter("symmetric");
        if (StringUtils.isBlank((CharSequence)username)) {
            username = "";
        }
        if (StringUtils.isBlank((CharSequence)password)) {
            password = "";
        }
        if (StringUtils.isNotBlank((CharSequence)key) && StringUtils.isNotBlank((CharSequence)username) && StringUtils.isNotBlank((CharSequence)password)) {
            byte[] byteKey = SymmetricUtils.getDecryptedSymmetricKey(key);
            username = SymmetricUtils.decrypt(username, byteKey);
            password = SymmetricUtils.decrypt(password, byteKey);
            log.info("Decrypt Username is : [{}], Password is : [{}]", (Object)username, (Object)password);
        }
        return new FormCaptchaLoginAuthenticationToken(username, password);
    }

    public void setPostOnly(boolean postOnly) {
        super.setPostOnly(postOnly);
        this.postOnly = postOnly;
    }
}

