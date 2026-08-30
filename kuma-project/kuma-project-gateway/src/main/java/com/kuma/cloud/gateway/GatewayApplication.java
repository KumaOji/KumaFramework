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

package com.kuma.cloud.gateway;

import com.kuma.boot.core.startup.StartupSpringApplication;
import com.kuma.cloud.bootstrap.annotation.KumaCloudApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.ComponentScan;

/**
 * Kuma 体系统一网关启动类。
 *
 * <p>基于 Spring Cloud Gateway（WebFlux），作为 uaa / blog 等业务应用的统一流量入口。
 *
 * <p>框架能力：{@code kuma-boot-starter-prometheus}（Actuator / KMC 端点）、
 * {@code kuma-boot-starter-logger}（统一 Logback）、{@code kuma-boot-starter-ip2region}（访问日志归属地）、
 * {@code kuma-cloud-starter-bootstrap}（云应用引导）。
 *
 * @author kuma
 */
@SpringBootApplication
@KumaCloudApplication
@ComponentScan(basePackages = {"com.kuma.boot", "com.kuma.cloud.gateway"})
@ConfigurationPropertiesScan(basePackages = {"com.kuma.boot", "com.kuma.cloud.gateway"})
public class GatewayApplication {

    static void main(String[] args) {
        new StartupSpringApplication(GatewayApplication.class)
                .setKmcBanner()
                .setKmcProfileIfNotExists("dev")
                .setKmcApplicationProperty("kuma-cloud-gateway")
                .setKmcAllowBeanDefinitionOverriding(true)
                .run(args);
    }
}
