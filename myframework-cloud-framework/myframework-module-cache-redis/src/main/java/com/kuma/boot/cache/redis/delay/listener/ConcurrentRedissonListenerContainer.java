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
import org.springframework.context.Lifecycle;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.List;

/**
 * ConcurrentRedissonListenerContainer
 *
 * @author kuma
 * @version 2021.10
 * @since 2022-02-18 10:36:41
 */
public class ConcurrentRedissonListenerContainer extends AbstractRedissonListenerContainer {

    private final int concurrency;

    public int getConcurrency() {
        return concurrency;
    }

    private List<RedissonListenerContainer> containers = new ArrayList<>();

    private RedissonListenerContainerFactory containerFactory = new RedissonListenerContainerFactoryAdapter();

    public ConcurrentRedissonListenerContainer( ContainerProperties containerProperties, int concurrency ) {
        super(containerProperties);
        Assert.isTrue(concurrency > 0, "concurrency must be greater than 0");
        this.concurrency = concurrency;
    }

    @Override
    protected void doStart() {
        for (int i = 0; i < this.concurrency; i++) {
            RedissonListenerContainer container =
                    containerFactory.createListenerContainer(this.getContainerProperties());
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

    /**
     * RedissonListenerContainerFactoryAdapter
     *
     * @author kuma
     * @version 2026.01
     * @since 2025-12-19 09:30:45
     */
    private static class RedissonListenerContainerFactoryAdapter implements RedissonListenerContainerFactory {

        @Override
        public RedissonListenerContainer createListenerContainer( ContainerProperties containerProperties ) {
            ListenerType listenerType = containerProperties.getListenerType();
            if (listenerType == ListenerType.BATCH) {
                return new BatchRedissonListenerContainer(containerProperties, containerProperties.getMaxFetch());
            }
            return new SimpleRedissonListenerContainer(containerProperties);
        }
    }
}
