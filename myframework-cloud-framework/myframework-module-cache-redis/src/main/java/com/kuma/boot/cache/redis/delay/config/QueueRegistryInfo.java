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

package com.kuma.boot.cache.redis.delay.config;

import com.kuma.boot.cache.redis.delay.handler.IsolationStrategy;
import com.kuma.boot.cache.redis.delay.message.MessageConverter;
import org.redisson.api.RBlockingQueue;
import org.redisson.api.RDelayedQueue;

/**
 * QueueRegistryInfo
 *
 * @author kuma
 * @version 2021.10
 * @since 2022-02-18 10:36:41
 */
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
        return queueName;
    }

    public String getIsolatedName() {
        return isolatedName;
    }

    public RedissonQueue getQueue() {
        return queue;
    }

    public IsolationStrategy getIsolationHandler() {
        return isolationHandler;
    }

    public MessageConverter getMessageConverter() {
        return messageConverter;
    }

    public RBlockingQueue<Object> getBlockingQueue() {
        return blockingQueue;
    }

    public RDelayedQueue<Object> getDelayedQueue() {
        return delayedQueue;
    }
}
