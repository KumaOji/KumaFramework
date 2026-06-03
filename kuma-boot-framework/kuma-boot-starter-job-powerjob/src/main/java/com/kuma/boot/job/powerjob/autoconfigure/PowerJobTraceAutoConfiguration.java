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

package com.kuma.boot.job.powerjob.autoconfigure;

import com.kuma.boot.job.powerjob.aspect.PowerJobMdcInspector;
import com.kuma.boot.job.powerjob.autoconfigure.properties.PowerJobProperties;
import io.micrometer.tracing.ScopedSpan;
import io.micrometer.tracing.Tracer;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;

/**
 * PowerJob Trace 自动配置
 *
 * @author kuma
 * @version 2024.1
 * @since 2024-01-01
 */
@AutoConfiguration(after = PowerJobAutoConfiguration.class)
@ConditionalOnClass({ScopedSpan.class, Tracer.class})
@ConditionalOnProperty(prefix = PowerJobProperties.PREFIX, name = "trace", havingValue = "true")
public class PowerJobTraceAutoConfiguration {

    @Bean
    public PowerJobMdcInspector powerJobMdcInspector() {
        return new PowerJobMdcInspector();
    }
}
