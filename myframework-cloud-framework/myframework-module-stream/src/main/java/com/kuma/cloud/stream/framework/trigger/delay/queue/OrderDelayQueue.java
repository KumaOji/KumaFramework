/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  com.kuma.boot.cache.redis.repository.RedisRepository
 *  org.springframework.stereotype.Component
 */
package com.kuma.cloud.stream.framework.trigger.delay.queue;

import com.kuma.boot.cache.redis.repository.RedisRepository;
import com.kuma.cloud.stream.framework.trigger.delay.AbstractDelayQueueMachineFactory;
import com.kuma.cloud.stream.framework.trigger.enums.DelayQueueEnums;
import org.springframework.stereotype.Component;

@Component
public class OrderDelayQueue
extends AbstractDelayQueueMachineFactory {
    protected OrderDelayQueue(RedisRepository cache) {
        super(cache);
    }

    @Override
    public String getDelayQueueName() {
        return DelayQueueEnums.ORDER.name();
    }
}

