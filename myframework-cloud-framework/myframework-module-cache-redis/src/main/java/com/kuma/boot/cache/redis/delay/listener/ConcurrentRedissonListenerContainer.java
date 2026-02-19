/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  org.springframework.context.Lifecycle
 *  org.springframework.util.Assert
 */
package com.kuma.boot.cache.redis.delay.listener;

import com.kuma.boot.cache.redis.delay.consts.ListenerType;

import java.util.ArrayList;
import java.util.List;
import org.springframework.context.Lifecycle;
import org.springframework.util.Assert;

public class ConcurrentRedissonListenerContainer
extends AbstractRedissonListenerContainer {
    private final int concurrency;
    private List<RedissonListenerContainer> containers = new ArrayList<RedissonListenerContainer>();
    private RedissonListenerContainerFactory containerFactory = new RedissonListenerContainerFactoryAdapter();

    public int getConcurrency() {
        return this.concurrency;
    }

    public ConcurrentRedissonListenerContainer(ContainerProperties containerProperties, int concurrency) {
        super(containerProperties);
        Assert.isTrue((concurrency > 0 ? 1 : 0) != 0, (String)"concurrency must be greater than 0");
        this.concurrency = concurrency;
    }

    @Override
    protected void doStart() {
        for (int i = 0; i < this.concurrency; ++i) {
            RedissonListenerContainer container = this.containerFactory.createListenerContainer(this.getContainerProperties());
            container.setRedissonClient(this.getRedissonClient());
            container.setListener(this.getRedissonListener());
            container.start();
            this.containers.add(container);
        }
    }

    @Override
    protected void doStop() {
        this.containers.forEach(Lifecycle::stop);
        this.containers.clear();
    }

    private static class RedissonListenerContainerFactoryAdapter
    implements RedissonListenerContainerFactory {
        private RedissonListenerContainerFactoryAdapter() {
        }

        @Override
        public RedissonListenerContainer createListenerContainer(ContainerProperties containerProperties) {
            ListenerType listenerType = containerProperties.getListenerType();
            if (listenerType == ListenerType.BATCH) {
                return new BatchRedissonListenerContainer(containerProperties, containerProperties.getMaxFetch());
            }
            return new SimpleRedissonListenerContainer(containerProperties);
        }
    }
}

