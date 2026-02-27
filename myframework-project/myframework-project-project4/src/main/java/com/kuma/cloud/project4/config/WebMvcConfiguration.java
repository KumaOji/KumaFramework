/*
 * Copyright 2023-2024 the original author or authors.
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

package com.kuma.cloud.project4.config;

import com.kuma.cloud.project4.web.pageable.DefaultPageParamArgumentResolver;
import com.kuma.cloud.project4.web.pageable.PageParamArgumentResolver;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.convert.converter.Converter;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.math.BigDecimal;
import java.util.List;

/**
 * Web MVC 配置
 * <p>TraceId、CORS 已由 myframework-module-web 提供，此处仅保留 project4 特有配置</p>
 *
 * @author kuma
 */
@Configuration
@Lazy
@EnableConfigurationProperties(PageableProperties.class)
public class WebMvcConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public PageParamArgumentResolver pageParamArgumentResolver(PageableProperties pageableProperties) {
        return new DefaultPageParamArgumentResolver(pageableProperties.getMaxPageSize(),
                pageableProperties.getPageParameterName(), pageableProperties.getSizeParameterName(),
                pageableProperties.getSortParameterName());
    }

    @Configuration(proxyBeanMethods = false)
    static class CustomWebMvcConfigurer implements WebMvcConfigurer {

        private final PageParamArgumentResolver pageParamArgumentResolver;

        CustomWebMvcConfigurer(PageParamArgumentResolver pageParamArgumentResolver) {
            this.pageParamArgumentResolver = pageParamArgumentResolver;
        }

        @Override
        public void addFormatters(FormatterRegistry registry) {
            registry.addConverter(new Converter<String, BigDecimal>() {
                @Override
                public BigDecimal convert(String source) {
                    if (source == null || source.isBlank()) {
                        return null;
                    }
                    try {
                        return new BigDecimal(source.trim());
                    } catch (NumberFormatException e) {
                        return null;
                    }
                }
            });
        }

        /**
         * Page Sql注入过滤
         * @param argumentResolvers 方法参数解析器集合
         */
        @Override
        public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
            argumentResolvers.add(this.pageParamArgumentResolver);
        }

        /**
         * 添加 Swagger UI 路径重定向
         * 将 /swagger 重定向到 /swagger-ui.html
         */
        @Override
        public void addViewControllers(ViewControllerRegistry registry) {
            registry.addRedirectViewController("/swagger", "/swagger-ui.html");
            registry.addRedirectViewController("/swagger/", "/swagger-ui.html");
            registry.addRedirectViewController("/doc", "/swagger-ui.html");
            registry.addRedirectViewController("/docs", "/swagger-ui.html");
            registry.addRedirectViewController("/api-doc", "/swagger-ui.html");
        }

    }

}
