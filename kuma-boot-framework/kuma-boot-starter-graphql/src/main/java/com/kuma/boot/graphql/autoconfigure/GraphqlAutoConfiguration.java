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

package com.kuma.boot.graphql.autoconfigure;

import com.kuma.boot.common.constant.StarterNameConstants;
import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.boot.graphql.autoconfigure.properties.GraphqlProperties;
import com.kuma.boot.graphql.exception.KmcDataFetcherExceptionResolver;
import com.kuma.boot.graphql.scalar.KmcScalarRegistrar;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.graphql.execution.DataFetcherExceptionResolver;
import org.springframework.graphql.execution.RuntimeWiringConfigurer;

/**
 * Spring for GraphQL 自动配置.
 *
 * <p>注册：
 * <ul>
 *   <li>{@link KmcScalarRegistrar} — 扩展标量（Long / BigDecimal / LocalDate / LocalDateTime）</li>
 *   <li>{@link KmcDataFetcherExceptionResolver} — 统一异常映射</li>
 * </ul>
 *
 * @author kuma
 */
@AutoConfiguration
@EnableConfigurationProperties(GraphqlProperties.class)
@ConditionalOnClass(name = "org.springframework.graphql.execution.GraphQlSource")
@ConditionalOnProperty(prefix = GraphqlProperties.PREFIX, name = "enabled", havingValue = "true", matchIfMissing = true)
public class GraphqlAutoConfiguration implements InitializingBean {

    @Override
    public void afterPropertiesSet() {
        LogUtils.started(GraphqlAutoConfiguration.class, StarterNameConstants.GRAPHQL_STARTER);
    }

    @Bean
    @ConditionalOnMissingBean(KmcScalarRegistrar.class)
    @ConditionalOnProperty(prefix = GraphqlProperties.PREFIX + ".scalars", name = "enabled",
            havingValue = "true", matchIfMissing = true)
    public RuntimeWiringConfigurer kmcScalarRegistrar() {
        return new KmcScalarRegistrar();
    }

    @Bean
    @ConditionalOnMissingBean(KmcDataFetcherExceptionResolver.class)
    @ConditionalOnProperty(prefix = GraphqlProperties.PREFIX + ".exception-handler", name = "enabled",
            havingValue = "true", matchIfMissing = true)
    public DataFetcherExceptionResolver kmcDataFetcherExceptionResolver(GraphqlProperties properties) {
        return new KmcDataFetcherExceptionResolver(properties);
    }
}
