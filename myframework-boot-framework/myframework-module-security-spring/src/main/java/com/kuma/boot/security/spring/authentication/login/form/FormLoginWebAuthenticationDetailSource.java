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

package com.kuma.boot.security.spring.authentication.login.form;

import com.kuma.boot.security.spring.autoconfigure.properties.OAuth2AuthenticationProperties;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.authentication.AuthenticationDetailsSource;

/**
 * <p>表单登录 Details 定义 </p>
 *
 * @author kuma
 * @version 2023.04
 * @since 2023-06-29 16:37:57
 */
public class FormLoginWebAuthenticationDetailSource
        implements AuthenticationDetailsSource<
        HttpServletRequest, com.kuma.boot.security.spring.authentication.login.form.FormLoginWebAuthenticationDetails> {

    private final OAuth2AuthenticationProperties authenticationProperties;

    public FormLoginWebAuthenticationDetailSource(
            OAuth2AuthenticationProperties authenticationProperties) {
        this.authenticationProperties = authenticationProperties;
    }

    @Override
    public com.kuma.boot.security.spring.authentication.login.form.FormLoginWebAuthenticationDetails buildDetails(HttpServletRequest context) {
        return new com.kuma.boot.security.spring.authentication.login.form.FormLoginWebAuthenticationDetails(
                context,
                authenticationProperties.getFormLogin().getCloseCaptcha(),
                authenticationProperties.getFormLogin().getCaptchaParameter(),
                authenticationProperties.getFormLogin().getCategory());
    }
}
