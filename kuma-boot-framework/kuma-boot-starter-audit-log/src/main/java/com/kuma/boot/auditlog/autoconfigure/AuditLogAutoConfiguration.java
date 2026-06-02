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

package com.kuma.boot.auditlog.autoconfigure;

import com.kuma.boot.auditlog.annotation.AuditLog;
import com.kuma.boot.auditlog.aop.AuditLogAspect;
import com.kuma.boot.auditlog.autoconfigure.properties.AuditLogProperties;
import com.kuma.boot.auditlog.core.AuditLogStore;
import com.kuma.boot.auditlog.core.AuditOperatorProvider;
import com.kuma.boot.auditlog.core.DefaultAuditOperatorProvider;
import com.kuma.boot.auditlog.core.Slf4jAuditLogStore;
import com.kuma.boot.common.constant.StarterNameConstants;
import com.kuma.boot.common.utils.log.LogUtils;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

/**
 * 操作审计日志自动配置.
 *
 * @author kuma
 */
@AutoConfiguration
@EnableConfigurationProperties(AuditLogProperties.class)
@ConditionalOnClass(AuditLog.class)
@ConditionalOnProperty(
        prefix = AuditLogProperties.PREFIX,
        name = "enabled",
        havingValue = "true",
        matchIfMissing = true)
public class AuditLogAutoConfiguration implements InitializingBean {

    @Bean
    @ConditionalOnMissingBean
    public AuditOperatorProvider auditOperatorProvider(AuditLogProperties properties) {
        return new DefaultAuditOperatorProvider(properties);
    }

    @Bean
    @ConditionalOnMissingBean
    public AuditLogStore auditLogStore() {
        return new Slf4jAuditLogStore();
    }

    @Bean
    @ConditionalOnMissingBean
    public AuditLogAspect auditLogAspect(AuditLogStore store,
            AuditOperatorProvider operatorProvider, AuditLogProperties properties) {
        Executor executor = properties.isAsync() ? buildExecutor(properties) : null;
        return new AuditLogAspect(store, operatorProvider, properties, executor);
    }

    private Executor buildExecutor(AuditLogProperties properties) {
        AuditLogProperties.Executor cfg = properties.getExecutor();
        AtomicInteger counter = new AtomicInteger(1);
        return new ThreadPoolExecutor(
                cfg.getCorePoolSize(),
                cfg.getMaxPoolSize(),
                60L, TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(cfg.getQueueCapacity()),
                runnable -> {
                    Thread thread = new Thread(runnable, cfg.getThreadNamePrefix() + counter.getAndIncrement());
                    thread.setDaemon(true);
                    return thread;
                },
                // 队列满时由调用线程同步执行，保证审计不丢失
                new ThreadPoolExecutor.CallerRunsPolicy());
    }

    @Override
    public void afterPropertiesSet() {
        LogUtils.started(AuditLogAutoConfiguration.class, StarterNameConstants.AUDIT_LOG_STARTER);
    }
}
