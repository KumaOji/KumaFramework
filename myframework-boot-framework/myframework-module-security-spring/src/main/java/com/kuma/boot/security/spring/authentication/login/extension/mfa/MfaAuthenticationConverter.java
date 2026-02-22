/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  jakarta.servlet.http.HttpServletRequest
 *  org.springframework.http.HttpMethod
 *  org.springframework.lang.Nullable
 *  org.springframework.security.core.Authentication
 *  org.springframework.security.core.context.SecurityContextHolder
 *  org.springframework.security.web.authentication.AuthenticationConverter
 *  org.springframework.security.web.servlet.util.matcher.PathPatternRequestMatcher
 *  org.springframework.security.web.util.matcher.RequestMatcher
 *  org.springframework.util.StringUtils
 */
package com.kuma.boot.security.spring.authentication.login.extension.mfa;

import com.kuma.boot.security.spring.authentication.login.extension.mfa.authentication.MfaAuthenticationToken;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpMethod;
import org.springframework.lang.Nullable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.AuthenticationConverter;
import org.springframework.security.web.servlet.util.matcher.PathPatternRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.util.StringUtils;

public class MfaAuthenticationConverter
implements AuthenticationConverter {
    public static final String SPRING_SECURITY_MFA_PARAM_NAME = "code";
    private RequestMatcher requestMatcher = MfaAuthenticationConverter.createLoginRequestMatcher();

    @Nullable
    public Authentication convert(HttpServletRequest request) {
        String username = null;
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            username = authentication.getName();
            authentication.setAuthenticated(false);
        } else if (this.requestMatcher.matches(request)) {
            username = request.getParameter("username");
        }
        if (!StringUtils.hasText((String)username)) {
            return null;
        }
        String secret = this.obtainSecret(request);
        if (StringUtils.hasText((String)secret)) {
            if (authentication != null) {
                authentication.setAuthenticated(true);
            }
            return new MfaAuthenticationToken(username, secret);
        }
        return null;
    }

    @Nullable
    protected String obtainSecret(HttpServletRequest request) {
        return request.getParameter(SPRING_SECURITY_MFA_PARAM_NAME);
    }

    private static RequestMatcher createLoginRequestMatcher() {
        return PathPatternRequestMatcher.withDefaults().matcher(HttpMethod.POST, "/login");
    }

    public void setRequestMatcher(RequestMatcher requestMatcher) {
        this.requestMatcher = requestMatcher;
    }
}

