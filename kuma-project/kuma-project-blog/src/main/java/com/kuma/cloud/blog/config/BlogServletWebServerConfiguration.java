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

package com.kuma.cloud.blog.config;

import org.springframework.boot.tomcat.servlet.TomcatServletWebServerFactory;
import org.springframework.boot.web.server.servlet.ServletWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * GraalVM Native Image：AOT 处理时 {@code @ConditionalOnMissingBean(ServletWebServerFactory)} 可能对「占位 Bean」
 * 误判为已存在，从而不注册本方法，运行时反而没有可用工厂。通过组件扫描（非 {@code @Import}）注册本类；
 * {@code @Import} 与组件扫描并存时 AOT 代码生成存在冲突，导致 Bean 定义丢失。
 * {@code TomcatServletWebServerAutoConfiguration} 在 {@link com.kuma.cloud.blog.BlogApplication} 上已排除，避免双注册。
 */
@Configuration(proxyBeanMethods = false)
public class BlogServletWebServerConfiguration {

    @Bean
    public ServletWebServerFactory servletWebServerFactory() {
        return new TomcatServletWebServerFactory();
    }
}
