/*
 * Copyright (c) 2020-2030, Kuma (2569277704@qq.com & https://blog.kumacloud.top/).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
import org.springframework.util.Assert;

/**
 * 一键登录过滤器
 *
 * @author kuma
 * @version 2023.04
 * @since 2023-06-16 13:51:44
 */
public class OneClickLoginAuthenticationFilter extends AbstractAuthenticationProcessingFilter {

    /**
     * 默认的一键登录请求处理url
     */
    public static final String KMC_SECURITY_EXTENSIONS_ONE_CLICK_LOGIN_URL = "/login/oneClick";

    public static final String KMC_SECURITY_EXTENSIONS_ONE_CLICK_LOGIN_TOKEN_KEY = "accessToken";

    private static final PathPatternRequestMatcher DEFAULT_ANT_PATH_REQUEST_MATCHER =
            PathPatternRequestMatcher.withDefaults()
                    .matcher(HttpMethod.POST, KMC_SECURITY_EXTENSIONS_ONE_CLICK_LOGIN_URL);

    private boolean postOnly = true;
    private Converter<HttpServletRequest, OneClickLoginAuthenticationToken>
            oneClickLoginAuthenticationTokenConverter;

    public OneClickLoginAuthenticationFilter() {
        super(DEFAULT_ANT_PATH_REQUEST_MATCHER);
        this.oneClickLoginAuthenticationTokenConverter = new com.kuma.boot.security.spring.authentication.login.extension.oneClick.OneClickAuthenticationConverter();
    }

    public OneClickLoginAuthenticationFilter(AuthenticationManager authenticationManager) {
        super(DEFAULT_ANT_PATH_REQUEST_MATCHER, authenticationManager);
        this.oneClickLoginAuthenticationTokenConverter = new com.kuma.boot.security.spring.authentication.login.extension.oneClick.OneClickAuthenticationConverter();
    }

    @Override
    public Authentication attemptAuthentication(
            HttpServletRequest request, HttpServletResponse response)
            throws AuthenticationException {
        if (postOnly && !HttpMethod.POST.name().equals(request.getMethod())) {
            throw new AuthenticationServiceException(
                    "Authentication method not supported: " + request.getMethod());
        }

        OneClickLoginAuthenticationToken oneClickLoginAuthenticationToken =
                oneClickLoginAuthenticationTokenConverter.convert(request);

        // Allow subclasses to set the "details" property
        if (oneClickLoginAuthenticationToken != null) {
            setDetails(request, oneClickLoginAuthenticationToken);
        }

        // 一键登录: 用户已注册则登录, 未注册则自动注册用户且返回登录状态
        return this.getAuthenticationManager().authenticate(oneClickLoginAuthenticationToken);
    }

    /**
     * Provided so that subclasses may configure what is put into the auth request's details
     * property.
     *
     * @param request     that an auth request is being created for
     * @param authRequest the auth request object that should have its details set
     */
    protected void setDetails(
            HttpServletRequest request, OneClickLoginAuthenticationToken authRequest) {
        authRequest.setDetails(authenticationDetailsSource.buildDetails(request));
    }

    public void setConverter(
            Converter<HttpServletRequest, OneClickLoginAuthenticationToken> converter) {
        Assert.notNull(converter, "Converter must not be null");
        this.oneClickLoginAuthenticationTokenConverter = converter;
    }

    /**
     * Defines whether only HTTP POST requests will be allowed by this filter. If set to true, and
     * an auth request is received which is not a POST request, an exception will be raised
     * immediately and auth will not be attempted. The
     * <tt>unsuccessfulAuthentication()</tt> method will be called as if handling a failed
     * auth.
     * <p>
     * Defaults to <tt>true</tt> but may be overridden by subclasses.
     */
    public void setPostOnly(boolean postOnly) {
        this.postOnly = postOnly;
    }

    public boolean isPostOnly() {
        return postOnly;
    }

    public void setOtherParamNames(List<String> otherParamNames) {
        if (this.oneClickLoginAuthenticationTokenConverter
                instanceof com.kuma.boot.security.spring.authentication.login.extension.oneClick.OneClickAuthenticationConverter oneClickAuthenticationConverter) {
            oneClickAuthenticationConverter.setOtherParamNames(otherParamNames);
        }
    }

    public void setTokenParamName(String tokenParamName) {
        if (this.oneClickLoginAuthenticationTokenConverter
                instanceof com.kuma.boot.security.spring.authentication.login.extension.oneClick.OneClickAuthenticationConverter oneClickAuthenticationConverter) {
            oneClickAuthenticationConverter.setTokenParamName(tokenParamName);
        }
    }

    public void setOneClickLoginService(OneClickLoginService oneClickLoginService) {
        if (this.oneClickLoginAuthenticationTokenConverter
                instanceof com.kuma.boot.security.spring.authentication.login.extension.oneClick.OneClickAuthenticationConverter oneClickAuthenticationConverter) {
            oneClickAuthenticationConverter.setOneClickLoginService(oneClickLoginService);
        }
    }
}
