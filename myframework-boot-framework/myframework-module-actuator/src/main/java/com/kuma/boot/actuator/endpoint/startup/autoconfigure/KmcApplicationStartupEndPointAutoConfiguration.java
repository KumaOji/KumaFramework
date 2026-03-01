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

package com.kuma.boot.actuator.endpoint.startup.autoconfigure;

import com.kuma.boot.actuator.endpoint.startup.KmcApplicationStartupEndpoint;
import com.kuma.boot.core.startup.StartupReporter;
import org.springframework.boot.actuate.autoconfigure.endpoint.condition.ConditionalOnAvailableEndpoint;
import org.springframework.boot.actuate.autoconfigure.startup.StartupEndpointAutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;

/**
 * {@link EnableAutoConfiguration Auto-configuration} for {@link KmcApplicationStartupEndpoint}.
 *
 * @author Zhijie
 * @since 2020/7/7
 */
@AutoConfiguration(before = StartupEndpointAutoConfiguration.class)
@ConditionalOnBean(StartupReporter.class)
@ConditionalOnAvailableEndpoint(endpoint = KmcApplicationStartupEndpoint.class)
public class KmcApplicationStartupEndPointAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public KmcApplicationStartupEndpoint kmcApplicationStartupEndpoint(StartupReporter startupReporter) {
        return new KmcApplicationStartupEndpoint(startupReporter);
    }
}
