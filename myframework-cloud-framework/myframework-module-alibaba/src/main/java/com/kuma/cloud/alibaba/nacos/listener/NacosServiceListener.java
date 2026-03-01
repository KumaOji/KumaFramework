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

package com.kuma.cloud.alibaba.nacos.listener;

import com.alibaba.cloud.nacos.NacosDiscoveryProperties;
import com.alibaba.cloud.nacos.NacosServiceManager;
import com.alibaba.cloud.nacos.discovery.NacosDiscoveryClient;
import com.alibaba.nacos.api.naming.listener.NamingEvent;
import com.alibaba.nacos.api.naming.pojo.Instance;
import com.kuma.boot.common.constant.StarterNameConstants;
import com.kuma.boot.common.utils.log.LogUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.annotation.Configuration;

import java.util.Collections;
import java.util.List;

/**
 * ConfigService
 *
 * @author kuma
 * @version 2022.03
 * @since 2021/04/06 11:20
 */
public class NacosServiceListener implements InitializingBean {

    @Override
    public void afterPropertiesSet() throws Exception {
        LogUtils.started(NacosServiceListener.class, StarterNameConstants.CORE_STARTER);
    }

    @Configuration
    public static class KmcNacosServiceListener implements InitializingBean {

        private final NacosServiceManager nacosServiceManager;

        private final NacosDiscoveryProperties properties;

        private final NacosDiscoveryClient discoveryClient;

        public KmcNacosServiceListener(NacosServiceManager nacosServiceManager,
                                       NacosDiscoveryProperties properties, NacosDiscoveryClient discoveryClient) {
            this.nacosServiceManager = nacosServiceManager;
            this.properties = properties;
            this.discoveryClient = discoveryClient;
        }

        @Override
        public void afterPropertiesSet() throws Exception {
            nacosServiceManager
                    .getNamingService()
                    .subscribe(
                            properties.getService(),
                            properties.getGroup(),
                            Collections.singletonList(properties.getClusterName()),
                            event -> {
                                if (event instanceof NamingEvent) {
                                    List<Instance> instances = ((NamingEvent) event).getInstances();

                                    LogUtils.info("");

                                    // Optional instanceOptional =
                                    // NacosWatch.this.selectCurrentInstance(instances);
                                    // instanceOptional.ifPresent((currentInstance) -> {
                                    //	NacosWatch.this.resetIfNeeded(currentInstance);
                                    // });
                                }
                            });

            List<String> services = discoveryClient.getServices();
            if (!services.isEmpty()) {
                for (String service : services) {
                    nacosServiceManager
                            .getNamingService()
                            .subscribe(
                                    service,
                                    this.properties.getGroup(),
                                    List.of(this.properties.getClusterName()),
                                    event -> {
                                        if (event instanceof NamingEvent) {
                                            List<Instance> instances = ((NamingEvent) event).getInstances();

                                            LogUtils.info("");

                                            // Optional instanceOptional =
                                            // NacosWatch.this.selectCurrentInstance(instances);
                                            // instanceOptional.ifPresent((currentInstance) -> {
                                            //	NacosWatch.this.resetIfNeeded(currentInstance);
                                            // });
                                        }
                                    });
                }
            }
        }
    }
}
