//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.kuma.boot.captcha.service.impl;

import com.kuma.boot.cache.redis.repository.RedisRepository;
import com.kuma.boot.captcha.model.CaptchaException;
import com.kuma.boot.captcha.service.CaptchaCacheService;
import com.kuma.boot.common.utils.context.ContextUtils;
import com.kuma.boot.common.utils.lang.StringUtils;
import java.util.Objects;

public class CaptchaCacheServiceRedisImpl implements CaptchaCacheService {
    private final RedisRepository redisRepository;

    public CaptchaCacheServiceRedisImpl() {
        RedisRepository bean = (RedisRepository)ContextUtils.getBean(RedisRepository.class, true);
        if (Objects.isNull(bean)) {
            throw new CaptchaException("redis 未初始化");
        } else {
            this.redisRepository = bean;
        }
    }

    public void set(String key, String value, long expiresInSeconds) {
        this.redisRepository.set(key, value, expiresInSeconds);
    }

    public boolean exists(String key) {
        return this.redisRepository.exists(key);
    }

    public void delete(String key) {
        this.redisRepository.del(new String[]{key});
    }

    public String get(String key) {
        Object o = this.redisRepository.get(key);
        return Objects.nonNull(o) ? this.redisRepository.get(key).toString() : null;
    }

    public Long increment(String key, long val) {
        String s = this.get(key);
        if (StringUtils.isEmpty(s)) {
            return null;
        } else {
            Long ret = Long.parseLong(s) + val;
            this.set(key, "" + ret, 0L);
            return ret;
        }
    }

    public String type() {
        return "redis";
    }
}
