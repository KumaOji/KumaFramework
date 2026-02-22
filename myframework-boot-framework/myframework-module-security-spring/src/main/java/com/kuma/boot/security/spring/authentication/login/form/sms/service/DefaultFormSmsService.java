/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.security.spring.authentication.login.form.sms.service;

public class DefaultFormSmsService
implements FormSmsService {
    @Override
    public boolean verifyCaptcha(String phone, String rawCode) {
        return true;
    }
}

