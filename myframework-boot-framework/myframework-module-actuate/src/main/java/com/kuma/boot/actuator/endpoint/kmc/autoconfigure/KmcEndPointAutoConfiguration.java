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

package com.kuma.boot.actuator.endpoint.kmc.autoconfigure;

import com.kuma.boot.actuator.endpoint.kmc.KmcEndPoint;
import com.kuma.boot.actuator.endpoint.kmc.KmcHealthEndPoint;
import com.kuma.boot.actuator.health.KmcHealthIndicator;
import com.kuma.boot.common.constant.StarterNameConstants;
import com.kuma.boot.common.utils.log.LogUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.actuate.autoconfigure.endpoint.condition.ConditionalOnAvailableEndpoint;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Bean;

/**
 * EndPoint配置
 *
 * @author kuma
 * @version 2022.03
 * @since 2021/04/02 10:25
 */
@AutoConfiguration
@ConditionalOnAvailableEndpoint(endpoint = KmcEndPoint.class)
public class KmcEndPointAutoConfiguration implements InitializingBean {

    @Override
    public void afterPropertiesSet() throws Exception {
        LogUtils.started(KmcEndPointAutoConfiguration.class, StarterNameConstants.ACTUATOR_STARTER);
    }

    @Bean
    @ConditionalOnBean(KmcHealthIndicator.class)
    public KmcHealthEndPoint kmcHealthEndPoint(KmcHealthIndicator kmcHealthIndicator) {
        return new KmcHealthEndPoint(kmcHealthIndicator);
    }

    @Bean
    public KmcEndPoint kmcEndPoint() {
        return new KmcEndPoint();
    }

}
