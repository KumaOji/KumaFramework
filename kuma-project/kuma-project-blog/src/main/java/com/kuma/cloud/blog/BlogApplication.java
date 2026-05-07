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
import org.springframework.boot.security.oauth2.client.autoconfigure.OAuth2ClientAutoConfiguration;
import org.springframework.boot.tomcat.autoconfigure.servlet.TomcatServletWebServerAutoConfiguration;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ImportRuntimeHints;


@KumaBootApplication(
        exclude = {
            DruidDataSourceAutoConfigure.class,
            TomcatServletWebServerAutoConfiguration.class,
            // 排除 OAuth2 Client：与 @SpringBootApplication 的 exclude 必须经 KumaBootApplication @AliasFor 合并，单独的
            // @EnableAutoConfiguration 不会替换元注解中的自动配置，Native 仍加载 Client 并在早期触发 MapperFactoryBean / Class<?> 错误
            OAuth2ClientAutoConfiguration.class,
        })
@KumaCloudApplication
@ImportRuntimeHints(BlogRuntimeHintsRegistrar.class)
@MapperScan("com.kuma.cloud.blog.mapper")
@ComponentScan(basePackages = {"com.kuma.boot", "com.kuma.cloud.blog"})
@ConfigurationPropertiesScan(basePackages = {"com.kuma.boot", "com.kuma.cloud.blog"})
public class BlogApplication {

    public static void main(String[] args) {
        // System property must be set before any Spring initialization so that
        // BootstrapApplicationListener (which fires at APPLICATION_ENVIRONMENT_PREPARED,
        // before application.yml is loaded) sees it and skips Bootstrap context creation.
        // Without this, AOT processAot captures only the Bootstrap parent context beans
        // instead of the full main ApplicationContext bean definitions.
        System.setProperty("spring.cloud.bootstrap.enabled", "false");
        new StartupSpringApplication(BlogApplication.class)
                .setKmcBanner()
                .setKmcProfileIfNotExists("dev")
                .setKmcApplicationProperty("kuma-cloud-blog")
                .setKmcAllowBeanDefinitionOverriding(true)
                .run(args);
    }
}
