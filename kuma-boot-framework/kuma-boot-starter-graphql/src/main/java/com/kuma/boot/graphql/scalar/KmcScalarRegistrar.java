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

package com.kuma.boot.graphql.scalar;

import graphql.schema.idl.RuntimeWiring;
import org.springframework.graphql.execution.RuntimeWiringConfigurer;

/**
 * 将 {@link KmcScalars} 中定义的扩展标量注册到 GraphQL RuntimeWiring.
 *
 * <p>Spring for GraphQL 会自动发现所有 {@link RuntimeWiringConfigurer} Bean 并在构建
 * {@code GraphQlSource} 时依次调用，无需额外配置。
 *
 * <p>在 Schema 文件中声明对应标量即可使用：
 * <pre>
 * scalar Long
 * scalar BigDecimal
 * scalar LocalDate
 * scalar LocalDateTime
 * </pre>
 *
 * @author kuma
 */
public class KmcScalarRegistrar implements RuntimeWiringConfigurer {

    @Override
    public void configure(RuntimeWiring.Builder builder) {
        builder
                .scalar(KmcScalars.LONG)
                .scalar(KmcScalars.BIG_DECIMAL)
                .scalar(KmcScalars.LOCAL_DATE)
                .scalar(KmcScalars.LOCAL_DATE_TIME);
    }
}
