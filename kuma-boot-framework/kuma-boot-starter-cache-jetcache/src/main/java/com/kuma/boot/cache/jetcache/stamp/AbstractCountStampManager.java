/*
 * Copyright (c) 2020-2030, Kuma (2569277704@qq.com & https://blog.kumacloud.top/).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.kuma.boot.cache.jetcache.stamp;

import com.alicp.jetcache.anno.CacheType;
import com.kuma.boot.cache.jetcache.exception.MaximumLimitExceededException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;
import org.springframework.util.DigestUtils;

import java.nio.charset.StandardCharsets;
import java.time.Duration;

/**
 * 基于计数的 Stamp 管理器抽象基类，用于频率限制场景（如验证码发送次数）。
 *
 * <p>通过 {@code counting()} 系列方法记录操作次数，达到上限时抛出 {@link MaximumLimitExceededException}。
 *
 * @author kuma
 * @since 2022-07-03
 */
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
        return counting(identity, maxTimes, null);
    }

    public int counting(String identity, int maxTimes, Duration expire) throws MaximumLimitExceededException {
        return counting(identity, maxTimes, expire, false);
    }

    public int counting(String identity, int maxTimes, Duration expire, String function) throws MaximumLimitExceededException {
        return counting(identity, maxTimes, expire, false, function);
    }

    public int counting(String identity, int maxTimes, Duration expire, boolean useMd5) throws MaximumLimitExceededException {
        return counting(identity, maxTimes, expire, useMd5, "AbstractCountStampManager");
    }

    public int counting(String identity, int maxTimes, Duration expire, boolean useMd5, String function)
            throws MaximumLimitExceededException {
        Assert.notNull(identity, "identity cannot be null");
        String key = useMd5
                ? DigestUtils.md5DigestAsHex(identity.getBytes(StandardCharsets.UTF_8))
                : identity;
        String expireKey = key + "_expire";
        Long index = get(key);
        if (index == null) {
            index = 0L;
        }
        if (index == 0L) {
            if (expire != null && !expire.isZero()) {
                create(key, expire);
                put(expireKey, System.currentTimeMillis(), expire);
            } else {
                create(key);
                put(expireKey, System.currentTimeMillis());
            }
        } else {
            Duration remaining = calculateRemainingTime(expire, expireKey, function);
            put(key, index + 1L, remaining);
            if (index == maxTimes - 1L) {
                throw new MaximumLimitExceededException("Requests are too frequent. Please try again later!");
            }
        }
        int times = (int) (index + 1L);
        log.debug("[kmc] |- {} has been recorded [{}] times.", function, times);
        return times;
    }

    private Duration calculateRemainingTime(Duration configuredDuration, String expireKey, String function) {
        Long begin = get(expireKey);
        long interval = System.currentTimeMillis() - (begin != null ? begin : 0L);
        log.debug("[kmc] |- {} operation interval [{}] millis.", function, interval);
        Duration base = (configuredDuration != null && !configuredDuration.isZero())
                ? configuredDuration
                : getExpire();
        return base.minusMillis(interval);
    }
}
