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
package com.kuma.boot.security.spring.authentication.login.extension.oneClick;

import com.kuma.boot.security.spring.authentication.login.extension.oneClick.service.OneClickLoginService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.List;
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

public class OneClickLoginAuthenticationFilter
extends AbstractAuthenticationProcessingFilter {
    public static final String TTC_SECURITY_EXTENSIONS_ONE_CLICK_LOGIN_URL = "/login/oneClick";
    public static final String TTC_SECURITY_EXTENSIONS_ONE_CLICK_LOGIN_TOKEN_KEY = "accessToken";
    private static final PathPatternRequestMatcher DEFAULT_ANT_PATH_REQUEST_MATCHER = PathPatternRequestMatcher.withDefaults().matcher(HttpMethod.POST, "/login/oneClick");
    private boolean postOnly = true;
    private Converter<HttpServletRequest, OneClickLoginAuthenticationToken> oneClickLoginAuthenticationTokenConverter = new OneClickAuthenticationConverter();

    public OneClickLoginAuthenticationFilter() {
        super((RequestMatcher)DEFAULT_ANT_PATH_REQUEST_MATCHER);
    }

    public OneClickLoginAuthenticationFilter(AuthenticationManager authenticationManager) {
        super((RequestMatcher)DEFAULT_ANT_PATH_REQUEST_MATCHER, authenticationManager);
    }

    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        if (this.postOnly && !HttpMethod.POST.name().equals(request.getMethod())) {
            throw new AuthenticationServiceException("Authentication method not supported: " + request.getMethod());
        }
        OneClickLoginAuthenticationToken oneClickLoginAuthenticationToken = (OneClickLoginAuthenticationToken)((Object)this.oneClickLoginAuthenticationTokenConverter.convert((Object)request));
        if (oneClickLoginAuthenticationToken != null) {
            this.setDetails(request, oneClickLoginAuthenticationToken);
        }
        return this.getAuthenticationManager().authenticate((Authentication)oneClickLoginAuthenticationToken);
    }

    protected void setDetails(HttpServletRequest request, OneClickLoginAuthenticationToken authRequest) {
        authRequest.setDetails(this.authenticationDetailsSource.buildDetails((Object)request));
    }

    public void setConverter(Converter<HttpServletRequest, OneClickLoginAuthenticationToken> converter) {
        Assert.notNull(converter, (String)"Converter must not be null");
        this.oneClickLoginAuthenticationTokenConverter = converter;
    }

    public void setPostOnly(boolean postOnly) {
        this.postOnly = postOnly;
    }

    public boolean isPostOnly() {
        return this.postOnly;
    }

    public void setOtherParamNames(List<String> otherParamNames) {
        Converter<HttpServletRequest, OneClickLoginAuthenticationToken> converter = this.oneClickLoginAuthenticationTokenConverter;
        if (converter instanceof OneClickAuthenticationConverter) {
            OneClickAuthenticationConverter oneClickAuthenticationConverter = (OneClickAuthenticationConverter)converter;
            oneClickAuthenticationConverter.setOtherParamNames(otherParamNames);
        }
    }

    public void setTokenParamName(String tokenParamName) {
        Converter<HttpServletRequest, OneClickLoginAuthenticationToken> converter = this.oneClickLoginAuthenticationTokenConverter;
        if (converter instanceof OneClickAuthenticationConverter) {
            OneClickAuthenticationConverter oneClickAuthenticationConverter = (OneClickAuthenticationConverter)converter;
            oneClickAuthenticationConverter.setTokenParamName(tokenParamName);
        }
    }

    public void setOneClickLoginService(OneClickLoginService oneClickLoginService) {
        Converter<HttpServletRequest, OneClickLoginAuthenticationToken> converter = this.oneClickLoginAuthenticationTokenConverter;
        if (converter instanceof OneClickAuthenticationConverter) {
            OneClickAuthenticationConverter oneClickAuthenticationConverter = (OneClickAuthenticationConverter)converter;
            oneClickAuthenticationConverter.setOneClickLoginService(oneClickLoginService);
        }
    }
}

