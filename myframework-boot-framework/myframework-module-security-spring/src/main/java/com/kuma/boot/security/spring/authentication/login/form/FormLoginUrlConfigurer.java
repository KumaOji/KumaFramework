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
import com.kuma.boot.common.utils.lang.StringUtils;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.FormLoginConfigurer;

/**
 * <p>Form Login 配置统一配置类 </p>
 *
 * @author kuma
 * @version 2023.04
 * @since 2023-06-29 16:38:41
 */
public class FormLoginUrlConfigurer {

    private final OAuth2AuthenticationProperties authenticationProperties;

    public FormLoginUrlConfigurer(OAuth2AuthenticationProperties authenticationProperties) {
        this.authenticationProperties = authenticationProperties;
    }

    public FormLoginConfigurer<HttpSecurity> from(FormLoginConfigurer<HttpSecurity> configurer) {
        configurer
                .loginPage(getFormLogin().getLoginPageUrl())
                .usernameParameter(getFormLogin().getUsernameParameter())
                .passwordParameter(getFormLogin().getPasswordParameter());

        if (StringUtils.isNotBlank(getFormLogin().getFailureForwardUrl())) {
            configurer.failureForwardUrl(getFormLogin().getFailureForwardUrl());
        }

        if (StringUtils.isNotBlank(getFormLogin().getSuccessForwardUrl())) {
            configurer.successForwardUrl(getFormLogin().getSuccessForwardUrl());
        }

        return configurer;
    }

    private OAuth2AuthenticationProperties.FormLogin getFormLogin() {
        return authenticationProperties.getFormLogin();
    }
}
