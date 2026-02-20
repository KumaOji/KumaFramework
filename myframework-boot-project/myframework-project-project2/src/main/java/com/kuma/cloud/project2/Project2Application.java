/*
 * Copyright (c) 2020-2030, Kuma (2569277704@qq.com & https://blog.kumacloud.top/).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use it except in compliance with the License.
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

package com.kuma.cloud.project2;

import com.alibaba.druid.spring.boot3.autoconfigure.DruidDataSourceAutoConfigure;
import com.kuma.boot.application.Application;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

/**
 * Project2 启动类 - data-mybatis 简单使用展示
 * <p>演示 MpSuperMapper、MpSuperEntity 等 data-mybatis 模块能力</p>
 * <p>排除 Druid 自动配置：Druid 1.2.27 与 Spring Boot 4 不兼容（DataSourceProperties 包路径变更），改用 HikariCP</p>
 *
 * @author kuma
 */
@SpringBootApplication(
        scanBasePackages = {"com.kuma.boot", "com.kuma.cloud.project2"},
        exclude = DruidDataSourceAutoConfigure.class)
@ConfigurationPropertiesScan(basePackages = {"com.kuma.boot", "com.kuma.cloud.project2"})
@MapperScan(basePackages = {"com.kuma.boot.mybatis.mapper", "com.kuma.cloud.project2.mapper"})
public class Project2Application extends Application {

    public static void main(String[] args) {
        run(Project2Application.class, args);
    }
}
