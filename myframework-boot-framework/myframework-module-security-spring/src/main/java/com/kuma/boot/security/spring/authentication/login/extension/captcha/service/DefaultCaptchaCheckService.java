/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  com.kuma.boot.cache.redis.repository.RedisRepository
 *  org.springframework.beans.factory.annotation.Autowired
 */
package com.kuma.boot.security.spring.authentication.login.extension.captcha.service;

import com.kuma.boot.cache.redis.repository.RedisRepository;
import org.springframework.beans.factory.annotation.Autowired;

public class DefaultCaptchaCheckService
implements CaptchaCheckService {
    @Autowired
    private RedisRepository repository;

    @Override
    public boolean verifyCaptcha(String verificationCode) {
        return false;
    }
}

