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

package com.kuma.boot.cache.redis.delay.listener;

import com.kuma.boot.cache.redis.delay.consts.ListenerType;
import com.kuma.boot.cache.redis.delay.handler.IsolationStrategy;
import com.kuma.boot.cache.redis.delay.handler.RedissonListenerErrorHandler;
import com.kuma.boot.cache.redis.delay.message.MessageConverter;

/**
 * ContainerProperties
 *
 * @author kuma
 * @version 2021.10
 * @since 2022-02-18 10:36:42
 */
public class ContainerProperties {

    private String queue;

    private ListenerType listenerType;

    private RedissonListenerErrorHandler errorHandler;

    private IsolationStrategy isolationStrategy;

    private MessageConverter messageConverter;

    private int concurrency;

    private int maxFetch;

    public String getQueue() {
        return queue;
    }

    public void setQueue(String queue) {
        this.queue = queue;
    }

    public ListenerType getListenerType() {
        return listenerType;
    }

    public void setListenerType(ListenerType listenerType) {
        this.listenerType = listenerType;
    }

    public RedissonListenerErrorHandler getErrorHandler() {
        return errorHandler;
    }

    public void setErrorHandler(RedissonListenerErrorHandler errorHandler) {
        this.errorHandler = errorHandler;
    }

    public IsolationStrategy getIsolationStrategy() {
        return isolationStrategy;
    }

    public void setIsolationStrategy(IsolationStrategy isolationStrategy) {
        this.isolationStrategy = isolationStrategy;
    }

    public MessageConverter getMessageConverter() {
        return messageConverter;
    }

    public void setMessageConverter(MessageConverter messageConverter) {
        this.messageConverter = messageConverter;
    }

    public int getConcurrency() {
        return concurrency;
    }

    public void setConcurrency(int concurrency) {
        this.concurrency = concurrency;
    }

    public int getMaxFetch() {
        return maxFetch;
    }

    public void setMaxFetch(int maxFetch) {
        this.maxFetch = maxFetch;
    }
}
