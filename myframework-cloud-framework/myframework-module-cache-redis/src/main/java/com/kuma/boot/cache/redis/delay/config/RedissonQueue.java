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
import org.springframework.util.Assert;

import java.util.Objects;

/**
 * RedissonQueue
 *
 * @author kuma
 * @version 2021.10
 * @since 2022-02-18 10:36:42
 */
public class RedissonQueue {

    private final String queue;

    public String getQueue() {
        return queue;
    }

    private final boolean delay;

    public boolean getDelay() {
        return delay;
    }

    private final IsolationStrategy isolationHandler;

    public IsolationStrategy getIsolationHandler() {
        return isolationHandler;
    }

    private final MessageConverter messageConverter;

    public MessageConverter getMessageConverter() {
        return messageConverter;
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

    public RedissonQueue(
            String queue, boolean delay, IsolationStrategy isolationHandler, MessageConverter messageConverter) {
        Assert.hasText(queue, "queue name must not be empty");
        this.queue = queue;
        this.delay = delay;
        this.isolationHandler = isolationHandler;
        this.messageConverter = messageConverter;
    }

    @Override
    public int hashCode() {
        return this.queue.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        RedissonQueue that = (RedissonQueue) o;
        return Objects.equals(this.queue, that.queue);
    }
}
