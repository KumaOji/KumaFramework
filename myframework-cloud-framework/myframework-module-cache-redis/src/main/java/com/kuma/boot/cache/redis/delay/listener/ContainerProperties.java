/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.cache.redis.delay.listener;

import com.kuma.boot.cache.redis.delay.consts.ListenerType;
import com.kuma.boot.cache.redis.delay.handler.IsolationStrategy;
import com.kuma.boot.cache.redis.delay.handler.RedissonListenerErrorHandler;
import com.kuma.boot.cache.redis.delay.message.MessageConverter;

public class ContainerProperties {
    private String queue;
    private ListenerType listenerType;
    private RedissonListenerErrorHandler errorHandler;
    private IsolationStrategy isolationStrategy;
    private MessageConverter messageConverter;
    private int concurrency;
    private int maxFetch;

    public String getQueue() {
        return this.queue;
    }

    public void setQueue(String queue) {
        this.queue = queue;
    }

    public ListenerType getListenerType() {
        return this.listenerType;
    }

    public void setListenerType(ListenerType listenerType) {
        this.listenerType = listenerType;
    }

    public RedissonListenerErrorHandler getErrorHandler() {
        return this.errorHandler;
    }

    public void setErrorHandler(RedissonListenerErrorHandler errorHandler) {
        this.errorHandler = errorHandler;
    }

    public IsolationStrategy getIsolationStrategy() {
        return this.isolationStrategy;
    }

    public void setIsolationStrategy(IsolationStrategy isolationStrategy) {
        this.isolationStrategy = isolationStrategy;
    }

    public MessageConverter getMessageConverter() {
        return this.messageConverter;
    }

    public void setMessageConverter(MessageConverter messageConverter) {
        this.messageConverter = messageConverter;
    }

    public int getConcurrency() {
        return this.concurrency;
    }

    public void setConcurrency(int concurrency) {
        this.concurrency = concurrency;
    }

    public int getMaxFetch() {
        return this.maxFetch;
    }

    public void setMaxFetch(int maxFetch) {
        this.maxFetch = maxFetch;
    }
}

