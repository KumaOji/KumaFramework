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

package com.kuma.cloud.project4;

import com.alibaba.druid.spring.boot3.autoconfigure.DruidDataSourceAutoConfigure;
import com.kuma.boot.core.startup.StartupSpringApplication;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

/**
 * Project4 启动类
 * <p>project4 无 Mapper 时，仅扫描 com.kuma.boot 以消除 MyBatis 警告</p>
 *
 * @author kuma
 */
@SpringBootApplication(
        scanBasePackages = {"com.kuma.boot", "com.kuma.cloud.project4"},
        exclude = DruidDataSourceAutoConfigure.class)
@ConfigurationPropertiesScan(basePackages = {"com.kuma.boot", "com.kuma.cloud.project4"})
@MapperScan(basePackages = {"com.kuma.boot.mybatis.mapper", "com.kuma.boot.data.mybatis.delay", "com.kuma.cloud.project4.mapper"})
public class Project4Application extends SpringBootServletInitializer {

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
        return builder.sources(Project4Application.class);
    }

    public static void main(String[] args) {
        new StartupSpringApplication(Project4Application.class)
                .setKmcBanner()
                .setKmcProfileIfNotExists("dev")
                .setKmcApplicationProperty("kuma-cloud-project")
                .setKmcAllowBeanDefinitionOverriding(true)
                .run(args);
    }

//    public static void main(String[] args) {
//        run(Project1Application.class, args);
//    }
}
