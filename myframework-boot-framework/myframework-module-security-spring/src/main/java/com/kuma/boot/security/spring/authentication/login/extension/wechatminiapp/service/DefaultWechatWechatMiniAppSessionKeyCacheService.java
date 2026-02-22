/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  com.kuma.boot.cache.redis.repository.RedisRepository
 *  org.springframework.beans.factory.annotation.Autowired
 */
package com.kuma.boot.security.spring.authentication.login.extension.wechatminiapp.service;

import com.kuma.boot.cache.redis.repository.RedisRepository;
import org.springframework.beans.factory.annotation.Autowired;

public class DefaultWechatWechatMiniAppSessionKeyCacheService
implements WechatMiniAppSessionKeyCacheService {
    @Autowired
    private RedisRepository redisRepository;

    @Override
    public String put(String cacheKey, String sessionKey) {
        this.redisRepository.set(cacheKey, (Object)sessionKey);
        return sessionKey;
    }

    @Override
    public String get(String cacheKey) {
        return (String)this.redisRepository.get(cacheKey);
    }
}

