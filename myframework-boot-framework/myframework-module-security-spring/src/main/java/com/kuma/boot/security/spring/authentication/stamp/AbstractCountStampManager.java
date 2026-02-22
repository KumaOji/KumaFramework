/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.kuma.boot.cache.redis.repository.RedisRepository
 *  com.kuma.boot.captcha.support.core.definition.AbstractRenderer
 *  org.apache.commons.lang3.ObjectUtils
 *  org.dromara.hutool.crypto.SecureUtil
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.util.Assert
 */
package com.kuma.boot.security.spring.authentication.stamp;

import com.kuma.boot.cache.redis.repository.RedisRepository;
import com.kuma.boot.captcha.support.core.definition.AbstractRenderer;
import com.kuma.boot.security.spring.exception.MaximumLimitExceededException;
import java.time.Duration;
import org.apache.commons.lang3.ObjectUtils;
import org.dromara.hutool.crypto.SecureUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

public abstract class AbstractCountStampManager
extends AbstractRenderer {
    private static final Logger log = LoggerFactory.getLogger(AbstractCountStampManager.class);

    public AbstractCountStampManager(RedisRepository redisRepository, String cacheName) {
        super(redisRepository, cacheName);
    }

    public AbstractCountStampManager(RedisRepository redisRepository, String cacheName, Duration expire) {
        super(redisRepository, cacheName, expire);
    }

    public int counting(String identity, int maxTimes) throws MaximumLimitExceededException {
        return this.counting(identity, maxTimes, null);
    }

    public int counting(String identity, int maxTimes, Duration expire) throws MaximumLimitExceededException {
        return this.counting(identity, maxTimes, expire, false);
    }

    public int counting(String identity, int maxTimes, Duration expire, String function) throws MaximumLimitExceededException {
        return this.counting(identity, maxTimes, expire, false, function);
    }

    public int counting(String identity, int maxTimes, Duration expire, boolean useMd5) throws MaximumLimitExceededException {
        return this.counting(identity, maxTimes, expire, useMd5, "AbstractCountStampManager");
    }

    public int counting(String identity, int maxTimes, Duration expire, boolean useMd5, String function) throws MaximumLimitExceededException {
        Assert.notNull((Object)identity, (String)"identity cannot be null");
        String key = useMd5 ? SecureUtil.md5((String)identity) : identity;
        String expireKey = key + "_expire";
        Long index = (Long)this.get(key);
        if (ObjectUtils.isEmpty((Object)index)) {
            index = 0L;
        }
        if (index == 0L) {
            if (ObjectUtils.isNotEmpty((Object)expire) && !expire.isZero()) {
                this.create(key, expire);
                this.put(expireKey, System.currentTimeMillis(), expire);
            } else {
                this.create(key);
                this.put(expireKey, System.currentTimeMillis());
            }
        } else {
            Duration newDuration = this.calculateRemainingTime(expire, expireKey, function);
            this.put(key, index + 1L, newDuration);
            if (index == (long)(maxTimes - 1)) {
                throw new MaximumLimitExceededException("Requests are too frequent. Please try again later!");
            }
        }
        long temp = index + 1L;
        int times = (int)temp;
        log.info("{} has been recorded [{}] times.", (Object)function, (Object)times);
        return times;
    }

    private Duration calculateRemainingTime(Duration configuredDuration, String expireKey, String function) {
        Long begin = (Long)this.get(expireKey);
        Long current = System.currentTimeMillis();
        long interval = current - begin;
        log.info("{} operation interval [{}] millis.", (Object)function, (Object)interval);
        Duration duration = !configuredDuration.isZero() ? configuredDuration.minusMillis(interval) : this.getExpire().minusMillis(interval);
        return duration;
    }
}

