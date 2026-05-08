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

package com.kuma.boot.idempotent.idempotentenhance.db.config;

import com.kuma.boot.idempotent.idempotentenhance.core.config.properties.IdempotentCoreProperties;
import com.kuma.boot.idempotent.idempotentenhance.core.handler.IdempotentExceptionEventHandler;
import com.kuma.boot.idempotent.idempotentenhance.core.helper.IdempotentHelper;
import com.kuma.boot.idempotent.idempotentenhance.core.repository.IdempotentRepository;
import com.kuma.boot.idempotent.idempotentenhance.db.MySqlIdempotentRepositoryImpl;
import com.kuma.boot.idempotent.idempotentenhance.db.mapper.BusinessIdempotentMapper;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * MySQL 幂等仓储装配；{@code BusinessIdempotentMapper} 的扫描由 {@code kuma-boot-starter-data-mybatis} 的
 * {@code MybatisPlusAutoConfiguration}（{@code @MapperScan}）统一声明，避免 Native/AOT 下单独在此类上
 * {@code @MapperScan} 导致装配失败。
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnClass(name = "org.apache.ibatis.session.SqlSessionFactory")
public class IdempotentAdapterDbAutoConfiguration {

    @Bean(name = "mySqlIdempotentRepository")
    @ConditionalOnMissingBean(name = "mySqlIdempotentRepository")
    public MySqlIdempotentRepositoryImpl mySqlIdempotentRepository(
            @NonNull BusinessIdempotentMapper businessIdempotentMapper) {
        return new MySqlIdempotentRepositoryImpl(businessIdempotentMapper);
    }

    @Bean
    @ConditionalOnMissingBean
    public IdempotentHelper idempotentHelper(
            @Qualifier("mySqlIdempotentRepository") @NonNull IdempotentRepository idempotentRepository,
            @NonNull IdempotentExceptionEventHandler exceptionEventHandler,
            @NonNull IdempotentCoreProperties idempotentCoreProperties) {
        return new IdempotentHelper(exceptionEventHandler, idempotentRepository, idempotentCoreProperties);
    }
}
