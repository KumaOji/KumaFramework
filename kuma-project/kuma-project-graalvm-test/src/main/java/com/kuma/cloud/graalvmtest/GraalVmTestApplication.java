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

package com.kuma.cloud.graalvmtest;

import com.alibaba.druid.spring.boot3.autoconfigure.DruidDataSourceAutoConfigure;
import com.kuma.boot.core.startup.StartupSpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

/**
 * GraalVM-Test 轻量入口（未引入 {@code kuma-boot-starter-web} / {@code kuma-boot-starter-security-spring}，
 * 便于先验证 Native）；自动配置排除与 {@code application.yml} 中 {@code spring.autoconfigure.exclude} 对齐。
 */
@SpringBootApplication(exclude = {DruidDataSourceAutoConfigure.class})
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
