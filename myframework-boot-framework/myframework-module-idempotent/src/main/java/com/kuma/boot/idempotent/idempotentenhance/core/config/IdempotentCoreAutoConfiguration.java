/*
 * Copyright (c) 2020-2030, kuma (2569277704@qq.com & https://blog.kumacloud.top/).
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

/** 幂等核心自动配置 */
@Configuration
// @ConditionalOnProperties(prefix = "enhance.idempotent", properties = {"enable"}, values =
// {"true"})
@EnableConfigurationProperties({IdempotentCoreProperties.class})
public class IdempotentCoreAutoConfiguration {

    // @Bean
    // public ApplicationContextHelper applicationContextHelper() {
    //	return new ApplicationContextHelper();
    // }

    @Bean
    @ConditionalOnBean({IdempotentHelper.class})
    @ConditionalOnMissingBean({IdempotentAspect.class})
    public IdempotentAspect idempotentAspect() {
        return new IdempotentAspect();
    }

    @Bean
    @ConditionalOnMissingBean
    public IdempotentExceptionEventHandler defaultIdempotentExceptionEventHandler() {
        return new DefaultIdempotentExceptionEventHandler();
    }

    // 等待后续实现了动态选择幂等组件时再注册
    //    @Bean
    public IdempotentRepositoryRegistrySupport idempotentRepositoryRegistrySupport(
            IdempotentCoreProperties properties) {
        return new IdempotentRepositoryRegistrySupport(properties);
    }
}
