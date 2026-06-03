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

package com.kuma.boot.graphql.autoconfigure.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

/**
 * GraphQL 模块配置（{@code kuma.boot.graphql.*}）.
 *
 * @author kuma
 */
@Data
@ConfigurationProperties(prefix = GraphqlProperties.PREFIX)
public class GraphqlProperties {

    public static final String PREFIX = "kuma.boot.graphql";

    /** 是否启用本模块. */
    private boolean enabled = true;

    @NestedConfigurationProperty
    private Scalars scalars = new Scalars();

    @NestedConfigurationProperty
    private ExceptionHandler exceptionHandler = new ExceptionHandler();

    @Data
    public static class Scalars {
        /** 是否注册扩展标量（Long、BigDecimal、LocalDate、LocalDateTime）. */
        private boolean enabled = true;
    }

    @Data
    public static class ExceptionHandler {
        /** 是否启用统一异常映射. */
        private boolean enabled = true;
        /** 在 extensions 中是否暴露完整堆栈（仅供开发调试）. */
        private boolean exposeStackTrace = false;
    }
}
