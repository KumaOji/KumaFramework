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

package com.kuma.boot.application;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;

/**
 * Myframework 通用启动基类
 * 业务项目继承此类，添加 @SpringBootApplication 并调用 run() 即可
 *
 * <p>示例：
 * <pre>
 * &#64;SpringBootApplication(scanBasePackages = {"com.kuma.boot", "cn.kuma.xxx"})
 * &#64;ConfigurationPropertiesScan(basePackages = {"com.kuma.boot", "cn.kuma.xxx"})
 * public class XxxApplication extends Application {
 *     public static void main(String[] args) {
 *         run(XxxApplication.class, args);
 *     }
 * }
 * </pre>
 *
 * @author kuma
 */
/***
 *  *   _  ___   _ __  __    _          ____ _     ___  _   _ ____
 *  *  | |/ / | | |  \/  |  / \        / ___| |   / _ \| | | |  _ \
 *  *  | ' /| | | | |\/| | / _ \      | |   | |  | | | | | | | | | |
 *  *  | . \| |_| | |  | |/ ___ \     | |___| |__| |_| | |_| | |_| |
 *  *  |_|\_\\___/|_|  |_/_/   \_\     \____|_____\___/ \___/|____/
 *  *
 *  */
public abstract class Application {

    protected static final Logger log = LoggerFactory.getLogger(Application.class);

    /**
     * 启动 Spring Boot 应用
     *
     * @param source 启动类（子类自身）
     * @param args   启动参数
     */
    protected static void run(Class<?> source, String[] args) {
        log.info("应用启动中...");
        SpringApplication app = new SpringApplication(source);
        app.setRegisterShutdownHook(true);
        app.run(args);
        log.info("应用启动完成");
    }
}
