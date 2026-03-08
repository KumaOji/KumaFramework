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

package com.kuma.boot.actuator.info.autoconfigure;

import com.kuma.boot.actuator.info.KmcInfoContributor;
import com.kuma.boot.actuator.info.KmcObservation;
import io.micrometer.observation.ObservationRegistry;
import org.springframework.boot.actuate.autoconfigure.info.ConditionalOnEnabledInfoContributor;
import org.springframework.boot.actuate.autoconfigure.info.InfoContributorAutoConfiguration;
import org.springframework.boot.actuate.autoconfigure.info.InfoContributorFallback;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.core.annotation.Order;

/**
 * KmcInfoAutoConfiguration
 *
 * @author kuma
 * @version 2022.03
 * @since 2021/06/17 17:21
 */
@AutoConfiguration
@ConditionalOnEnabledInfoContributor(value = "kmc", fallback = InfoContributorFallback.DISABLE)
public class KmcInfoAutoConfiguration {

    @Bean
    @Order(InfoContributorAutoConfiguration.DEFAULT_ORDER)
    public KmcInfoContributor kmcInfoContributor() {
        return new KmcInfoContributor();
    }

    @Bean
    @Order(InfoContributorAutoConfiguration.DEFAULT_ORDER)
    public KmcObservation kmcObservation(ObservationRegistry observationRegistry) {
        return new KmcObservation(observationRegistry);
    }
}
