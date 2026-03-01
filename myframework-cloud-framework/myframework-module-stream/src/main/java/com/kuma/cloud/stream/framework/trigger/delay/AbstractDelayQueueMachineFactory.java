/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  com.kuma.boot.cache.redis.repository.RedisRepository
 *  com.kuma.boot.common.utils.date.DateUtils
 *  com.kuma.boot.common.utils.log.LogUtils
 */
package com.kuma.cloud.stream.framework.trigger.delay;

import com.kuma.boot.cache.redis.repository.RedisRepository;
import com.kuma.boot.common.utils.date.DateUtils;
import com.kuma.boot.common.utils.log.LogUtils;

public abstract class AbstractDelayQueueMachineFactory
implements DelayQueueMachine {
    private final RedisRepository cache;

    protected AbstractDelayQueueMachineFactory(RedisRepository cache) {
        this.cache = cache;
    }

    @Override
    public boolean addJob(String jobId, Long triggerTime) {
        long delaySeconds = triggerTime / 1000L;
        boolean result = this.cache.zAdd(this.getDelayQueueName(), (Object)jobId, (double)delaySeconds);
        LogUtils.info((String)"\u589e\u52a0\u5ef6\u65f6\u4efb\u52a1, \u7f13\u5b58key {}, \u6267\u884c\u65f6\u95f4 {},\u4efb\u52a1id {}", (Object[])new Object[]{this.getDelayQueueName(), DateUtils.toString((Long)triggerTime), jobId});
        return result;
    }
}

