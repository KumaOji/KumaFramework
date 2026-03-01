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

package com.kuma.boot.security.spring.authentication.login.extension.account;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.core.convert.converter.Converter;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.servlet.util.matcher.PathPatternRequestMatcher;
import org.springframework.util.Assert;

/**
 * 帐户验证过滤器
 *
 * @author kuma
 * @version 2023.04
 * @since 2023-06-29 13:07:20
 */
public class AccountAuthenticationFilter extends AbstractAuthenticationProcessingFilter {

    public static final String KMC_SECURITY_EXTENSIONS_ACCOUNT_LOGIN_USERNAME_KEY = "username";
    public static final String KMC_SECURITY_EXTENSIONS_ACCOUNT_LOGIN_PASSWORD_KEY = "password";
    public static final String KMC_SECURITY_EXTENSIONS_ACCOUNT_LOGIN_TYPE_KEY = "type";
    public static final String KMC_SECURITY_EXTENSIONS_ACCOUNT_LOGIN_URL = "/login/account";

    private static final PathPatternRequestMatcher DEFAULT_ANT_PATH_REQUEST_MATCHER =
            PathPatternRequestMatcher.withDefaults()
                    .matcher(HttpMethod.POST, KMC_SECURITY_EXTENSIONS_ACCOUNT_LOGIN_URL);

    private Converter<HttpServletRequest, AccountAuthenticationToken>
            accountVerificationAuthenticationTokenConverter;

    private boolean postOnly = true;

    public AccountAuthenticationFilter() {
        super(DEFAULT_ANT_PATH_REQUEST_MATCHER);
        this.accountVerificationAuthenticationTokenConverter = new com.kuma.boot.security.spring.authentication.login.extension.account.AccountAuthenticationConverter();
    }

    public AccountAuthenticationFilter(AuthenticationManager authenticationManager) {
        super(DEFAULT_ANT_PATH_REQUEST_MATCHER, authenticationManager);
        this.accountVerificationAuthenticationTokenConverter = new com.kuma.boot.security.spring.authentication.login.extension.account.AccountAuthenticationConverter();
    }

    @Override
    public Authentication attemptAuthentication(
            HttpServletRequest request, HttpServletResponse response)
            throws AuthenticationException {
        if (this.postOnly && !HttpMethod.POST.matches(request.getMethod())) {
            throw new AuthenticationServiceException(
                    "Authentication method not supported: " + request.getMethod());
        }

        AccountAuthenticationToken accountAuthenticationToken =
                accountVerificationAuthenticationTokenConverter.convert(request);

        // Allow subclasses to set the "details" property
        if (accountAuthenticationToken != null) {
            setDetails(request, accountAuthenticationToken);
        }

        return this.getAuthenticationManager().authenticate(accountAuthenticationToken);
    }

    protected void setDetails(HttpServletRequest request, AccountAuthenticationToken authRequest) {
        authRequest.setDetails(this.authenticationDetailsSource.buildDetails(request));
    }

    public void setConverter(Converter<HttpServletRequest, AccountAuthenticationToken> converter) {
        Assert.notNull(converter, "Converter must not be null");
        this.accountVerificationAuthenticationTokenConverter = converter;
    }

    public void setPostOnly(boolean postOnly) {
        this.postOnly = postOnly;
    }

    public void setUsernameParameter(String usernameParameter) {
        if (this.accountVerificationAuthenticationTokenConverter
                instanceof com.kuma.boot.security.spring.authentication.login.extension.account.AccountAuthenticationConverter accountAuthenticationConverter) {
            accountAuthenticationConverter.setUsernameParameter(usernameParameter);
        }
    }

    public void setPasswordParameter(String passwordParameter) {
        if (this.accountVerificationAuthenticationTokenConverter
                instanceof com.kuma.boot.security.spring.authentication.login.extension.account.AccountAuthenticationConverter accountAuthenticationConverter) {
            accountAuthenticationConverter.setPasswordParameter(passwordParameter);
        }
    }

    public void setTypeParameter(String typeParameter) {
        if (this.accountVerificationAuthenticationTokenConverter
                instanceof com.kuma.boot.security.spring.authentication.login.extension.account.AccountAuthenticationConverter accountAuthenticationConverter) {
            accountAuthenticationConverter.setTypeParameter(typeParameter);
        }
    }

    /**
     * 重写该方法，避免在日志Debug级别会输出错误信息的问题。
     *
     * @param request  请求
     * @param response 响应
     * @param failed   失败内容
     * @throws IOException      IOException
     * @throws ServletException ServletException
     */
    @Override
    protected void unsuccessfulAuthentication(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException failed)
            throws IOException, ServletException {
        SecurityContextHolder.clearContext();
        getRememberMeServices().loginFail(request, response);
        getFailureHandler().onAuthenticationFailure(request, response, failed);
    }
}
