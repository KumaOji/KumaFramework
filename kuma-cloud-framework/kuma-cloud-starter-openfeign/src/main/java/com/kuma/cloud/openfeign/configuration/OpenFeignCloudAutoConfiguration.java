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

package com.kuma.cloud.openfeign.configuration;

import com.kuma.boot.common.constant.StarterNameConstants;
import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.cloud.openfeign.interceptor.FeignHeaderPropagationInterceptor;
import com.kuma.cloud.openfeign.properties.OpenFeignCloudProperties;
import feign.Feign;
import feign.Logger;
import feign.Request;
import feign.RequestInterceptor;
import java.util.concurrent.TimeUnit;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

/**
 * OpenFeign 声明式调用自动配置.
 *
 * <p>提供统一的超时、日志级别默认值，以及微服务调用链的请求头透传能力。
 * 业务应用仍需在启动类上标注 {@code @EnableFeignClients} 以扫描 Feign 客户端接口。
 *
 * @author kuma
 */
@AutoConfiguration
@EnableConfigurationProperties(OpenFeignCloudProperties.class)
@ConditionalOnClass(Feign.class)
@ConditionalOnProperty(
        prefix = OpenFeignCloudProperties.PREFIX,
        name = "enabled",
        havingValue = "true",
        matchIfMissing = true)
public class OpenFeignCloudAutoConfiguration implements InitializingBean {

    /**
     * 统一的连接 / 读超时配置.
     */
    @Bean
    @ConditionalOnMissingBean
    public Request.Options feignRequestOptions(OpenFeignCloudProperties properties) {
        return new Request.Options(
                properties.getConnectTimeout(), TimeUnit.MILLISECONDS,
                properties.getReadTimeout(), TimeUnit.MILLISECONDS,
                properties.isFollowRedirects());
    }

    /**
     * Feign 日志级别（需对应 logger 开到 DEBUG 才会真正输出）.
     */
    @Bean
    @ConditionalOnMissingBean
    public Logger.Level feignLoggerLevel(OpenFeignCloudProperties properties) {
        return switch (properties.getLoggerLevel()) {
            case BASIC -> Logger.Level.BASIC;
            case HEADERS -> Logger.Level.HEADERS;
            case FULL -> Logger.Level.FULL;
            default -> Logger.Level.NONE;
        };
    }

    /**
     * 请求头透传拦截器：仅在 Web 应用且开启透传时注册.
     */
    @Bean
    @ConditionalOnWebApplication
    @ConditionalOnMissingBean(FeignHeaderPropagationInterceptor.class)
    @ConditionalOnProperty(
            prefix = OpenFeignCloudProperties.PREFIX + ".propagation",
            name = "enabled",
            havingValue = "true",
            matchIfMissing = true)
    public RequestInterceptor feignHeaderPropagationInterceptor(OpenFeignCloudProperties properties) {
        return new FeignHeaderPropagationInterceptor(properties);
    }

    @Override
    public void afterPropertiesSet() {
        LogUtils.started(OpenFeignCloudAutoConfiguration.class, StarterNameConstants.OPENFEIGN_CLOUD_STARTER);
    }
}
