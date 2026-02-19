/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  org.redisson.api.RBlockingQueue
 *  org.redisson.api.RDelayedQueue
 */
package com.kuma.boot.cache.redis.delay.config;

import com.kuma.boot.cache.redis.delay.handler.IsolationStrategy;
import com.kuma.boot.cache.redis.delay.message.MessageConverter;
import org.redisson.api.RBlockingQueue;
import org.redisson.api.RDelayedQueue;

public class QueueRegistryInfo {
    private String queueName;
    private String isolatedName;
    private RedissonQueue queue;
    private IsolationStrategy isolationHandler;
    private MessageConverter messageConverter;
    private RBlockingQueue<Object> blockingQueue;
    private RDelayedQueue<Object> delayedQueue;

    protected void setQueueName(String queueName) {
        this.queueName = queueName;
    }

    protected void setIsolatedName(String isolatedName) {
        this.isolatedName = isolatedName;
    }

    protected void setQueue(RedissonQueue queue) {
        this.queue = queue;
    }

    protected void setIsolationHandler(IsolationStrategy isolationHandler) {
        this.isolationHandler = isolationHandler;
    }

    protected void setMessageConverter(MessageConverter messageConverter) {
        this.messageConverter = messageConverter;
    }

    protected void setBlockingQueue(RBlockingQueue<Object> blockingQueue) {
        this.blockingQueue = blockingQueue;
    }

    protected void setDelayedQueue(RDelayedQueue<Object> delayedQueue) {
        this.delayedQueue = delayedQueue;
    }

    public String getQueueName() {
        return this.queueName;
    }

    public String getIsolatedName() {
        return this.isolatedName;
    }

    public RedissonQueue getQueue() {
        return this.queue;
    }

    public IsolationStrategy getIsolationHandler() {
        return this.isolationHandler;
    }

    public MessageConverter getMessageConverter() {
        return this.messageConverter;
    }

    public RBlockingQueue<Object> getBlockingQueue() {
        return this.blockingQueue;
    }

    public RDelayedQueue<Object> getDelayedQueue() {
        return this.delayedQueue;
    }
}

