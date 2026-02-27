/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  com.kuma.boot.cache.redis.repository.RedisRepository
 *  com.kuma.boot.common.utils.context.ContextUtils
 *  com.kuma.boot.common.utils.lang.StringUtils
 */
package com.kuma.boot.captcha.service.impl;

import com.kuma.boot.cache.redis.repository.RedisRepository;
import com.kuma.boot.captcha.model.CaptchaException;
import com.kuma.boot.captcha.service.CaptchaCacheService;
import com.kuma.boot.common.utils.context.ContextUtils;
import com.kuma.boot.common.utils.lang.StringUtils;
import java.util.Objects;

public class CaptchaCacheServiceRedisImpl
implements CaptchaCacheService {
    private final RedisRepository redisRepository;

    public CaptchaCacheServiceRedisImpl() {
        RedisRepository bean = (RedisRepository)ContextUtils.getBean(RedisRepository.class, (boolean)true);
        if (Objects.isNull(bean)) {
            throw new CaptchaException("redis \u672a\u521d\u59cb\u5316");
        }
        this.redisRepository = bean;
    }

    @Override
    public void set(String key, String value, long expiresInSeconds) {
        this.redisRepository.set(key, (Object)value, Long.valueOf(expiresInSeconds));
    }

    @Override
    public boolean exists(String key) {
        return this.redisRepository.exists(key);
    }

    @Override
    public void delete(String key) {
        this.redisRepository.del(new String[]{key});
    }

    @Override
    public String get(String key) {
        Object o = this.redisRepository.get(key);
        if (Objects.nonNull(o)) {
            return this.redisRepository.get(key).toString();
        }
        return null;
    }

    @Override
    public Long increment(String key, long val) {
        String s = this.get(key);
        if (StringUtils.isEmpty((String)s)) {
            return null;
        }
        Long ret = Long.parseLong(s) + val;
        this.set(key, "" + ret, 0L);
        return ret;
    }

    @Override
    public String type() {
        return "redis";
    }
}

