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

import com.kuma.boot.security.spring.authentication.login.form.qrcode.FormQrcodeLoginHttpConfigurer;
import com.kuma.boot.security.spring.authentication.login.form.sms.FormSmsLoginHttpConfigurer;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.SecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.HttpSecurityBuilder;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;

/**
 * 基于spring security自定义扩展登录方式(基于json请求)
 *
 * @author kuma
 * @version 2023.07
 * @see SecurityConfigurerAdapter
 * @since 2023-07-10 17:42:42
 */
public class FormLoginFilterSecurityConfigurer<H extends HttpSecurityBuilder<H>>
        extends AbstractHttpConfigurer<FormLoginFilterSecurityConfigurer<H>, H> {

    private FormSmsLoginHttpConfigurer<H> formSmsLoginHttpConfigurer;
    private FormQrcodeLoginHttpConfigurer<H> formQrcodeLoginHttpConfigurer;

    private FormSmsLoginHttpConfigurer<H> lazyFormSmsLoginConfigurer() {
        if (formSmsLoginHttpConfigurer == null) {
            this.formSmsLoginHttpConfigurer = new FormSmsLoginHttpConfigurer<>();
        }
        return formSmsLoginHttpConfigurer;
    }

    public FormLoginFilterSecurityConfigurer<H> formSmsLogin(
            Customizer<FormSmsLoginHttpConfigurer<H>> formSmsLoginHttpConfigurerCustomizer) {
        formSmsLoginHttpConfigurerCustomizer.customize(lazyFormSmsLoginConfigurer());
        return this;
    }

    private FormQrcodeLoginHttpConfigurer<H> lazyFormQrcodeLoginConfigurer() {
        if (formQrcodeLoginHttpConfigurer == null) {
            this.formQrcodeLoginHttpConfigurer = new FormQrcodeLoginHttpConfigurer<>();
        }
        return formQrcodeLoginHttpConfigurer;
    }

    public FormLoginFilterSecurityConfigurer<H> formQrcodeLogin(
            Customizer<FormQrcodeLoginHttpConfigurer<H>> formQrcodeLoginHttpConfigurerCustomizer) {
        formQrcodeLoginHttpConfigurerCustomizer.customize(lazyFormQrcodeLoginConfigurer());
        return this;
    }

    @Override
    public void init(H builder) {
        init(formSmsLoginHttpConfigurer, builder);
        init(formQrcodeLoginHttpConfigurer, builder);
    }

    @Override
    public void configure(H builder) {
        configure(formSmsLoginHttpConfigurer, builder);
        configure(formQrcodeLoginHttpConfigurer, builder);
    }

    private <E extends AbstractHttpConfigurer<E, H>> void init(E e, H builder){
        if (e != null) {
            e.init(builder);
        }
    }

    private <E extends AbstractHttpConfigurer<E, H>> void configure(E e, H builder) {
        if (e != null) {
            e.configure(builder);
        }
    }
}
