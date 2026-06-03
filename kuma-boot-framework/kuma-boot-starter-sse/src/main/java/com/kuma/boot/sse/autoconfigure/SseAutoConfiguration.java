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

package com.kuma.boot.sse.autoconfigure;

import com.kuma.boot.common.constant.StarterNameConstants;
import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.boot.sse.autoconfigure.properties.SseProperties;
import com.kuma.boot.sse.core.DefaultSseEmitterManager;
import com.kuma.boot.sse.core.SseEmitterManager;
import com.kuma.boot.sse.core.SseEvent;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

/**
 * SSE（Server-Sent Events）自动配置.
 *
 * <p>注册默认的 {@link SseEmitterManager} Bean，并在配置了心跳间隔时启动心跳调度。
 * 仅在 Servlet Web 环境下激活。
 *
 * @author kuma
 */
@AutoConfiguration
@EnableConfigurationProperties(SseProperties.class)
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
@ConditionalOnProperty(prefix = SseProperties.PREFIX, name = "enabled", havingValue = "true", matchIfMissing = true)
public class SseAutoConfiguration implements InitializingBean, DisposableBean {

    private ScheduledExecutorService heartbeatScheduler;

    @Override
    public void afterPropertiesSet() {
        LogUtils.started(SseAutoConfiguration.class, StarterNameConstants.SSE_STARTER);
    }

    @Bean
    @ConditionalOnMissingBean(SseEmitterManager.class)
    public SseEmitterManager sseEmitterManager(SseProperties properties) {
        DefaultSseEmitterManager manager = new DefaultSseEmitterManager(properties);
        scheduleHeartbeat(manager, properties);
        return manager;
    }

    @Override
    public void destroy() {
        if (heartbeatScheduler != null && !heartbeatScheduler.isShutdown()) {
            heartbeatScheduler.shutdownNow();
        }
    }

    // ── 内部方法 ──────────────────────────────────────────────────────────────

    private void scheduleHeartbeat(SseEmitterManager manager, SseProperties properties) {
        long intervalMs = properties.getHeartbeatInterval().toMillis();
        if (intervalMs <= 0) {
            return;
        }
        heartbeatScheduler = Executors.newSingleThreadScheduledExecutor(r -> {
            Thread t = new Thread(r, "kuma-sse-heartbeat");
            t.setDaemon(true);
            return t;
        });
        heartbeatScheduler.scheduleAtFixedRate(
                () -> manager.sendToAll(SseEvent.heartbeat()),
                intervalMs,
                intervalMs,
                TimeUnit.MILLISECONDS
        );
    }
}
