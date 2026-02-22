/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.security.config.Customizer
 *  org.springframework.security.config.annotation.web.HttpSecurityBuilder
 *  org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer
 */
package com.kuma.boot.security.spring.authentication.login.form;

import com.kuma.boot.security.spring.authentication.login.form.qrcode.FormQrcodeLoginHttpConfigurer;
import com.kuma.boot.security.spring.authentication.login.form.sms.FormSmsLoginHttpConfigurer;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.HttpSecurityBuilder;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;

public class FormLoginFilterSecurityConfigurer<H extends HttpSecurityBuilder<H>>
extends AbstractHttpConfigurer<FormLoginFilterSecurityConfigurer<H>, H> {
    private FormSmsLoginHttpConfigurer<H> formSmsLoginHttpConfigurer;
    private FormQrcodeLoginHttpConfigurer<H> formQrcodeLoginHttpConfigurer;

    private FormSmsLoginHttpConfigurer<H> lazyFormSmsLoginConfigurer() {
        if (this.formSmsLoginHttpConfigurer == null) {
            this.formSmsLoginHttpConfigurer = new FormSmsLoginHttpConfigurer();
        }
        return this.formSmsLoginHttpConfigurer;
    }

    public FormLoginFilterSecurityConfigurer<H> formSmsLogin(Customizer<FormSmsLoginHttpConfigurer<H>> formSmsLoginHttpConfigurerCustomizer) {
        formSmsLoginHttpConfigurerCustomizer.customize(this.lazyFormSmsLoginConfigurer());
        return this;
    }

    private FormQrcodeLoginHttpConfigurer<H> lazyFormQrcodeLoginConfigurer() {
        if (this.formQrcodeLoginHttpConfigurer == null) {
            this.formQrcodeLoginHttpConfigurer = new FormQrcodeLoginHttpConfigurer();
        }
        return this.formQrcodeLoginHttpConfigurer;
    }

    public FormLoginFilterSecurityConfigurer<H> formQrcodeLogin(Customizer<FormQrcodeLoginHttpConfigurer<H>> formQrcodeLoginHttpConfigurerCustomizer) {
        formQrcodeLoginHttpConfigurerCustomizer.customize(this.lazyFormQrcodeLoginConfigurer());
        return this;
    }

    public void init(H builder) throws Exception {
        this.init(this.formSmsLoginHttpConfigurer, builder);
        this.init(this.formQrcodeLoginHttpConfigurer, builder);
    }

    public void configure(H builder) throws Exception {
        this.configure(this.formSmsLoginHttpConfigurer, builder);
        this.configure(this.formQrcodeLoginHttpConfigurer, builder);
    }

    private <E extends AbstractHttpConfigurer<E, H>> void init(E e, H builder) throws Exception {
        if (e != null) {
            e.init(builder);
        }
    }

    private <E extends AbstractHttpConfigurer<E, H>> void configure(E e, H builder) throws Exception {
        if (e != null) {
            e.configure(builder);
        }
    }
}

