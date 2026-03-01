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

package com.kuma.boot.security.spring.authentication.login.form.sms;

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

public class FormSmsLoginAuthenticationFilter extends AbstractAuthenticationProcessingFilter {

    public static final String SPRING_SECURITY_FORM_PHONE_KEY = "phone";

    public static final String SPRING_SECURITY_FORM_CAPTCHA_KEY = "captcha";

    public static final String SPRING_SECURITY_FORM_TYPE_KEY = "type";

    private static final PathPatternRequestMatcher DEFAULT_ANT_PATH_REQUEST_MATCHER =
            PathPatternRequestMatcher.withDefaults().matcher(HttpMethod.POST, "/form/login/phone");

    private String phoneParameter = SPRING_SECURITY_FORM_PHONE_KEY;
    private String captchaParameter = SPRING_SECURITY_FORM_CAPTCHA_KEY;
    private String typeParameter = SPRING_SECURITY_FORM_TYPE_KEY;

    private Converter<HttpServletRequest, FormSmsLoginAuthenticationToken>
            phoneAuthenticationTokenConverter;

    private boolean postOnly = true;

    public FormSmsLoginAuthenticationFilter() {
        super(DEFAULT_ANT_PATH_REQUEST_MATCHER);
        this.phoneAuthenticationTokenConverter = defaultConverter();
    }

    public FormSmsLoginAuthenticationFilter(AuthenticationManager authenticationManager) {
        super(DEFAULT_ANT_PATH_REQUEST_MATCHER, authenticationManager);
        this.phoneAuthenticationTokenConverter = defaultConverter();
    }

    @Override
    public Authentication attemptAuthentication(
            HttpServletRequest request, HttpServletResponse response)
            throws AuthenticationException {
        if (this.postOnly && !HttpMethod.POST.matches(request.getMethod())) {
            throw new AuthenticationServiceException(
                    "Authentication method not supported: " + request.getMethod());
        }

        FormSmsLoginAuthenticationToken authRequest =
                phoneAuthenticationTokenConverter.convert(request);
        // Allow subclasses to set the "details" property
        setDetails(request, authRequest);
        return this.getAuthenticationManager().authenticate(authRequest);
    }

    private Converter<HttpServletRequest, FormSmsLoginAuthenticationToken> defaultConverter() {
        return request -> {
            String phone = request.getParameter(this.phoneParameter);
            phone = (phone != null) ? phone.trim() : "";

            String captcha = request.getParameter(this.captchaParameter);
            captcha = (captcha != null) ? captcha.trim() : "";

            String type = request.getParameter(this.typeParameter);
            type = (type != null) ? type.trim() : "";

            return new FormSmsLoginAuthenticationToken(phone, captcha, type);
        };
    }

    protected void setDetails(
            HttpServletRequest request, FormSmsLoginAuthenticationToken authRequest) {
        authRequest.setDetails(this.authenticationDetailsSource.buildDetails(request));
    }

    public void setPhoneParameter(String phoneParameter) {
        Assert.hasText(phoneParameter, "phoneParameter must not be empty or null");
        this.phoneParameter = phoneParameter;
    }

    public void setCaptchaParameter(String captchaParameter) {
        Assert.hasText(captchaParameter, "Password parameter must not be empty or null");
        this.captchaParameter = captchaParameter;
    }

    public void setConverter(
            Converter<HttpServletRequest, FormSmsLoginAuthenticationToken> converter) {
        Assert.notNull(converter, "Converter must not be null");
        this.phoneAuthenticationTokenConverter = converter;
    }

    public void setPostOnly(boolean postOnly) {
        this.postOnly = postOnly;
    }

    public final String getPhoneParameter() {
        return this.phoneParameter;
    }

    public final String getCaptchaParameter() {
        return this.captchaParameter;
    }

    public String getTypeParameter() {
        return typeParameter;
    }

    public void setTypeParameter(String typeParameter) {
        this.typeParameter = typeParameter;
    }

    public Converter<HttpServletRequest, FormSmsLoginAuthenticationToken>
    getPhoneAuthenticationTokenConverter() {
        return phoneAuthenticationTokenConverter;
    }

    public void setPhoneAuthenticationTokenConverter(
            Converter<HttpServletRequest, FormSmsLoginAuthenticationToken>
                    phoneAuthenticationTokenConverter) {
        this.phoneAuthenticationTokenConverter = phoneAuthenticationTokenConverter;
    }

    public boolean isPostOnly() {
        return postOnly;
    }
}
