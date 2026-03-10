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

package com.kuma.cloud.job.worker.starter.autoConfig;

// import org.kjob.common.utils.NetUtils;

import com.kuma.cloud.job.worker.KmcJobSpringWorker;
import com.kuma.cloud.job.worker.common.KmcJobWorkerConfig;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * KmcJobAutoConfiguration
 *
 * @author kuma
 * @version 2026.02
 * @since 2025-12-19 09:30:45
 */
@Configuration
@EnableConfigurationProperties(KmcJobProperties.class)
@ConditionalOnProperty(
        prefix = "kmcjob.worker",
        name = "enabled",
        havingValue = "true",
        matchIfMissing = true)
public class KmcJobAutoConfiguration {

    @Autowired
    KmcJobProperties properties;

    @Bean
    @ConditionalOnMissingBean
    public KmcJobSpringWorker initKJobWorker() {

        KmcJobProperties.Worker worker = properties.getWorker();

        List<String> serverAddress = Arrays.asList(worker.getServerAddress().split(","));

        KmcJobWorkerConfig config = new KmcJobWorkerConfig();

        if (worker.getPort() != null) {
            config.setPort(worker.getPort());
        }
        if (worker.getServerPort() != null) {
            config.setServerPort(worker.getServerPort());
        }

        config.setAppName(worker.getAppName());
        config.setServerAddress(serverAddress);
        config.setNameServerAddress(worker.getNameServerAddress());
        config.setMaxHeavyweightTaskNum(worker.getMaxHeavyweightTaskNum());
        config.setMaxLightweightTaskNum(worker.getMaxLightweightTaskNum());
        config.setHealthReportInterval(worker.getHealthReportInterval());
        return new KmcJobSpringWorker(config);
    }
}
