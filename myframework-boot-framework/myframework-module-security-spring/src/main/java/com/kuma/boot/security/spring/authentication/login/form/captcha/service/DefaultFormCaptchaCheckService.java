/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.security.spring.authentication.login.form.captcha.service;

public class DefaultFormCaptchaCheckService
implements FormCaptchaCheckService {
    @Override
    public boolean verifyCaptcha(String verificationCode) {
        return false;
    }
}

