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

package com.kuma.cloud.project1;

import com.kuma.boot.application.Application;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

/**
 * Project1 启动类
 * <p>project1 无 Mapper 时，仅扫描 com.kuma.boot 以消除 MyBatis 警告</p>
 *
 * @author kuma
 */
@SpringBootApplication(scanBasePackages = {"com.kuma.boot", "com.kuma.cloud.project1"})
@ConfigurationPropertiesScan(basePackages = {"com.kuma.boot", "com.kuma.cloud.project1"})
@MapperScan(basePackages = "com.kuma.boot.mybatis.mapper")
public class Project1Application extends Application {

    public static void main(String[] args) {
        run(Project1Application.class, args);
    }
}
