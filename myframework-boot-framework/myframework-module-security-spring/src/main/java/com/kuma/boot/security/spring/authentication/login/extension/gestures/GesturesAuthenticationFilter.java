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
package com.kuma.boot.security.spring.authentication.login.extension.gestures;

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

public class GesturesAuthenticationFilter
extends AbstractAuthenticationProcessingFilter {
    public static final String SPRING_SECURITY_FORM_USERNAME_KEY = "username";
    public static final String SPRING_SECURITY_FORM_PASSWORD_KEY = "password";
    private static final PathPatternRequestMatcher DEFAULT_ANT_PATH_REQUEST_MATCHER = PathPatternRequestMatcher.withDefaults().matcher(HttpMethod.POST, "/login/gestures");
    private String usernameParameter = "username";
    private String passwordParameter = "password";
    private Converter<HttpServletRequest, GesturesAuthenticationToken> gesturesAuthenticationTokenConverter = this.defaultConverter();
    private boolean postOnly = true;

    public GesturesAuthenticationFilter() {
        super((RequestMatcher)DEFAULT_ANT_PATH_REQUEST_MATCHER);
    }

    public GesturesAuthenticationFilter(AuthenticationManager authenticationManager) {
        super((RequestMatcher)DEFAULT_ANT_PATH_REQUEST_MATCHER, authenticationManager);
    }

    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        if (this.postOnly && !HttpMethod.POST.matches(request.getMethod())) {
            throw new AuthenticationServiceException("Authentication method not supported: " + request.getMethod());
        }
        GesturesAuthenticationToken authRequest = (GesturesAuthenticationToken)((Object)this.gesturesAuthenticationTokenConverter.convert((Object)request));
        this.setDetails(request, authRequest);
        return this.getAuthenticationManager().authenticate((Authentication)authRequest);
    }

    private Converter<HttpServletRequest, GesturesAuthenticationToken> defaultConverter() {
        return request -> {
            String username = request.getParameter(this.usernameParameter);
            username = username != null ? username.trim() : "";
            String passord = request.getParameter(this.passwordParameter);
            passord = passord != null ? passord.trim() : "";
            return new GesturesAuthenticationToken(username, passord);
        };
    }

    protected void setDetails(HttpServletRequest request, GesturesAuthenticationToken authRequest) {
        authRequest.setDetails(this.authenticationDetailsSource.buildDetails((Object)request));
    }

    public void setUsernameParameter(String usernameParameter) {
        Assert.hasText((String)usernameParameter, (String)"Username parameter must not be empty or null");
        this.usernameParameter = usernameParameter;
    }

    public void setPasswordParameter(String passwordParameter) {
        Assert.hasText((String)passwordParameter, (String)"Password parameter must not be empty or null");
        this.passwordParameter = passwordParameter;
    }

    public void setConverter(Converter<HttpServletRequest, GesturesAuthenticationToken> converter) {
        Assert.notNull(converter, (String)"Converter must not be null");
        this.gesturesAuthenticationTokenConverter = converter;
    }

    public void setPostOnly(boolean postOnly) {
        this.postOnly = postOnly;
    }

    public final String getUsernameParameter() {
        return this.usernameParameter;
    }

    public String getPasswordParameter() {
        return this.passwordParameter;
    }
}

