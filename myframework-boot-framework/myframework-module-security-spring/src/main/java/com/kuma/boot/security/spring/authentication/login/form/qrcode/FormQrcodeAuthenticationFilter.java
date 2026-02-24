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

package com.kuma.boot.security.spring.authentication.login.form.qrcode;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import cn.hutool.core.util.StrUtil;
import org.springframework.core.convert.converter.Converter;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.servlet.util.matcher.PathPatternRequestMatcher;
import org.springframework.util.Assert;

public class FormQrcodeAuthenticationFilter extends AbstractAuthenticationProcessingFilter {

    public static final String SPRING_SECURITY_FORM_UUID_KEY = "uuid";

    private static final PathPatternRequestMatcher DEFAULT_ANT_PATH_REQUEST_MATCHER =
            PathPatternRequestMatcher.withDefaults().matcher(HttpMethod.POST, "/form/login/qrcode");

    private String uuidParameter = SPRING_SECURITY_FORM_UUID_KEY;

    private Converter<HttpServletRequest, FormQrcodeAuthenticationToken>
            qrcodeAuthenticationTokenConverter;

    private boolean postOnly = true;

    public FormQrcodeAuthenticationFilter() {
        super(DEFAULT_ANT_PATH_REQUEST_MATCHER);
        this.qrcodeAuthenticationTokenConverter = defaultConverter();
    }

    public FormQrcodeAuthenticationFilter(AuthenticationManager authenticationManager) {
        super(DEFAULT_ANT_PATH_REQUEST_MATCHER, authenticationManager);
        this.qrcodeAuthenticationTokenConverter = defaultConverter();
    }

    @Override
    public Authentication attemptAuthentication(
            HttpServletRequest request, HttpServletResponse response)
            throws AuthenticationException {
        if (this.postOnly && !HttpMethod.POST.matches(request.getMethod())) {
            throw new AuthenticationServiceException(
                    "Authentication method not supported: " + request.getMethod());
        }

        FormQrcodeAuthenticationToken authRequest =
                qrcodeAuthenticationTokenConverter.convert(request);
        // Allow subclasses to set the "details" property
        setDetails(request, authRequest);
        return this.getAuthenticationManager().authenticate(authRequest);
    }

    private Converter<HttpServletRequest, FormQrcodeAuthenticationToken> defaultConverter() {
        return request -> {
            String username = request.getParameter(this.uuidParameter);
            username = (username != null) ? username.trim() : "";

            String authorization = request.getHeader("Authorization");
            if (StrUtil.isBlank(authorization)) {
                throw new AuthenticationCredentialsNotFoundException("");
            }

            return new FormQrcodeAuthenticationToken(username, "");
        };
    }

    protected void setDetails(
            HttpServletRequest request, FormQrcodeAuthenticationToken authRequest) {
        authRequest.setDetails(this.authenticationDetailsSource.buildDetails(request));
    }

    public void setUsernameParameter(String usernameParameter) {
        Assert.hasText(usernameParameter, "Username parameter must not be empty or null");
        // this.usernameParameter = usernameParameter;
    }

    public void setPasswordParameter(String passwordParameter) {
        Assert.hasText(passwordParameter, "Password parameter must not be empty or null");
        // this.passwordParameter = passwordParameter;
    }

    public void setConverter(
            Converter<HttpServletRequest, FormQrcodeAuthenticationToken> converter) {
        Assert.notNull(converter, "Converter must not be null");
        this.qrcodeAuthenticationTokenConverter = converter;
    }

    public void setPostOnly(boolean postOnly) {
        this.postOnly = postOnly;
    }
}
