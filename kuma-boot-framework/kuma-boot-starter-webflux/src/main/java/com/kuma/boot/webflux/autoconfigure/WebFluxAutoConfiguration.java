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

package com.kuma.boot.webflux.autoconfigure;

import com.kuma.boot.common.constant.StarterNameConstants;
import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.boot.webflux.autoconfigure.properties.WebFluxProperties;
import com.kuma.boot.webflux.filter.TraceMdcWebFilter;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.http.codec.json.Jackson2JsonDecoder;
import org.springframework.http.codec.json.Jackson2JsonEncoder;
import org.springframework.lang.NonNull;
import org.springframework.web.reactive.config.CorsRegistry;
import org.springframework.web.reactive.config.ResourceHandlerRegistry;
import org.springframework.web.reactive.config.WebFluxConfigurer;

/**
 * WebFlux 核心自动配置
 *
 * <p>提供 CORS、Codec、静态资源等通用 WebFlux 增强配置，仅在 REACTIVE Web 环境下激活。
 *
 * @author kuma
 */
@AutoConfiguration
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.REACTIVE)
@EnableConfigurationProperties(WebFluxProperties.class)
public class WebFluxAutoConfiguration implements WebFluxConfigurer, InitializingBean {

    private final WebFluxProperties properties;

    public WebFluxAutoConfiguration(WebFluxProperties properties) {
        this.properties = properties;
    }

    @Override
    public void afterPropertiesSet() {
        LogUtils.started(WebFluxAutoConfiguration.class, StarterNameConstants.WEBFLUX_STARTER);
    }

    // ── CORS ─────────────────────────────────────────────────────────────────

    @Override
    public void addCorsMappings(@NonNull CorsRegistry registry) {
        if (!properties.getCors().isEnabled()) {
            return;
        }
        registry.addMapping(properties.getCors().getMapping())
                .allowedOriginPatterns("*")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH")
                .allowedHeaders("*")
                .exposedHeaders("*")
                .allowCredentials(true)
                .maxAge(properties.getCors().getMaxAge());
    }

    // ── Codec ─────────────────────────────────────────────────────────────────

    @Override
    public void configureHttpMessageCodecs(@NonNull ServerCodecConfigurer configurer) {
        configurer.defaultCodecs().jackson2JsonEncoder(new Jackson2JsonEncoder());
        configurer.defaultCodecs().jackson2JsonDecoder(new Jackson2JsonDecoder());
        // 允许最大 100 MB 的请求体，防止 DataBufferLimitException
        configurer.defaultCodecs().maxInMemorySize(100 * 1024 * 1024);
    }

    // ── 静态资源 ─────────────────────────────────────────────────────────────

    @Override
    public void addResourceHandlers(@NonNull ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/**")
                .addResourceLocations("classpath:/static/");
    }

    // ── Beans ─────────────────────────────────────────────────────────────────

    @Bean
    @ConditionalOnProperty(
            prefix = WebFluxProperties.PREFIX + ".trace",
            name = "enabled",
            havingValue = "true",
            matchIfMissing = true)
    public TraceMdcWebFilter traceMdcWebFilter() {
        return new TraceMdcWebFilter();
    }
}
