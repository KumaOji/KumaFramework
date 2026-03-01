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

package com.kuma.boot.security.spring.authentication.login.extension.captcha;

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
import org.springframework.util.Assert;

public class CaptchaAuthenticationFilter extends AbstractAuthenticationProcessingFilter {

    public static final String KMC_SECURITY_EXTENSIONS_CAPTCHA_LOGIN_USERNAME_KEY = "username";
    public static final String KMC_SECURITY_EXTENSIONS_CAPTCHA_LOGIN_PASSWORD_KEY = "password";
    public static final String KMC_SECURITY_EXTENSIONS_CAPTCHA_LOGIN_VERIFICATION_CODE_KEY =
            "verification_code";
    public static final String KMC_SECURITY_EXTENSIONS_CAPTCHA_LOGIN_TYPE_KEY = "type";
    public static final String KMC_SECURITY_EXTENSIONS_CAPTCHA_LOGIN_URL = "/login/captcha";

    private static final PathPatternRequestMatcher DEFAULT_ANT_PATH_REQUEST_MATCHER =
            PathPatternRequestMatcher.withDefaults()
                    .matcher(HttpMethod.POST, KMC_SECURITY_EXTENSIONS_CAPTCHA_LOGIN_URL);

    private Converter<HttpServletRequest, CaptchaAuthenticationToken>
            captchaAuthenticationTokenConverter;

    private boolean postOnly = true;

    public CaptchaAuthenticationFilter() {
        super(DEFAULT_ANT_PATH_REQUEST_MATCHER);
        this.captchaAuthenticationTokenConverter = new com.kuma.boot.security.spring.authentication.login.extension.captcha.CaptchaAuthenticationConverter();
    }

    public CaptchaAuthenticationFilter(AuthenticationManager authenticationManager) {
        super(DEFAULT_ANT_PATH_REQUEST_MATCHER, authenticationManager);
        this.captchaAuthenticationTokenConverter = new com.kuma.boot.security.spring.authentication.login.extension.captcha.CaptchaAuthenticationConverter();
    }

    @Override
    public Authentication attemptAuthentication(
            HttpServletRequest request, HttpServletResponse response)
            throws AuthenticationException {
        if (this.postOnly && !HttpMethod.POST.matches(request.getMethod())) {
            throw new AuthenticationServiceException(
                    "Authentication method not supported: " + request.getMethod());
        }

        CaptchaAuthenticationToken captchaAuthenticationToken =
                captchaAuthenticationTokenConverter.convert(request);
        // Allow subclasses to set the "details" property
        if (captchaAuthenticationToken != null) {
            setDetails(request, captchaAuthenticationToken);
        }

        return this.getAuthenticationManager().authenticate(captchaAuthenticationToken);
    }

    protected void setDetails(
            HttpServletRequest request, CaptchaAuthenticationToken captchaAuthenticationToken) {
        captchaAuthenticationToken.setDetails(
                this.authenticationDetailsSource.buildDetails(request));
    }

    public void setConverter(Converter<HttpServletRequest, CaptchaAuthenticationToken> converter) {
        Assert.notNull(converter, "Converter must not be null");
        this.captchaAuthenticationTokenConverter = converter;
    }

    public void setPostOnly(boolean postOnly) {
        this.postOnly = postOnly;
    }

    public void setUsernameParameter(String usernameParameter) {
        if (this.captchaAuthenticationTokenConverter
                instanceof com.kuma.boot.security.spring.authentication.login.extension.captcha.CaptchaAuthenticationConverter captchaAuthenticationConverter) {
            captchaAuthenticationConverter.setUsernameParameter(usernameParameter);
        }
    }

    public void setPasswordParameter(String passwordParameter) {
        if (this.captchaAuthenticationTokenConverter
                instanceof com.kuma.boot.security.spring.authentication.login.extension.captcha.CaptchaAuthenticationConverter captchaAuthenticationConverter) {
            captchaAuthenticationConverter.setPasswordParameter(passwordParameter);
        }
    }

    public void setTypeParameter(String typeParameter) {
        if (this.captchaAuthenticationTokenConverter
                instanceof com.kuma.boot.security.spring.authentication.login.extension.captcha.CaptchaAuthenticationConverter captchaAuthenticationConverter) {
            captchaAuthenticationConverter.setTypeParameter(typeParameter);
        }
    }

    public void setVerificationCodeParameter(String verificationCodeParameter) {
        if (this.captchaAuthenticationTokenConverter
                instanceof com.kuma.boot.security.spring.authentication.login.extension.captcha.CaptchaAuthenticationConverter captchaAuthenticationConverter) {
            captchaAuthenticationConverter.setVerificationCodeParameter(verificationCodeParameter);
        }
    }
}
