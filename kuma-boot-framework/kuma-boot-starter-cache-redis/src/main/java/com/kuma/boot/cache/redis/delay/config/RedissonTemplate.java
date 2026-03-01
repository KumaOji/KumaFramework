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

import com.kuma.boot.cache.redis.delay.message.DefaultRedissonMessageConverter;
import com.kuma.boot.cache.redis.delay.message.MessageConverter;
import com.kuma.boot.cache.redis.delay.message.QueueMessage;
import com.kuma.boot.cache.redis.delay.message.RedissonHeaders;
import com.kuma.boot.common.utils.log.LogUtils;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import org.redisson.api.RBlockingQueue;
import org.redisson.api.RDelayedQueue;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.util.Assert;

/**
 * RedissonTemplate
 *
 * @author kuma
 * @version 2021.10
 * @since 2022-02-18 10:24:41
 */
public class RedissonTemplate implements BeanFactoryAware, SmartInitializingSingleton {

    private BeanFactory beanFactory;

    private RedissonQueueRegistry redissonQueueRegistry;
    private MessageConverter globalMessageConverter = new DefaultRedissonMessageConverter();

    public MessageConverter getGlobalMessageConverter() {
        return globalMessageConverter;
    }

    public void setGlobalMessageConverter(MessageConverter globalMessageConverter) {
        Assert.notNull(globalMessageConverter, "MessageConverter must not be null");
        this.globalMessageConverter = globalMessageConverter;
    }

    public void send(final String queueName, final Object payload) {
        this.send(queueName, payload, new HashMap<>(8));
    }

    public void send(final String queueName, final Object payload, Map<String, Object> headers) {
        try {
            this.checkQueueAndPayload(queueName, payload);

            final QueueRegistryInfo registryInfo = this.checkAndGetRegistryInfo(queueName);
            final RBlockingQueue<Object> blockingQueue = registryInfo.getBlockingQueue();
            final MessageConverter messageConverter = this.getRequiredMessageConverter(queueName);

            this.fillInfrastructureHeaders(queueName, headers);
            QueueMessage<?> message = messageConverter.toMessage(payload, headers);
            boolean offer = blockingQueue.offer(message);
            if (offer) {
                LogUtils.info("redisson send delay msg success");
            }

            LogUtils.info("添加队列成功，队列键：{}，队列值：{}", queueName, payload);
        } catch (Exception e) {
            LogUtils.error("添加队列失败：{}", e.getMessage());
            throw new RuntimeException("添加队列失败");
        }
    }

    public void sendWithDelay(final String queueName, final Object payload, final long delay) {
        this.sendWithDelay(queueName, payload, new HashMap<>(8), delay);
    }

    public void sendWithDelay(
            final String queueName, final Object payload, Map<String, Object> headers, final long delay) {
        try {
            this.checkQueueAndPayload(queueName, payload);
            Assert.isTrue(delay > 0, "delay millis must be positive");

            final QueueRegistryInfo registryInfo = this.checkAndGetRegistryInfo(queueName);
            final RDelayedQueue<Object> delayedQueue = registryInfo.getDelayedQueue();
            Assert.notNull(delayedQueue, "the delay queue doesn't define");
            final MessageConverter messageConverter = this.getRequiredMessageConverter(queueName);

            this.fillInfrastructureHeaders(queueName, headers);
            headers.put(RedissonHeaders.EXPECTED_DELAY_MILLIS, delay);
            QueueMessage<?> message = messageConverter.toMessage(payload, headers);
            delayedQueue.offer(message, delay, TimeUnit.MILLISECONDS);

            LogUtils.info(
                    "添加延时队列成功，队列键：{}，队列值：{}，延迟时间：{}", queueName, payload, TimeUnit.MILLISECONDS.toSeconds(delay) + "秒");
        } catch (Exception e) {
            LogUtils.error("添加延时队列失败：{}", e.getMessage());
            throw new RuntimeException("添加延时队列失败");
        }
    }

    private void checkQueueAndPayload(String queueName, Object payload) {
        Assert.hasText(queueName, "queueName must not be empty");
        Assert.notNull(payload, "payload must not be null");
    }

    private QueueRegistryInfo checkAndGetRegistryInfo(String queueName) {
        QueueRegistryInfo registryInfo = this.redissonQueueRegistry.getRegistryInfo(queueName);
        Assert.notNull(registryInfo, "queue not registered");
        RBlockingQueue<Object> blockingQueue = registryInfo.getBlockingQueue();
        Assert.notNull(blockingQueue, "target queue doesn't define");
        return registryInfo;
    }

    private MessageConverter getRequiredMessageConverter(String queueName) {
        final QueueRegistryInfo registryInfo = this.redissonQueueRegistry.getRegistryInfo(queueName);
        MessageConverter messageConverter = registryInfo.getMessageConverter();
        if (messageConverter == null) {
            messageConverter = this.globalMessageConverter;
        }
        return messageConverter;
    }

    private void fillInfrastructureHeaders(final String queueName, final Map<String, Object> headers) {
        headers.put(RedissonHeaders.DELIVERY_QUEUE_NAME, queueName);
        headers.put(RedissonHeaders.SEND_TIMESTAMP, System.currentTimeMillis());
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
    }

    @Override
    public void afterSingletonsInstantiated() {
        this.redissonQueueRegistry = this.beanFactory.getBean(
                RedissonConfigUtils.REDISSON_QUEUE_REGISTRY_BEAN_NAME, RedissonQueueRegistry.class);
    }
}
