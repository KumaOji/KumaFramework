/*
 * Copyright (c) 2020-2030, Kuma (2569277704@qq.com & https://blog.kumacloud.top/).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 */

package com.kuma.cloud.graalvmtest.config;

import org.springframework.boot.tomcat.servlet.TomcatServletWebServerFactory;
import org.springframework.boot.web.server.servlet.ServletWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Native / AOT：入口排除 {@link org.springframework.boot.tomcat.servlet.TomcatServletWebServerAutoConfiguration}，
 * 在此显式提供 {@link TomcatServletWebServerFactory}（与 Blog 同一策略）。
 */
@Configuration(proxyBeanMethods = false)
public class GraalVmServletWebServerConfiguration {

    @Bean
    public ServletWebServerFactory servletWebServerFactory() {
        return new TomcatServletWebServerFactory();
    }
}
