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

import com.alibaba.cloud.nacos.NacosConfigManager;
import com.alibaba.nacos.api.config.ConfigType;
import com.alibaba.nacos.api.config.listener.Listener;
import com.kuma.boot.common.constant.StarterNameConstants;
import com.kuma.boot.common.utils.log.LogUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;

import java.util.Properties;
import java.util.concurrent.Executor;

/**
 * ConfigService
 *
 * @author kuma
 * @version 2022.03
 * @since 2021/04/06 11:20
 */
public class NacosConfigListener implements InitializingBean {

    @Override
    public void afterPropertiesSet() throws Exception {
        LogUtils.started(NacosConfigListener.class, StarterNameConstants.CORE_STARTER);
    }

    @com.alibaba.nacos.api.config.annotation.NacosConfigListener(dataId = "kuma-cloud", type = ConfigType.YAML)
    public void onReceived(Properties value) {
        LogUtils.info("kuma cloud on received from nacos properties data : {}", value);
    }

    @Configuration
    @ConditionalOnProperty(
            prefix = "spring.cloud.nacos.config",
            value = "enabled",
            havingValue = "true",
            matchIfMissing = true)
    public static class KmcNacosConfigListener implements InitializingBean {

        //@Value("${spring.application.name}")
        //private String appName;

        private final NacosConfigManager nacosConfigManager;

        public KmcNacosConfigListener(NacosConfigManager nacosConfigManager) {
            this.nacosConfigManager = nacosConfigManager;
        }

        @Override
        public void afterPropertiesSet() throws Exception {
            nacosConfigManager.getConfigService().addListener("test", "DEFAULT_GROUP", new Listener() {
                @Override
                public Executor getExecutor() {
                    return null;
                }

                @Override
                public void receiveConfigInfo(String configInfo) {
                    LogUtils.info("kuma cloud on received from nacos config info : {}", configInfo);
                }
            });
        }
    }
}
