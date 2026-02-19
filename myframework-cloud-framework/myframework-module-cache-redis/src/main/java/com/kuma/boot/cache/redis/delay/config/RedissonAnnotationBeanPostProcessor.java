/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  com.kuma.boot.common.utils.log.LogUtils
 *  org.redisson.api.RedissonClient
 *  org.springframework.beans.BeansException
 *  org.springframework.beans.factory.BeanFactory
 *  org.springframework.beans.factory.BeanFactoryAware
 *  org.springframework.beans.factory.SmartInitializingSingleton
 *  org.springframework.beans.factory.config.BeanPostProcessor
 *  org.springframework.beans.factory.support.BeanDefinitionValidationException
 *  org.springframework.core.annotation.AnnotationUtils
 *  org.springframework.messaging.handler.annotation.support.DefaultMessageHandlerMethodFactory
 *  org.springframework.messaging.handler.annotation.support.MessageHandlerMethodFactory
 *  org.springframework.messaging.handler.invocation.InvocableHandlerMethod
 *  org.springframework.util.ReflectionUtils
 *  org.springframework.util.StringUtils
 */
package com.kuma.boot.cache.redis.delay.config;

import com.kuma.boot.cache.redis.delay.annotation.RedissonListener;
import com.kuma.boot.cache.redis.delay.consts.ListenerType;
import com.kuma.boot.cache.redis.delay.handler.IsolationStrategy;
import com.kuma.boot.cache.redis.delay.handler.RedissonListenerErrorHandler;
import com.kuma.boot.cache.redis.delay.listener.BatchRedissonMessageListenerAdapter;
import com.kuma.boot.cache.redis.delay.listener.ContainerProperties;
import com.kuma.boot.cache.redis.delay.listener.DefaultRedissonListenerContainerFactory;
import com.kuma.boot.cache.redis.delay.listener.RedissonListenerContainer;
import com.kuma.boot.cache.redis.delay.listener.RedissonListenerContainerFactory;
import com.kuma.boot.cache.redis.delay.listener.RedissonMessageListener;
import com.kuma.boot.cache.redis.delay.listener.SimpleRedissonMessageListenerAdapter;
import com.kuma.boot.cache.redis.delay.message.MessageConverter;
import com.kuma.boot.common.utils.log.LogUtils;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import org.redisson.api.RedissonClient;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.support.BeanDefinitionValidationException;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.messaging.handler.annotation.support.DefaultMessageHandlerMethodFactory;
import org.springframework.messaging.handler.annotation.support.MessageHandlerMethodFactory;
import org.springframework.messaging.handler.invocation.InvocableHandlerMethod;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;

public class RedissonAnnotationBeanPostProcessor
implements BeanPostProcessor,
BeanFactoryAware,
SmartInitializingSingleton {
    private BeanFactory beanFactory;
    private MessageHandlerMethodFactory messageHandlerMethodFactory;
    private RedissonListenerContainerFactory redissonListenerContainerFactory;
    private RedissonListenerRegistry redissonListenerRegistry;
    private RedissonClient redissonClient;
    private List<ListenerDescriptor> listenerDescriptors = new ArrayList<ListenerDescriptor>(8);

    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
        DefaultMessageHandlerMethodFactory handlerMethodFactory = new DefaultMessageHandlerMethodFactory();
        handlerMethodFactory.setBeanFactory(beanFactory);
        handlerMethodFactory.afterPropertiesSet();
        this.messageHandlerMethodFactory = handlerMethodFactory;
        this.redissonListenerContainerFactory = new DefaultRedissonListenerContainerFactory();
    }

    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        Class<?> clazz = bean.getClass();
        List<Method> allMethod = this.findAllMethod(clazz);
        allMethod.forEach(method -> this.processMethod(bean, (Method)method));
        return bean;
    }

    private List<Method> findAllMethod(Class clazz) {
        LinkedList<Method> methods = new LinkedList<Method>();
        ReflectionUtils.doWithMethods((Class)clazz, methods::add);
        return methods;
    }

    private void processMethod(Object bean, Method method) {
        String[] queues;
        RedissonListener annotation = (RedissonListener)AnnotationUtils.findAnnotation((Method)method, RedissonListener.class);
        if (annotation == null) {
            return;
        }
        int maxFetch = annotation.maxFetch();
        if (maxFetch <= 0) {
            throw new BeanDefinitionValidationException("maxFetch must be grater than 0");
        }
        for (String queue : queues = annotation.queues()) {
            InvocableHandlerMethod invocableHandlerMethod = this.messageHandlerMethodFactory.createInvocableHandlerMethod(bean, method);
            ListenerDescriptor listenerDescriptor = new ListenerDescriptor();
            listenerDescriptor.setQueueInterested(queue);
            listenerDescriptor.setListenerType(maxFetch > 1 ? ListenerType.BATCH : ListenerType.SIMPLE);
            listenerDescriptor.setHandlerMethod(invocableHandlerMethod);
            listenerDescriptor.setErrorHandlerName(annotation.errorHandler());
            listenerDescriptor.setIsolationStrategyName(annotation.isolationStrategy());
            listenerDescriptor.setMessageConverterName(annotation.messageConverter());
            listenerDescriptor.setConcurrency(annotation.concurrency());
            listenerDescriptor.setMaxFetch(maxFetch);
            this.listenerDescriptors.add(listenerDescriptor);
        }
    }

    public void afterSingletonsInstantiated() {
        this.redissonClient = (RedissonClient)this.beanFactory.getBean(RedissonClient.class);
        this.redissonListenerRegistry = (RedissonListenerRegistry)this.beanFactory.getBean("com.kuma.cloud.redis.redisson.redisson.internalRedissonListenerRegistry", RedissonListenerRegistry.class);
        this.listenerDescriptors.forEach(descriptor -> {
            String queueInterested = descriptor.getQueueInterested();
            InvocableHandlerMethod invocableHandlerMethod = descriptor.getHandlerMethod();
            RedissonListenerErrorHandler errorHandler = null;
            IsolationStrategy isolationStrategy = null;
            String errorHandlerName = descriptor.getErrorHandlerName();
            String isolationStrategyName = descriptor.getIsolationStrategyName();
            String messageConverterName = descriptor.getMessageConverterName();
            if (StringUtils.hasText((String)errorHandlerName)) {
                errorHandler = (RedissonListenerErrorHandler)this.beanFactory.getBean(errorHandlerName, RedissonListenerErrorHandler.class);
            }
            ListenerType listenerType = descriptor.getListenerType();
            if (StringUtils.hasText((String)isolationStrategyName)) {
                isolationStrategy = (IsolationStrategy)this.beanFactory.getBean(isolationStrategyName, IsolationStrategy.class);
            }
            MessageConverter messageConverter = null;
            if (StringUtils.hasText((String)messageConverterName)) {
                messageConverter = (MessageConverter)this.beanFactory.getBean(messageConverterName, MessageConverter.class);
            } else {
                try {
                    messageConverter = (MessageConverter)this.beanFactory.getBean(MessageConverter.class);
                }
                catch (BeansException e) {
                    LogUtils.error((String)"no MessageConverter found for RedissonMessageListener to apply", (Object[])new Object[0]);
                }
            }
            MessageConverter payloadMessageConverter = messageConverter;
            String isolatedQueueName = isolationStrategy == null ? queueInterested : isolationStrategy.getRedisQueueName(queueInterested);
            ContainerProperties containerProperties = new ContainerProperties();
            containerProperties.setQueue(isolatedQueueName);
            containerProperties.setListenerType(listenerType);
            containerProperties.setErrorHandler(errorHandler);
            containerProperties.setIsolationStrategy(isolationStrategy);
            containerProperties.setMessageConverter(payloadMessageConverter);
            containerProperties.setConcurrency(descriptor.getConcurrency());
            containerProperties.setMaxFetch(descriptor.getMaxFetch());
            RedissonMessageListener messageListener = this.createMessageListener(containerProperties, invocableHandlerMethod);
            RedissonListenerContainer listenerContainer = this.redissonListenerContainerFactory.createListenerContainer(containerProperties);
            listenerContainer.setRedissonClient(this.redissonClient);
            listenerContainer.setListener(messageListener);
            this.redissonListenerRegistry.registerListenerContainer(listenerContainer);
        });
    }

    private RedissonMessageListener createMessageListener(ContainerProperties containerProperties, InvocableHandlerMethod invocableHandlerMethod) {
        if (containerProperties.getListenerType() == ListenerType.BATCH) {
            return new BatchRedissonMessageListenerAdapter(invocableHandlerMethod, containerProperties.getMessageConverter(), containerProperties.getErrorHandler());
        }
        return new SimpleRedissonMessageListenerAdapter(invocableHandlerMethod, containerProperties.getMessageConverter(), containerProperties.getErrorHandler());
    }

    private static class ListenerDescriptor {
        private String queueInterested;
        private ListenerType listenerType;
        private InvocableHandlerMethod handlerMethod;
        private String errorHandlerName;
        private String isolationStrategyName;
        private String messageConverterName;
        private int concurrency;
        private int maxFetch;

        private ListenerDescriptor() {
        }

        public String getQueueInterested() {
            return this.queueInterested;
        }

        public void setQueueInterested(String queueInterested) {
            this.queueInterested = queueInterested;
        }

        public ListenerType getListenerType() {
            return this.listenerType;
        }

        public void setListenerType(ListenerType listenerType) {
            this.listenerType = listenerType;
        }

        public InvocableHandlerMethod getHandlerMethod() {
            return this.handlerMethod;
        }

        public void setHandlerMethod(InvocableHandlerMethod handlerMethod) {
            this.handlerMethod = handlerMethod;
        }

        public String getErrorHandlerName() {
            return this.errorHandlerName;
        }

        public void setErrorHandlerName(String errorHandlerName) {
            this.errorHandlerName = errorHandlerName;
        }

        public String getIsolationStrategyName() {
            return this.isolationStrategyName;
        }

        public void setIsolationStrategyName(String isolationStrategyName) {
            this.isolationStrategyName = isolationStrategyName;
        }

        public String getMessageConverterName() {
            return this.messageConverterName;
        }

        public void setMessageConverterName(String messageConverterName) {
            this.messageConverterName = messageConverterName;
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
}

