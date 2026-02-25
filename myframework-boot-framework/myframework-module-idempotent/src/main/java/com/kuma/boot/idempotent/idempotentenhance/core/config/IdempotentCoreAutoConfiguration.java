/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.boot.autoconfigure.condition.ConditionalOnBean
 *  org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
 *  org.springframework.boot.context.properties.EnableConfigurationProperties
 *  org.springframework.context.annotation.Bean
 *  org.springframework.context.annotation.Configuration
 */
package com.kuma.boot.idempotent.idempotentenhance.core.config;

import com.kuma.boot.idempotent.idempotentenhance.core.aspect.IdempotentAspect;
import com.kuma.boot.idempotent.idempotentenhance.core.config.properties.IdempotentCoreProperties;
import com.kuma.boot.idempotent.idempotentenhance.core.handler.DefaultIdempotentExceptionEventHandler;
import com.kuma.boot.idempotent.idempotentenhance.core.handler.IdempotentExceptionEventHandler;
import com.kuma.boot.idempotent.idempotentenhance.core.helper.IdempotentHelper;
import com.kuma.boot.idempotent.idempotentenhance.core.registry.IdempotentRepositoryRegistrySupport;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(value={IdempotentCoreProperties.class})
public class IdempotentCoreAutoConfiguration {
    @Bean
    @ConditionalOnBean(value={IdempotentHelper.class})
    @ConditionalOnMissingBean(value={IdempotentAspect.class})
    public IdempotentAspect idempotentAspect() {
        return new IdempotentAspect();
    }

    @Bean
    @ConditionalOnMissingBean
    public IdempotentExceptionEventHandler defaultIdempotentExceptionEventHandler() {
        return new DefaultIdempotentExceptionEventHandler();
    }

    public IdempotentRepositoryRegistrySupport idempotentRepositoryRegistrySupport(IdempotentCoreProperties properties) {
        return new IdempotentRepositoryRegistrySupport(properties);
    }
}

