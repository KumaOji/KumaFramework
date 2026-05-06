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

package com.kuma.cloud.blog;

import com.alibaba.druid.spring.boot3.autoconfigure.DruidDataSourceAutoConfigure;
import com.kuma.boot.core.startup.StartupSpringApplication;
import com.kuma.boot.web.annotation.KumaBootApplication;
import com.kuma.cloud.blog.aot.BlogRuntimeHintsRegistrar;
import com.kuma.cloud.bootstrap.annotation.KumaCloudApplication;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ImportRuntimeHints;

@KumaBootApplication
@KumaCloudApplication
@ImportRuntimeHints(BlogRuntimeHintsRegistrar.class)
@MapperScan("com.kuma.cloud.blog.mapper")
@ComponentScan(basePackages = {"com.kuma.boot", "com.kuma.cloud.blog"})
@EnableAutoConfiguration(exclude = DruidDataSourceAutoConfigure.class)
@ConfigurationPropertiesScan(basePackages = {"com.kuma.boot", "com.kuma.cloud.blog"})
public class BlogApplication {

    public static void main(String[] args) {
        new StartupSpringApplication(BlogApplication.class)
                .setKmcBanner()
                .setKmcProfileIfNotExists("dev")
                .setKmcApplicationProperty("kuma-cloud-blog")
                .setKmcAllowBeanDefinitionOverriding(true)
                .run(args);
    }
}
