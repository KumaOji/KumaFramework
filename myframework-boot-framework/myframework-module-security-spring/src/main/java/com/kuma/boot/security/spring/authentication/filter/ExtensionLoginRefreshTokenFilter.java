/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  jakarta.servlet.FilterChain
 *  jakarta.servlet.ServletException
 *  jakarta.servlet.ServletRequest
 *  jakarta.servlet.ServletResponse
 *  jakarta.servlet.http.HttpServletRequest
 *  jakarta.servlet.http.HttpServletResponse
 *  org.springframework.http.HttpMethod
 *  org.springframework.http.HttpOutputMessage
 *  org.springframework.http.converter.HttpMessageConverter
 *  org.springframework.http.server.ServletServerHttpResponse
 *  org.springframework.security.oauth2.core.endpoint.OAuth2AccessTokenResponse
 *  org.springframework.security.oauth2.core.http.converter.OAuth2AccessTokenResponseHttpMessageConverter
 *  org.springframework.security.web.servlet.util.matcher.PathPatternRequestMatcher
 *  org.springframework.web.filter.OncePerRequestFilter
 */
package com.kuma.boot.security.spring.authentication.filter;

import com.kuma.boot.security.spring.oauth2.token.OAuth2AccessTokenStore;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.security.oauth2.core.endpoint.OAuth2AccessTokenResponse;
import org.springframework.security.oauth2.core.http.converter.OAuth2AccessTokenResponseHttpMessageConverter;
import org.springframework.security.web.servlet.util.matcher.PathPatternRequestMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

public class ExtensionLoginRefreshTokenFilter
extends OncePerRequestFilter {
    private OAuth2AccessTokenStore oAuth2AccessTokenStore;
    private final HttpMessageConverter<OAuth2AccessTokenResponse> accessTokenHttpResponseConverter = new OAuth2AccessTokenResponseHttpMessageConverter();
    private static final PathPatternRequestMatcher DEFAULT_ANT_PATH_REQUEST_MATCHER = PathPatternRequestMatcher.withDefaults().matcher(HttpMethod.POST, "/login/token/refresh_token");

    public ExtensionLoginRefreshTokenFilter(OAuth2AccessTokenStore oAuth2AccessTokenStore) {
        this.oAuth2AccessTokenStore = oAuth2AccessTokenStore;
    }

    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        if (!DEFAULT_ANT_PATH_REQUEST_MATCHER.matches(request)) {
            filterChain.doFilter((ServletRequest)request, (ServletResponse)response);
            return;
        }
        String refreshToken = request.getParameter("refresh_token");
        OAuth2AccessTokenResponse accessTokenResponse = this.oAuth2AccessTokenStore.freshToken(refreshToken);
        ServletServerHttpResponse httpResponse = new ServletServerHttpResponse(response);
        this.accessTokenHttpResponseConverter.write((Object)accessTokenResponse, null, (HttpOutputMessage)httpResponse);
    }

    public OAuth2AccessTokenStore getoAuth2AccessTokenStore() {
        return this.oAuth2AccessTokenStore;
    }

    public void setoAuth2AccessTokenStore(OAuth2AccessTokenStore oAuth2AccessTokenStore) {
        this.oAuth2AccessTokenStore = oAuth2AccessTokenStore;
    }
}

