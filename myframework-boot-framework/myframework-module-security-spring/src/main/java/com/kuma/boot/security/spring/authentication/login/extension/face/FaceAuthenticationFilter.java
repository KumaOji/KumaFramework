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
package com.kuma.boot.security.spring.authentication.login.extension.face;

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

public class FaceAuthenticationFilter
extends AbstractAuthenticationProcessingFilter {
    public static final String TTC_SECURITY_EXTENSIONS_FACE_IMG_BASE64_PARAMETER_KEY = "imgBase64";
    public static final String TTC_SECURITY_EXTENSIONS_FACE_LOGIN_URL = "/login/face";
    private static final PathPatternRequestMatcher DEFAULT_ANT_PATH_REQUEST_MATCHER = PathPatternRequestMatcher.withDefaults().matcher(HttpMethod.POST, "/login/face");
    private Converter<HttpServletRequest, FaceAuthenticationToken> faceAuthenticationTokenConverter = new FaceAuthenticationConverter();
    private boolean postOnly = true;

    public FaceAuthenticationFilter() {
        super((RequestMatcher)DEFAULT_ANT_PATH_REQUEST_MATCHER);
    }

    public FaceAuthenticationFilter(AuthenticationManager authenticationManager) {
        super((RequestMatcher)DEFAULT_ANT_PATH_REQUEST_MATCHER, authenticationManager);
    }

    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        if (this.postOnly && !HttpMethod.POST.matches(request.getMethod())) {
            throw new AuthenticationServiceException("Authentication method not supported: " + request.getMethod());
        }
        FaceAuthenticationToken faceAuthenticationToken = (FaceAuthenticationToken)((Object)this.faceAuthenticationTokenConverter.convert((Object)request));
        if (faceAuthenticationToken != null) {
            this.setDetails(request, faceAuthenticationToken);
        }
        return this.getAuthenticationManager().authenticate((Authentication)faceAuthenticationToken);
    }

    protected void setDetails(HttpServletRequest request, FaceAuthenticationToken authRequest) {
        authRequest.setDetails(this.authenticationDetailsSource.buildDetails((Object)request));
    }

    public void setConverter(Converter<HttpServletRequest, FaceAuthenticationToken> converter) {
        Assert.notNull(converter, (String)"Converter must not be null");
        this.faceAuthenticationTokenConverter = converter;
    }

    public void setPostOnly(boolean postOnly) {
        this.postOnly = postOnly;
    }

    public void setImgBase64Parameter(String imgBase64Parameter) {
        Converter<HttpServletRequest, FaceAuthenticationToken> converter = this.faceAuthenticationTokenConverter;
        if (converter instanceof FaceAuthenticationConverter) {
            FaceAuthenticationConverter faceAuthenticationConverter = (FaceAuthenticationConverter)converter;
            faceAuthenticationConverter.setImgBase64Parameter(imgBase64Parameter);
        }
    }
}

