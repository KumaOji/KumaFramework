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

package com.kuma.boot.job.quartz.autoconfigure;

import com.kuma.boot.job.quartz.aspect.QuartzMdcInspector;
import com.kuma.boot.job.quartz.autoconfigure.properties.QuartzProperties;
import io.micrometer.tracing.ScopedSpan;
import io.micrometer.tracing.Tracer;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;

/**
 * Quartz MDC / Trace 自动配置。
 * <p>当 Micrometer Tracing 在类路径且 {@code kuma.boot.job.quartz.trace=true} 时激活。</p>
 */
@AutoConfiguration(after = QuartzAutoConfiguration.class)
@ConditionalOnClass({ScopedSpan.class, Tracer.class})
@ConditionalOnProperty(prefix = QuartzProperties.PREFIX, name = "trace", havingValue = "true", matchIfMissing = true)
public class QuartzTraceAutoConfiguration {

    @Bean
    public QuartzMdcInspector quartzMdcInspector() {
        return new QuartzMdcInspector();
    }
}
