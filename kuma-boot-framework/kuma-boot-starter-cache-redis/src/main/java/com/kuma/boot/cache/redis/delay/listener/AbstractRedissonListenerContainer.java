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

import com.kuma.boot.common.support.thread.ThreadFactoryCreator;
import java.util.concurrent.Executor;
import org.redisson.api.RedissonClient;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.util.Assert;

/**
 * AbstractRedissonListenerContainer
 *
 * @author kuma
 * @version 2021.10
 * @since 2022-02-18 10:26:01
 */
public abstract class AbstractRedissonListenerContainer implements RedissonListenerContainer {

    private final Object lifecycleMonitor = new Object();

    private Executor taskExecutor =
            new SimpleAsyncTaskExecutor(ThreadFactoryCreator.create("kmc-redisson-simple-consume-thread"));

    public Executor getTaskExecutor() {
        return taskExecutor;
    }

    public void setTaskExecutor(Executor taskExecutor) {
        this.taskExecutor = taskExecutor;
    }

    private final ContainerProperties containerProperties;

    private RedissonMessageListener<?> redissonListener;

    public RedissonMessageListener<?> getRedissonListener() {
        return redissonListener;
    }

    private RedissonClient redissonClient;

    public RedissonClient getRedissonClient() {
        return redissonClient;
    }

    private boolean autoStartup = true;

    public void setAutoStartup(boolean autoStartup) {
        this.autoStartup = autoStartup;
    }

    private int phase = Integer.MAX_VALUE;

    public void setPhase(int phase) {
        this.phase = phase;
    }

    private volatile boolean running = false;

    public AbstractRedissonListenerContainer(ContainerProperties containerProperties) {
        Assert.notNull(containerProperties, "ContainerProperties must not be null");
        this.containerProperties = containerProperties;
    }

    @Override
    public boolean isAutoStartup() {
        return this.autoStartup;
    }

    @Override
    public void stop(Runnable callback) {
        try {
            this.stop();
        } finally {
            callback.run();
        }
    }

    @Override
    public void start() {
        if (isRunning()) {
            return;
        }
        synchronized (this.lifecycleMonitor) {
            this.doStart();
            this.running = true;
        }
    }

    @Override
    public void stop() {
        if (!isRunning()) {
            return;
        }
        synchronized (this.lifecycleMonitor) {
            this.doStop();
            this.running = false;
        }
    }

    @Override
    public boolean isRunning() {
        return this.running;
    }

    @Override
    public int getPhase() {
        return this.phase;
    }

    @Override
    public ContainerProperties getContainerProperties() {
        return this.containerProperties;
    }

    @Override
    public void setListener(RedissonMessageListener<?> listener) {
        Assert.notNull(listener, "RedissonMessageListener must not be null");
        this.redissonListener = listener;
    }

    @Override
    public void setRedissonClient(RedissonClient redissonClient) {
        Assert.notNull(redissonClient, "RedissonClient must not be null");
        this.redissonClient = redissonClient;
    }

    /** do start */
    protected abstract void doStart();

    /** do stop */
    protected abstract void doStop();

    protected enum ConsumerStatus {
        /** created */
        CREATED,
        /** running */
        RUNNING,
        /** stopped */
        STOPPED
    }
}
