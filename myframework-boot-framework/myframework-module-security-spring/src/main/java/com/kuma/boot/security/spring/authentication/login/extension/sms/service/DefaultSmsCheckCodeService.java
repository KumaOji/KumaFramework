/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.security.spring.authentication.login.extension.sms.service;

public class DefaultSmsCheckCodeService
implements SmsCheckCodeService {
    @Override
    public boolean verifyCaptcha(String phone, String rawCode) {
        return true;
    }
}

