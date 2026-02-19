/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  org.redisson.api.RBlockingQueue
 *  org.redisson.api.RDelayedQueue
 *  org.redisson.api.RQueue
 *  org.redisson.api.RedissonClient
 *  org.redisson.client.codec.Codec
 *  org.springframework.beans.BeansException
 *  org.springframework.beans.factory.BeanFactory
 *  org.springframework.beans.factory.BeanFactoryAware
 *  org.springframework.beans.factory.config.BeanPostProcessor
 *  org.springframework.beans.factory.support.BeanDefinitionValidationException
 */
package com.kuma.boot.cache.redis.delay.config;

import com.kuma.boot.cache.redis.delay.handler.IsolationStrategy;
import com.kuma.boot.cache.redis.delay.message.FastJsonCodec;
import org.redisson.api.RBlockingQueue;
import org.redisson.api.RDelayedQueue;
import org.redisson.api.RQueue;
import org.redisson.api.RedissonClient;
import org.redisson.client.codec.Codec;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.support.BeanDefinitionValidationException;

public class RedissonQueueBeanPostProcessor
implements BeanFactoryAware,
BeanPostProcessor {
    private BeanFactory beanFactory;
    private RedissonClient redissonClient;
    private RedissonQueueRegistry redissonQueueRegistry;

    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
        this.redissonClient = (RedissonClient)this.beanFactory.getBean(RedissonClient.class);
        this.redissonQueueRegistry = (RedissonQueueRegistry)this.beanFactory.getBean("com.kuma.cloud.redis.redisson.redisson.internalRedissonQueueRegistry", RedissonQueueRegistry.class);
    }

    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        if (bean instanceof RedissonQueue) {
            RedissonQueue redissonQueue = (RedissonQueue)bean;
            QueueRegistryInfo registryInfo = new QueueRegistryInfo();
            String queueName = redissonQueue.getQueue();
            QueueRegistryInfo registeredInfo = this.redissonQueueRegistry.getRegistryInfo(queueName);
            if (registeredInfo != null) {
                throw new BeanDefinitionValidationException("duplicate bean of RedissonQueue named [" + queueName + "]");
            }
            IsolationStrategy isolationHandler = redissonQueue.getIsolationHandler();
            String isolatedName = isolationHandler == null ? queueName : isolationHandler.getRedisQueueName(queueName);
            RBlockingQueue blockingQueue = this.redissonClient.getBlockingQueue(isolatedName, (Codec)FastJsonCodec.INSTANCE);
            RDelayedQueue delayedQueue = null;
            if (redissonQueue.getDelay()) {
                delayedQueue = this.redissonClient.getDelayedQueue((RQueue)blockingQueue);
            }
            registryInfo.setQueueName(queueName);
            registryInfo.setIsolatedName(isolatedName);
            registryInfo.setQueue(redissonQueue);
            registryInfo.setIsolationHandler(isolationHandler);
            registryInfo.setMessageConverter(redissonQueue.getMessageConverter());
            registryInfo.setBlockingQueue((RBlockingQueue<Object>)blockingQueue);
            registryInfo.setDelayedQueue((RDelayedQueue<Object>)delayedQueue);
            this.redissonQueueRegistry.registerQueueInfo(queueName, registryInfo);
        }
        return bean;
    }
}

