/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  com.kuma.boot.common.utils.log.LogUtils
 *  org.redisson.api.RBlockingQueue
 *  org.redisson.api.RDelayedQueue
 *  org.springframework.beans.BeansException
 *  org.springframework.beans.factory.BeanFactory
 *  org.springframework.beans.factory.BeanFactoryAware
 *  org.springframework.beans.factory.SmartInitializingSingleton
 *  org.springframework.util.Assert
 */
package com.kuma.boot.cache.redis.delay.config;

import com.kuma.boot.cache.redis.delay.message.DefaultRedissonMessageConverter;
import com.kuma.boot.cache.redis.delay.message.MessageConverter;
import com.kuma.boot.cache.redis.delay.message.QueueMessage;
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

public class RedissonTemplate
implements BeanFactoryAware,
SmartInitializingSingleton {
    private BeanFactory beanFactory;
    private RedissonQueueRegistry redissonQueueRegistry;
    private MessageConverter globalMessageConverter = new DefaultRedissonMessageConverter();

    public MessageConverter getGlobalMessageConverter() {
        return this.globalMessageConverter;
    }

    public void setGlobalMessageConverter(MessageConverter globalMessageConverter) {
        Assert.notNull((Object)globalMessageConverter, (String)"MessageConverter must not be null");
        this.globalMessageConverter = globalMessageConverter;
    }

    public void send(String queueName, Object payload) {
        this.send(queueName, payload, new HashMap<String, Object>(8));
    }

    public void send(String queueName, Object payload, Map<String, Object> headers) {
        try {
            this.checkQueueAndPayload(queueName, payload);
            QueueRegistryInfo registryInfo = this.checkAndGetRegistryInfo(queueName);
            RBlockingQueue<Object> blockingQueue = registryInfo.getBlockingQueue();
            MessageConverter messageConverter = this.getRequiredMessageConverter(queueName);
            this.fillInfrastructureHeaders(queueName, headers);
            QueueMessage<?> message = messageConverter.toMessage(payload, headers);
            boolean offer = blockingQueue.offer(message);
            if (offer) {
                LogUtils.info((String)"redisson send delay msg success", (Object[])new Object[0]);
            }
            LogUtils.info((String)"\u6dfb\u52a0\u961f\u5217\u6210\u529f\uff0c\u961f\u5217\u952e\uff1a{}\uff0c\u961f\u5217\u503c\uff1a{}", (Object[])new Object[]{queueName, payload});
        }
        catch (Exception e) {
            LogUtils.error((String)"\u6dfb\u52a0\u961f\u5217\u5931\u8d25\uff1a{}", (Object[])new Object[]{e.getMessage()});
            throw new RuntimeException("\u6dfb\u52a0\u961f\u5217\u5931\u8d25");
        }
    }

    public void sendWithDelay(String queueName, Object payload, long delay) {
        this.sendWithDelay(queueName, payload, new HashMap<String, Object>(8), delay);
    }

    public void sendWithDelay(String queueName, Object payload, Map<String, Object> headers, long delay) {
        try {
            this.checkQueueAndPayload(queueName, payload);
            Assert.isTrue((delay > 0L ? 1 : 0) != 0, (String)"delay millis must be positive");
            QueueRegistryInfo registryInfo = this.checkAndGetRegistryInfo(queueName);
            RDelayedQueue<Object> delayedQueue = registryInfo.getDelayedQueue();
            Assert.notNull(delayedQueue, (String)"the delay queue doesn't define");
            MessageConverter messageConverter = this.getRequiredMessageConverter(queueName);
            this.fillInfrastructureHeaders(queueName, headers);
            headers.put("expected_delay_millis", delay);
            QueueMessage<?> message = messageConverter.toMessage(payload, headers);
            delayedQueue.offer(message, delay, TimeUnit.MILLISECONDS);
            LogUtils.info((String)"\u6dfb\u52a0\u5ef6\u65f6\u961f\u5217\u6210\u529f\uff0c\u961f\u5217\u952e\uff1a{}\uff0c\u961f\u5217\u503c\uff1a{}\uff0c\u5ef6\u8fdf\u65f6\u95f4\uff1a{}", (Object[])new Object[]{queueName, payload, TimeUnit.MILLISECONDS.toSeconds(delay) + "\u79d2"});
        }
        catch (Exception e) {
            LogUtils.error((String)"\u6dfb\u52a0\u5ef6\u65f6\u961f\u5217\u5931\u8d25\uff1a{}", (Object[])new Object[]{e.getMessage()});
            throw new RuntimeException("\u6dfb\u52a0\u5ef6\u65f6\u961f\u5217\u5931\u8d25");
        }
    }

    private void checkQueueAndPayload(String queueName, Object payload) {
        Assert.hasText((String)queueName, (String)"queueName must not be empty");
        Assert.notNull((Object)payload, (String)"payload must not be null");
    }

    private QueueRegistryInfo checkAndGetRegistryInfo(String queueName) {
        QueueRegistryInfo registryInfo = this.redissonQueueRegistry.getRegistryInfo(queueName);
        Assert.notNull((Object)registryInfo, (String)"queue not registered");
        RBlockingQueue<Object> blockingQueue = registryInfo.getBlockingQueue();
        Assert.notNull(blockingQueue, (String)"target queue doesn't define");
        return registryInfo;
    }

    private MessageConverter getRequiredMessageConverter(String queueName) {
        QueueRegistryInfo registryInfo = this.redissonQueueRegistry.getRegistryInfo(queueName);
        MessageConverter messageConverter = registryInfo.getMessageConverter();
        if (messageConverter == null) {
            messageConverter = this.globalMessageConverter;
        }
        return messageConverter;
    }

    private void fillInfrastructureHeaders(String queueName, Map<String, Object> headers) {
        headers.put("delivery_queue_name", queueName);
        headers.put("send_timestamp", System.currentTimeMillis());
    }

    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
    }

    public void afterSingletonsInstantiated() {
        this.redissonQueueRegistry = (RedissonQueueRegistry)this.beanFactory.getBean("com.kuma.cloud.redis.redisson.redisson.internalRedissonQueueRegistry", RedissonQueueRegistry.class);
    }
}

