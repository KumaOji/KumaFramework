//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.kuma.boot.cache.jetcache.stamp;

import cn.hutool.crypto.SecureUtil;
import com.alicp.jetcache.anno.CacheType;
import com.kuma.boot.cache.jetcache.exception.MaximumLimitExceededException;
import java.time.Duration;
import org.apache.commons.lang3.ObjectUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

public abstract class AbstractCountStampManager extends AbstractStampManager<String, Long> {
    private static final Logger log = LoggerFactory.getLogger(AbstractCountStampManager.class);

    public AbstractCountStampManager(String cacheName) {
        super(cacheName);
    }

    public AbstractCountStampManager(String cacheName, CacheType cacheType) {
        super(cacheName, cacheType);
    }

    public AbstractCountStampManager(String cacheName, CacheType cacheType, Duration expire) {
        super(cacheName, cacheType, expire);
    }

    public int counting(String identity, int maxTimes) throws MaximumLimitExceededException {
        return this.counting(identity, maxTimes, (Duration)null);
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
        Assert.notNull(identity, "identity cannot be null");
        String key = useMd5 ? SecureUtil.md5(identity) : identity;
        String expireKey = key + "_expire";
        Long index = (Long)this.get(key);
        if (ObjectUtils.isEmpty(index)) {
            index = 0L;
        }

        if (index == 0L) {
            if (ObjectUtils.isNotEmpty(expire) && !expire.isZero()) {
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

        int times = Long.valueOf(index + 1L).intValue();
        log.debug("[kmc] |- {} has been recorded [{}] times.", function, times);
        return times;
    }

    private Duration calculateRemainingTime(Duration configuredDuration, String expireKey, String function) {
        Long begin = (Long)this.get(expireKey);
        Long current = System.currentTimeMillis();
        long interval = current - begin;
        log.debug("[kmc] |- {} operation interval [{}] millis.", function, interval);
        Duration duration;
        if (!configuredDuration.isZero()) {
            duration = configuredDuration.minusMillis(interval);
        } else {
            duration = this.getExpire().minusMillis(interval);
        }

        return duration;
    }
}
