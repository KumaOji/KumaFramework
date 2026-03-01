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

package com.kuma.boot.web.request.configuration;

import com.kuma.boot.common.constant.StarterNameConstants;
import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.boot.web.request.aspect.RequestLoggerAspect;
import com.kuma.boot.web.request.listener.RequestLoggerListener;
import com.kuma.boot.web.request.properties.RequestLoggerProperties;
import com.kuma.boot.web.request.service.RequestLoggerService;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

import java.util.List;

/**
 * 当web项目引入此依赖时，自动配置对应的内容 初始化log的事件监听与切面配置
 *
 * @author kuma
 * @version 2022.03
 * @since 2020/4/30 10:21
 */
@AutoConfiguration(
        after = {
                com.kuma.boot.web.request.configuration.RedisRequestLoggerConfiguration.class,
                com.kuma.boot.web.request.configuration.KafkaRequestLoggerConfiguration.class,
                com.kuma.boot.web.request.configuration.LoggerRequestLoggerConfiguration.class
        })
@EnableConfigurationProperties({RequestLoggerProperties.class})
@ConditionalOnProperty(
        prefix = RequestLoggerProperties.PREFIX,
        name = "enabled",
        havingValue = "true")
public class RequestLoggerConfiguration implements InitializingBean {

    @Override
    public void afterPropertiesSet() throws Exception {
        LogUtils.started(RequestLoggerConfiguration.class, StarterNameConstants.LOGGER_STARTER);
    }

    @Bean
    public RequestLoggerListener requestLoggerListener(
            List<RequestLoggerService> requestLoggerServices) {
        return new RequestLoggerListener(requestLoggerServices);
    }

    @Bean
    public RequestLoggerAspect requestLoggerAspect() {
        return new RequestLoggerAspect();
    }
}
