/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  com.fasterxml.jackson.databind.ObjectMapper
 *  jakarta.servlet.ServletException
 *  jakarta.servlet.http.HttpServletRequest
 *  jakarta.servlet.http.HttpServletResponse
 *  org.springframework.core.convert.converter.Converter
 *  org.springframework.http.HttpMethod
 *  org.springframework.security.authentication.AuthenticationManager
 *  org.springframework.security.authentication.AuthenticationServiceException
 *  org.springframework.security.authentication.BadCredentialsException
 *  org.springframework.security.core.Authentication
 *  org.springframework.security.core.AuthenticationException
 *  org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter
 *  org.springframework.security.web.servlet.util.matcher.PathPatternRequestMatcher
 *  org.springframework.security.web.util.matcher.RequestMatcher
 *  org.springframework.util.Assert
 */
package com.kuma.boot.security.spring.authentication.login.extension.wechatminiapp;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kuma.boot.security.spring.authentication.login.extension.wechatminiapp.client.WechatMiniAppRequest;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import org.springframework.core.convert.converter.Converter;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.servlet.util.matcher.PathPatternRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.util.Assert;

public class WechatMiniAppAuthenticationFilter
extends AbstractAuthenticationProcessingFilter {
    private static final PathPatternRequestMatcher DEFAULT_ANT_PATH_REQUEST_MATCHER = PathPatternRequestMatcher.withDefaults().matcher(HttpMethod.POST, "/login/wechat/miniapp");
    private final ObjectMapper om = new ObjectMapper();
    private Converter<HttpServletRequest, WechatMiniAppAuthenticationToken> miniAppAuthenticationTokenConverter = this.defaultConverter();
    private boolean postOnly = true;

    public WechatMiniAppAuthenticationFilter() {
        super((RequestMatcher)DEFAULT_ANT_PATH_REQUEST_MATCHER);
    }

    public WechatMiniAppAuthenticationFilter(AuthenticationManager authenticationManager) {
        super((RequestMatcher)DEFAULT_ANT_PATH_REQUEST_MATCHER, authenticationManager);
    }

    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException, IOException, ServletException {
        if (this.postOnly && !HttpMethod.POST.matches(request.getMethod())) {
            throw new AuthenticationServiceException("Authentication method not supported: " + request.getMethod());
        }
        WechatMiniAppAuthenticationToken authRequest = (WechatMiniAppAuthenticationToken)((Object)this.miniAppAuthenticationTokenConverter.convert((Object)request));
        if (authRequest == null) {
            throw new BadCredentialsException("fail to extract miniapp authentication request params");
        }
        this.setDetails(request, authRequest);
        return this.getAuthenticationManager().authenticate((Authentication)authRequest);
    }

    protected void setDetails(HttpServletRequest request, WechatMiniAppAuthenticationToken authRequest) {
        authRequest.setDetails(this.authenticationDetailsSource.buildDetails((Object)request));
    }

    public void setConverter(Converter<HttpServletRequest, WechatMiniAppAuthenticationToken> converter) {
        Assert.notNull(converter, (String)"Converter must not be null");
        this.miniAppAuthenticationTokenConverter = converter;
    }

    public void setPostOnly(boolean postOnly) {
        this.postOnly = postOnly;
    }

    private Converter<HttpServletRequest, WechatMiniAppAuthenticationToken> defaultConverter() {
        return request -> {
            WechatMiniAppAuthenticationToken wechatMiniAppAuthenticationToken;
            block8: {
                BufferedReader reader = request.getReader();
                try {
                    WechatMiniAppRequest wechatMiniAppRequest = (WechatMiniAppRequest)this.om.readValue((Reader)reader, WechatMiniAppRequest.class);
                    wechatMiniAppAuthenticationToken = new WechatMiniAppAuthenticationToken(wechatMiniAppRequest);
                    if (reader == null) break block8;
                }
                catch (Throwable throwable) {
                    try {
                        if (reader != null) {
                            try {
                                reader.close();
                            }
                            catch (Throwable throwable2) {
                                throwable.addSuppressed(throwable2);
                            }
                        }
                        throw throwable;
                    }
                    catch (IOException e) {
                        return null;
                    }
                }
                reader.close();
            }
            return wechatMiniAppAuthenticationToken;
        };
    }
}

