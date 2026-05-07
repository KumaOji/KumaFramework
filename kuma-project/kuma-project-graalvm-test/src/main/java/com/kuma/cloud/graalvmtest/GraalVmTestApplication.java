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

import com.kuma.boot.core.startup.StartupSpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * GraalVM-Test 入口。
 *
 * <p>KMC Banner：{@link StartupSpringApplication#setKmcBanner()} 与 core 中
 * {@link StartupSpringApplication#DEFAULT_BANNER_LOCATION}（classpath:banner/kmc-banner.txt）对齐；
 * {@code application.yml} / {@code application-dev.yml} 的 {@code spring.banner.location} 须保持一致。</p>
 */
@SpringBootApplication
public class GraalVmTestApplication {

    public static void main(String[] args) {
        // 与 BlogApplication 一致：在任何 Spring 初始化之前设置，避免 Bootstrap 父上下文优先于主上下文（AOT/Cloud 场景）
        System.setProperty("spring.cloud.bootstrap.enabled", "false");
        new StartupSpringApplication(GraalVmTestApplication.class)
                .setKmcBanner()
                .setKmcProfileIfNotExists("dev")
                .setKmcApplicationProperty("kuma-cloud-graalvm-test")
                .setKmcAllowBeanDefinitionOverriding(true)
                .run(args);
    }
}
