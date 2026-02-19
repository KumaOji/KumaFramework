/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  com.kuma.boot.common.support.thread.ThreadFactoryCreator
 *  org.redisson.api.RedissonClient
 *  org.springframework.core.task.SimpleAsyncTaskExecutor
 *  org.springframework.util.Assert
 */
package com.kuma.boot.cache.redis.delay.listener;

import com.kuma.boot.common.support.thread.ThreadFactoryCreator;
import java.util.concurrent.Executor;
import org.redisson.api.RedissonClient;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.util.Assert;

public abstract class AbstractRedissonListenerContainer
implements RedissonListenerContainer {
    private final Object lifecycleMonitor = new Object();
    private Executor taskExecutor = new SimpleAsyncTaskExecutor(ThreadFactoryCreator.create((String)"kmc-redisson-simple-consume-thread"));
    private final ContainerProperties containerProperties;
    private RedissonMessageListener<?> redissonListener;
    private RedissonClient redissonClient;
    private boolean autoStartup = true;
    private int phase = Integer.MAX_VALUE;
    private volatile boolean running = false;

    public Executor getTaskExecutor() {
        return this.taskExecutor;
    }

    public void setTaskExecutor(Executor taskExecutor) {
        this.taskExecutor = taskExecutor;
    }

    public RedissonMessageListener<?> getRedissonListener() {
        return this.redissonListener;
    }

    public RedissonClient getRedissonClient() {
        return this.redissonClient;
    }

    public void setAutoStartup(boolean autoStartup) {
        this.autoStartup = autoStartup;
    }

    public void setPhase(int phase) {
        this.phase = phase;
    }

    public AbstractRedissonListenerContainer(ContainerProperties containerProperties) {
        Assert.notNull((Object)containerProperties, (String)"ContainerProperties must not be null");
        this.containerProperties = containerProperties;
    }

    public boolean isAutoStartup() {
        return this.autoStartup;
    }

    public void stop(Runnable callback) {
        try {
            this.stop();
        }
        finally {
            callback.run();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void start() {
        if (this.isRunning()) {
            return;
        }
        Object object = this.lifecycleMonitor;
        synchronized (object) {
            this.doStart();
            this.running = true;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void stop() {
        if (!this.isRunning()) {
            return;
        }
        Object object = this.lifecycleMonitor;
        synchronized (object) {
            this.doStop();
            this.running = false;
        }
    }

    public boolean isRunning() {
        return this.running;
    }

    public int getPhase() {
        return this.phase;
    }

    @Override
    public ContainerProperties getContainerProperties() {
        return this.containerProperties;
    }

    @Override
    public void setListener(RedissonMessageListener<?> listener) {
        Assert.notNull(listener, (String)"RedissonMessageListener must not be null");
        this.redissonListener = listener;
    }

    @Override
    public void setRedissonClient(RedissonClient redissonClient) {
        Assert.notNull((Object)redissonClient, (String)"RedissonClient must not be null");
        this.redissonClient = redissonClient;
    }

    protected abstract void doStart();

    protected abstract void doStop();

    protected static enum ConsumerStatus {
        CREATED,
        RUNNING,
        STOPPED;

    }
}

