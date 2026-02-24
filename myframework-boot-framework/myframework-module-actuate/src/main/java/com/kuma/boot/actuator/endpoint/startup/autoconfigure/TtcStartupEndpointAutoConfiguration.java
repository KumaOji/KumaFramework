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

package com.kuma.boot.actuator.endpoint.startup.autoconfigure;

import com.kuma.boot.actuator.endpoint.startup.KmcStartupEndpoint;
import com.kuma.boot.common.constant.StarterNameConstants;
import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.boot.core.startup.StartupReporter;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.actuate.autoconfigure.endpoint.condition.ConditionalOnAvailableEndpoint;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Bean;

/**
 * CanalClientConfiguration
 *
 * @author kuma
 * @version 2021.9
 * @since 2021-09-04 07:28:42
 */
@AutoConfiguration
@ConditionalOnAvailableEndpoint(endpoint = KmcStartupEndpoint.class)
public class KmcStartupEndpointAutoConfiguration implements InitializingBean {

    @Override
    public void afterPropertiesSet() throws Exception {
        LogUtils.started(KmcStartupEndpointAutoConfiguration.class, StarterNameConstants.ACTUATOR_STARTER);
    }

    @Bean
    @ConditionalOnBean(StartupReporter.class)
    public KmcStartupEndpoint kmcStartupEndpoint(StartupReporter startupReporter) {
        return new KmcStartupEndpoint(startupReporter);
    }
}
