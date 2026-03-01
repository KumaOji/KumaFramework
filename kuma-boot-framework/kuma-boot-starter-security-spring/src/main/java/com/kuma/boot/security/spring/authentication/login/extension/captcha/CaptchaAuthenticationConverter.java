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

import com.kuma.boot.security.spring.utils.ExtensionLoginUtils;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.core.convert.converter.Converter;
import org.springframework.util.Assert;
import org.springframework.util.MultiValueMap;

/**
 * 帐户验证转换器
 *
 * @author kuma
 * @version 2023.04
 * @since 2023-06-29 13:07:23
 */
public class CaptchaAuthenticationConverter
        implements Converter<HttpServletRequest, CaptchaAuthenticationToken> {

    private String usernameParameter =
            CaptchaAuthenticationFilter.KMC_SECURITY_EXTENSIONS_CAPTCHA_LOGIN_USERNAME_KEY;
    private String passwordParameter =
            CaptchaAuthenticationFilter.KMC_SECURITY_EXTENSIONS_CAPTCHA_LOGIN_PASSWORD_KEY;
    private String verificationCodeParameter =
            CaptchaAuthenticationFilter.KMC_SECURITY_EXTENSIONS_CAPTCHA_LOGIN_VERIFICATION_CODE_KEY;
    private String typeParameter =
            CaptchaAuthenticationFilter.KMC_SECURITY_EXTENSIONS_CAPTCHA_LOGIN_TYPE_KEY;

    @Override
    public CaptchaAuthenticationToken convert(HttpServletRequest request) {
        MultiValueMap<String, String> parameters = ExtensionLoginUtils.getParameters(request);

        // username (REQUIRED)
        ExtensionLoginUtils.checkRequiredParameter(parameters, usernameParameter);
        // password (REQUIRED)
        ExtensionLoginUtils.checkRequiredParameter(parameters, passwordParameter);
        // type (REQUIRED)
        ExtensionLoginUtils.checkRequiredParameter(parameters, verificationCodeParameter);
        // type (REQUIRED)
        ExtensionLoginUtils.checkRequiredParameter(parameters, typeParameter);

        String username = request.getParameter(this.usernameParameter);
        String password = request.getParameter(this.passwordParameter);
        String verificationCode = request.getParameter(this.verificationCodeParameter);
        String type = request.getParameter(this.typeParameter);

        return CaptchaAuthenticationToken.unauthenticated(
                username, password, verificationCode, type);
    }

    public void setUsernameParameter(String usernameParameter) {
        Assert.hasText(usernameParameter, "Username parameter must not be empty or null");
        this.usernameParameter = usernameParameter;
    }

    public void setPasswordParameter(String passwordParameter) {
        Assert.hasText(passwordParameter, "Password parameter must not be empty or null");
        this.passwordParameter = passwordParameter;
    }

    public void setVerificationCodeParameter(String verificationCodeParameter) {
        Assert.hasText(
                verificationCodeParameter, "verificationCode parameter must not be empty or null");
        this.verificationCodeParameter = verificationCodeParameter;
    }

    public String getUsernameParameter() {
        return usernameParameter;
    }

    public String getPasswordParameter() {
        return passwordParameter;
    }

    public String getVerificationCodeParameter() {
        return verificationCodeParameter;
    }

    public String getTypeParameter() {
        return typeParameter;
    }

    public void setTypeParameter(String typeParameter) {
        this.typeParameter = typeParameter;
    }
}
