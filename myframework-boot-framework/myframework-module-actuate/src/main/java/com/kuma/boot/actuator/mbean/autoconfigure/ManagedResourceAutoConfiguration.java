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

package com.kuma.boot.actuator.mbean.autoconfigure;

import com.kuma.boot.actuator.mbean.autoconfigure.properties.ManagedResourceProperties;
import com.kuma.boot.actuator.mbean.KmcMBeanRegistrar;
import com.kuma.boot.actuator.mbean.KmcManagedResource;
import com.kuma.boot.common.constant.StarterNameConstants;
import com.kuma.boot.common.utils.log.LogUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

import javax.management.MalformedObjectNameException;

/**
 * EndPoint配置
 *
 * @author kuma
 * @version 2022.03
 * @since 2021/04/02 10:25
 */
@AutoConfiguration
@EnableConfigurationProperties({ManagedResourceProperties.class})
@ConditionalOnProperty(
        prefix = ManagedResourceProperties.PREFIX,
        name = "enabled",
        havingValue = "true",
        matchIfMissing = true)
public class ManagedResourceAutoConfiguration implements InitializingBean {

    @Override
    public void afterPropertiesSet() throws Exception {
        LogUtils.started(ManagedResourceAutoConfiguration.class, StarterNameConstants.ACTUATOR_STARTER);
    }

    @Bean
    public KmcManagedResource kmcManagedResource() {
        return new KmcManagedResource();
    }

    @Bean
    public KmcMBeanRegistrar kmcMbeanRegistrar() throws MalformedObjectNameException {
        return new KmcMBeanRegistrar();
    }
}
