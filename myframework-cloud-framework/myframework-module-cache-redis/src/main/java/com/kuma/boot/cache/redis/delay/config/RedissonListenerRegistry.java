/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  jakarta.annotation.PreDestroy
 *  org.springframework.context.Lifecycle
 *  org.springframework.context.SmartLifecycle
 */
package com.kuma.boot.cache.redis.delay.config;

import com.kuma.boot.cache.redis.delay.listener.RedissonListenerContainer;
import jakarta.annotation.PreDestroy;
import java.util.ArrayList;
import java.util.List;
import org.springframework.context.Lifecycle;
import org.springframework.context.SmartLifecycle;

public class RedissonListenerRegistry
implements SmartLifecycle {
    private List<RedissonListenerContainer> listenerContainers = new ArrayList<RedissonListenerContainer>(8);

    public void registerListenerContainer(RedissonListenerContainer listenerContainer) {
        this.listenerContainers.add(listenerContainer);
    }

    @PreDestroy
    public void destroy() {
        this.stop();
    }

    public boolean isAutoStartup() {
        return true;
    }

    public void stop(Runnable callback) {
        this.listenerContainers.forEach(listenerContainer -> listenerContainer.stop(callback));
    }

    public void start() {
        this.listenerContainers.forEach(Lifecycle::start);
    }

    public void stop() {
        this.listenerContainers.forEach(Lifecycle::stop);
    }

    public boolean isRunning() {
        return false;
    }

    public int getPhase() {
        return Integer.MAX_VALUE;
    }
}

