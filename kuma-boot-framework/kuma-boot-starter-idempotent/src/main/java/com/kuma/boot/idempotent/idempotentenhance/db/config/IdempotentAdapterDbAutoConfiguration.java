/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jspecify.annotations.NonNull
 *  org.mybatis.spring.annotation.MapperScan
 *  org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
 *  org.springframework.context.annotation.Bean
 *  org.springframework.context.annotation.Configuration
 */
package com.kuma.boot.idempotent.idempotentenhance.db.config;

import com.kuma.boot.idempotent.idempotentenhance.core.config.properties.IdempotentCoreProperties;
import com.kuma.boot.idempotent.idempotentenhance.core.handler.IdempotentExceptionEventHandler;
import com.kuma.boot.idempotent.idempotentenhance.core.helper.IdempotentHelper;
import com.kuma.boot.idempotent.idempotentenhance.core.repository.IdempotentRepository;
import com.kuma.boot.idempotent.idempotentenhance.db.MySqlIdempotentRepositoryImpl;
import com.kuma.boot.idempotent.idempotentenhance.db.mapper.BusinessIdempotentMapper;
import org.jspecify.annotations.NonNull;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnClass(name = "org.apache.ibatis.session.SqlSessionFactory")
@MapperScan(value={"com.kuma.boot.idempotent.idempotentenhance.db.mapper"})
public class IdempotentAdapterDbAutoConfiguration {
    @Bean(name={"mySqlIdempotentRepository"})
    @ConditionalOnMissingBean(name={"mySqlIdempotentRepository"})
    public MySqlIdempotentRepositoryImpl mySqlIdempotentRepository(@NonNull BusinessIdempotentMapper businessIdempotentMapper) {
        return new MySqlIdempotentRepositoryImpl(businessIdempotentMapper);
    }

    @Bean
    @ConditionalOnMissingBean
    public IdempotentHelper idempotentHelper(@NonNull IdempotentRepository idempotentRepository, @NonNull IdempotentExceptionEventHandler exceptionEventHandler, @NonNull IdempotentCoreProperties idempotentCoreProperties) {
        return new IdempotentHelper(exceptionEventHandler, idempotentRepository, idempotentCoreProperties);
    }
}

