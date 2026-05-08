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

package com.kuma.boot.data.mybatis.autoconfigure;

import com.kuma.boot.data.mybatis.autoconfigure.properties.MybatisPlusProperties;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;

/**
 * 框架内置 Mapper 包统一扫描。
 *
 * <p>必须通过具体主启动类上的 {@link org.springframework.context.annotation.Import} 引入（勿放在组合注解如
 * {@code @KumaBootApplication} 上，否则 Graal {@code processAot} 可能跳过）。勿仅依赖
 * {@code @ComponentScan("com.kuma.boot")} 拾取带 {@code @MapperScan} 的 {@code @AutoConfiguration}。
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnClass(name = "org.apache.ibatis.session.SqlSessionFactory")
@ConditionalOnProperty(
        prefix = MybatisPlusProperties.PREFIX,
        name = "enabled",
        havingValue = "true",
        matchIfMissing = true)
@MapperScan(
        basePackages = {
            "com.kuma.boot.mybatis.mapper",
            "com.kuma.boot.data.mybatis.delay",
            "com.kuma.boot.idempotent.idempotentenhance.db.mapper",
            "com.kuma.boot.idgenerator.uid.worker.dao",
        })
public class KumaFrameworkMapperScanConfiguration {}
