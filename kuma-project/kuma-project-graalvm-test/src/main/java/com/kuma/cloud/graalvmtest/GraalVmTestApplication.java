/*
 * Copyright (c) 2020-2030, Kuma (2569277704@qq.com & https://blog.kumacloud.top/).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 */

package com.kuma.cloud.graalvmtest;

import com.alibaba.druid.spring.boot3.autoconfigure.DruidDataSourceAutoConfigure;
import com.kuma.boot.core.startup.StartupSpringApplication;
import com.kuma.boot.web.annotation.KumaBootApplication;
import com.kuma.cloud.graalvmtest.aot.GraalVmRuntimeHintsRegistrar;
import org.springframework.boot.tomcat.autoconfigure.servlet.TomcatServletWebServerAutoConfiguration;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScan.Filter;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.ImportRuntimeHints;

/**
 * GraalVM-Test：已引入 {@code kuma-boot-starter-web} / {@code kuma-boot-starter-totp}，与 Blog 同属
 * {@link KumaBootApplication} + Druid/Tomcat 自动配置排除 + 手动 Servlet 工厂；Native 详见
 * {@link GraalVmRuntimeHintsRegistrar} 与 {@link com.kuma.cloud.graalvmtest.config.GraalVmServletWebServerConfiguration}。
 */
@KumaBootApplication(
        exclude = {DruidDataSourceAutoConfigure.class, TomcatServletWebServerAutoConfiguration.class})
@ImportRuntimeHints(GraalVmRuntimeHintsRegistrar.class)
@ComponentScan(
        basePackages = {"com.kuma.boot", "com.kuma.cloud.graalvmtest"},
        excludeFilters =
                @Filter(type = FilterType.CUSTOM, classes = ExcludeOAuth2ClientManagerTypeFilter.class))
@ConfigurationPropertiesScan(basePackages = {"com.kuma.boot", "com.kuma.cloud.graalvmtest"})
public class GraalVmTestApplication {

    public static void main(String[] args) {
        System.setProperty("spring.cloud.bootstrap.enabled", "false");
        new StartupSpringApplication(GraalVmTestApplication.class)
                .setKmcBanner()
                .setKmcProfileIfNotExists("dev")
                .setKmcApplicationProperty("kuma-cloud-graalvm-test")
                .setKmcAllowBeanDefinitionOverriding(true)
                .run(args);
    }
}
