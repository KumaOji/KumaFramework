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

import com.kuma.boot.cache.redis.delay.listener.RedissonListenerContainer;
import jakarta.annotation.PreDestroy;
import java.util.ArrayList;
import java.util.List;
import org.springframework.context.Lifecycle;
import org.springframework.context.SmartLifecycle;

/**
 * RedissonListenerRegistry
 *
 * @author kuma
 * @version 2021.10
 * @since 2022-02-18 10:24:16
 */
public class RedissonListenerRegistry implements SmartLifecycle {

    private List<RedissonListenerContainer> listenerContainers = new ArrayList<>(8);

    public void registerListenerContainer(RedissonListenerContainer listenerContainer) {
        this.listenerContainers.add(listenerContainer);
    }

    @PreDestroy
    public void destroy() {
        this.stop();
    }

    @Override
    public boolean isAutoStartup() {
        return true;
    }

    @Override
    public void stop(Runnable callback) {
        this.listenerContainers.forEach(listenerContainer -> listenerContainer.stop(callback));
    }

    @Override
    public void start() {
        this.listenerContainers.forEach(Lifecycle::start);
    }

    @Override
    public void stop() {
        this.listenerContainers.forEach(Lifecycle::stop);
    }

    @Override
    public boolean isRunning() {
        return false;
    }

    @Override
    public int getPhase() {
        return Integer.MAX_VALUE;
    }
}
