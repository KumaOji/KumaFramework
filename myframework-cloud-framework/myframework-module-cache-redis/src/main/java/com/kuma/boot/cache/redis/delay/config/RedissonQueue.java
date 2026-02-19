/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  org.springframework.util.Assert
 */
package com.kuma.boot.cache.redis.delay.config;

import com.kuma.boot.cache.redis.delay.handler.IsolationStrategy;
import com.kuma.boot.cache.redis.delay.message.MessageConverter;
import java.util.Objects;
import org.springframework.util.Assert;

public class RedissonQueue {
    private final String queue;
    private final boolean delay;
    private final IsolationStrategy isolationHandler;
    private final MessageConverter messageConverter;

    public String getQueue() {
        return this.queue;
    }

    public boolean getDelay() {
        return this.delay;
    }

    public IsolationStrategy getIsolationHandler() {
        return this.isolationHandler;
    }

    public MessageConverter getMessageConverter() {
        return this.messageConverter;
    }

    public RedissonQueue(String queue) {
        this(queue, false);
    }

    public RedissonQueue(String queue, boolean delay) {
        this(queue, delay, null);
    }

    public RedissonQueue(String queue, boolean delay, IsolationStrategy isolationHandler) {
        this(queue, delay, isolationHandler, null);
    }

    public RedissonQueue(String queue, boolean delay, IsolationStrategy isolationHandler, MessageConverter messageConverter) {
        Assert.hasText((String)queue, (String)"queue name must not be empty");
        this.queue = queue;
        this.delay = delay;
        this.isolationHandler = isolationHandler;
        this.messageConverter = messageConverter;
    }

    public int hashCode() {
        return this.queue.hashCode();
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        RedissonQueue that = (RedissonQueue)o;
        return Objects.equals(this.queue, that.queue);
    }
}

