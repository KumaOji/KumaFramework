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

package com.kuma.boot.spring.config;

import com.kuma.boot.common.util.ObjectId;
import com.kuma.boot.spring.web.pageable.DefaultPageParamArgumentResolver;
import com.kuma.boot.spring.web.pageable.PageParamArgumentResolver;
import com.kuma.boot.spring.web.trace.TraceIdFilter;
import com.kuma.boot.spring.web.trace.TraceIdGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

/**
 * Web MVC 配置
 *
 * @author kuma
 */
@Configuration
@RequiredArgsConstructor
@EnableConfigurationProperties({WebProperties.class, PageableProperties.class})
public class WebMvcConfiguration {

    private final WebProperties webProperties;

    @Bean
    @ConditionalOnMissingBean
    public PageParamArgumentResolver pageParamArgumentResolver(PageableProperties pageableProperties) {
        return new DefaultPageParamArgumentResolver(pageableProperties.getMaxPageSize(),
                pageableProperties.getPageParameterName(), pageableProperties.getSizeParameterName(),
                pageableProperties.getSortParameterName());
    }

    /**
     * 允许聚合者对提供者的文档进行跨域访问 解决聚合文档导致的跨域问题
     * @return FilterRegistrationBean
     */
    @Bean
    @ConditionalOnProperty(prefix = WebProperties.PREFIX + ".cors-config", name = "enabled", havingValue = "true")
    public FilterRegistrationBean<CorsFilter> corsFilterRegistrationBean() {
        WebProperties.CorsConfig corsConfig = this.webProperties.getCorsConfig();
        CorsConfiguration corsConfiguration = getCorsConfiguration(corsConfig);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration(corsConfig.getUrlPattern(), corsConfiguration);
        FilterRegistrationBean<CorsFilter> bean = new FilterRegistrationBean<>(new CorsFilter(source));
        bean.setOrder(Ordered.HIGHEST_PRECEDENCE + 1000);
        return bean;
    }

    private static CorsConfiguration getCorsConfiguration(WebProperties.CorsConfig corsConfig) {
        CorsConfiguration corsConfiguration = new CorsConfiguration();
        corsConfiguration.setAllowedOrigins(corsConfig.getAllowedOrigins());
        corsConfiguration.setAllowedOriginPatterns(corsConfig.getAllowedOriginPatterns());
        corsConfiguration.setAllowedMethods(corsConfig.getAllowedMethods());
        corsConfiguration.setAllowedHeaders(corsConfig.getAllowedHeaders());
        corsConfiguration.setExposedHeaders(corsConfig.getExposedHeaders());
        corsConfiguration.setAllowCredentials(corsConfig.getAllowCredentials());
        corsConfiguration.setMaxAge(corsConfig.getMaxAge());
        return corsConfiguration;
    }

    @Configuration(proxyBeanMethods = false)
    static class CustomWebMvcConfigurer implements WebMvcConfigurer {

        private final PageParamArgumentResolver pageParamArgumentResolver;

        CustomWebMvcConfigurer(PageParamArgumentResolver pageParamArgumentResolver) {
            this.pageParamArgumentResolver = pageParamArgumentResolver;
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

    @Configuration(proxyBeanMethods = false)
    static class TraceIdConfiguration {

        @Bean
        @ConditionalOnMissingBean(TraceIdGenerator.class)
        public TraceIdGenerator traceIdGenerator() {
            return () -> ObjectId.get().toString();
        }

        @Bean
        public FilterRegistrationBean<TraceIdFilter> traceIdFilterRegistrationBean(WebProperties webProperties, TraceIdGenerator traceIdGenerator) {
            String traceIdHeaderName = webProperties.getTraceIdHeaderName();
            TraceIdFilter traceIdFilter = new TraceIdFilter(traceIdHeaderName, traceIdGenerator);
            FilterRegistrationBean<TraceIdFilter> registrationBean = new FilterRegistrationBean<>(traceIdFilter);
            registrationBean.setOrder(Ordered.HIGHEST_PRECEDENCE);
            return registrationBean;
        }

    }

}
