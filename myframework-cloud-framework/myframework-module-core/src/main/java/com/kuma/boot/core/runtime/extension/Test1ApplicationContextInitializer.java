/*
 * Copyright (c) 2020-2030, Shuigedeng (2569277704@qq.com & https://blog.kumacloud.top/).
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

package com.kuma.boot.core.runtime.extension;

import com.kuma.boot.common.utils.log.LogUtils;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * 这是整个spring容器在刷新之前初始化ConfigurableApplicationContext的回调接口，
 * 简单来说，就是在容器刷新之前调用此类的initialize方法。
 * 这个点允许被用户自己扩展。用户可以在整个spring容器还没被初始化之前做一些事情。
 *
 * 可以想到的场景可能为，在最开始激活一些配置，或者利用这时候class还没被类加载器加载的时机，进行动态字节码注入等操作。
 *
 * 在启动类中用springApplication.addInitializers(new TestApplicationContextInitializer())语句加入
 * 配置文件配置context.initializer.classes=com.example.demo.TestApplicationContextInitializer
 * Spring SPI扩展，在spring.factories中加入org.springframework.context.ApplicationContextInitializer=com.example.demo.TestApplicationContextInitializer
 */
public class Test1ApplicationContextInitializer implements ApplicationContextInitializer {
    @Override
    public void initialize(ConfigurableApplicationContext applicationContext) {
        LogUtils.info("1--------[ApplicationContextInitializer]");
    }
}
