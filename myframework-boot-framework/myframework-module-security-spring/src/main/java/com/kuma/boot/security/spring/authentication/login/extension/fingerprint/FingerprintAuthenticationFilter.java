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
package com.kuma.boot.security.spring.authentication.login.extension.fingerprint;

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

public class FingerprintAuthenticationFilter
extends AbstractAuthenticationProcessingFilter {
    public static final String TTC_SECURITY_EXTENSIONS_FINGERPRINT_LOGIN_NAME_URL = "name";
    public static final String TTC_SECURITY_EXTENSIONS_FINGERPRINT_LOGIN_FINGER_PRINT_URL = "fingerPrint";
    public static final String TTC_SECURITY_EXTENSIONS_FINGERPRINT_LOGIN_URL = "/login/fingerprint";
    private static final PathPatternRequestMatcher DEFAULT_ANT_PATH_REQUEST_MATCHER = PathPatternRequestMatcher.withDefaults().matcher(HttpMethod.POST, "/login/fingerprint");
    private Converter<HttpServletRequest, FingerprintAuthenticationToken> fingerprintAuthenticationTokenConverter = new FingerprintAuthenticationConverter();
    private boolean postOnly = true;

    public FingerprintAuthenticationFilter() {
        super((RequestMatcher)DEFAULT_ANT_PATH_REQUEST_MATCHER);
    }

    public FingerprintAuthenticationFilter(AuthenticationManager authenticationManager) {
        super((RequestMatcher)DEFAULT_ANT_PATH_REQUEST_MATCHER, authenticationManager);
    }

    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        if (this.postOnly && !HttpMethod.POST.matches(request.getMethod())) {
            throw new AuthenticationServiceException("Authentication method not supported: " + request.getMethod());
        }
        FingerprintAuthenticationToken authRequest = (FingerprintAuthenticationToken)((Object)this.fingerprintAuthenticationTokenConverter.convert((Object)request));
        this.setDetails(request, authRequest);
        return this.getAuthenticationManager().authenticate((Authentication)authRequest);
    }

    protected void setDetails(HttpServletRequest request, FingerprintAuthenticationToken authRequest) {
        authRequest.setDetails(this.authenticationDetailsSource.buildDetails((Object)request));
    }

    public void setConverter(Converter<HttpServletRequest, FingerprintAuthenticationToken> converter) {
        Assert.notNull(converter, (String)"Converter must not be null");
        this.fingerprintAuthenticationTokenConverter = converter;
    }

    public void setPostOnly(boolean postOnly) {
        this.postOnly = postOnly;
    }

    public Converter<HttpServletRequest, FingerprintAuthenticationToken> getFingerprintAuthenticationTokenConverter() {
        return this.fingerprintAuthenticationTokenConverter;
    }

    public void setFingerprintAuthenticationTokenConverter(Converter<HttpServletRequest, FingerprintAuthenticationToken> fingerprintAuthenticationTokenConverter) {
        this.fingerprintAuthenticationTokenConverter = fingerprintAuthenticationTokenConverter;
    }

    public boolean isPostOnly() {
        return this.postOnly;
    }

    public void setUsernameParameter(String usernameParameter) {
        Converter<HttpServletRequest, FingerprintAuthenticationToken> converter = this.fingerprintAuthenticationTokenConverter;
        if (converter instanceof FingerprintAuthenticationConverter) {
            FingerprintAuthenticationConverter fingerprintAuthenticationConverter = (FingerprintAuthenticationConverter)converter;
            fingerprintAuthenticationConverter.setUsernameParameter(usernameParameter);
        }
    }

    public void setFingerPrintParameter(String fingerPrintParameter) {
        Converter<HttpServletRequest, FingerprintAuthenticationToken> converter = this.fingerprintAuthenticationTokenConverter;
        if (converter instanceof FingerprintAuthenticationConverter) {
            FingerprintAuthenticationConverter fingerprintAuthenticationConverter = (FingerprintAuthenticationConverter)converter;
            fingerprintAuthenticationConverter.setFingerPrintParameter(fingerPrintParameter);
        }
    }
}

