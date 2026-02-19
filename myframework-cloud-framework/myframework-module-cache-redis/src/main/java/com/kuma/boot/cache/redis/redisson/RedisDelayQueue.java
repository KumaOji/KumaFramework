/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  com.kuma.boot.common.utils.log.LogUtils
 *  org.redisson.api.RBlockingDeque
 *  org.redisson.api.RDelayedQueue
 *  org.redisson.api.RQueue
 *  org.redisson.api.RedissonClient
 */
package com.kuma.boot.cache.redis.redisson;

import com.kuma.boot.common.utils.log.LogUtils;
import java.util.concurrent.TimeUnit;
import org.redisson.api.RBlockingDeque;
import org.redisson.api.RDelayedQueue;
import org.redisson.api.RQueue;
import org.redisson.api.RedissonClient;

public class RedisDelayQueue {
    private final RedissonClient redissonClient;

    public RedisDelayQueue(RedissonClient redissonClient) {
        this.redissonClient = redissonClient;
    }

    public <T> void addDelayQueue(T value, long delay, TimeUnit timeUnit, String queueCode) {
        try {
            RBlockingDeque blockingDeque = this.redissonClient.getBlockingDeque(queueCode);
            RDelayedQueue delayedQueue = this.redissonClient.getDelayedQueue((RQueue)blockingDeque);
            delayedQueue.offer(value, delay, timeUnit);
            LogUtils.info((String)"\u6dfb\u52a0\u5ef6\u65f6\u961f\u5217\u6210\u529f\uff0c\u961f\u5217\u952e\uff1a{}\uff0c\u961f\u5217\u503c\uff1a{}\uff0c\u5ef6\u8fdf\u65f6\u95f4\uff1a{}", (Object[])new Object[]{queueCode, value, timeUnit.toSeconds(delay) + "\u79d2"});
        }
        catch (Exception e) {
            LogUtils.error((String)"\u6dfb\u52a0\u5ef6\u65f6\u961f\u5217\u5931\u8d25\uff1a{}", (Object[])new Object[]{e.getMessage()});
            throw new RuntimeException("\u6dfb\u52a0\u5ef6\u65f6\u961f\u5217\u5931\u8d25");
        }
    }

    public <T> T getDelayQueue(String queueCode) throws InterruptedException {
        RBlockingDeque blockingDeque = this.redissonClient.getBlockingDeque(queueCode);
        Object value = blockingDeque.take();
        return (T)value;
    }
}

