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

package com.kuma.boot.core.runtime.extension;

/**
 * 这个类，顾名思义，也是用来初始化bean的。InitializingBean接口为bean提供了初始化方法的方式， 它只包括afterPropertiesSet方法，凡是继承该接口的类，
 * 在初始化bean的时候都会执行该方法。这个扩展点的触发时机在postProcessAfterInitialization之前。
 *
 * 使用场景：用户实现此接口，来进行系统启动的时候一些业务指标的初始化工作。
 */

import com.kuma.boot.common.utils.log.LogUtils;
import org.springframework.beans.factory.InitializingBean;

/**
 * Test10NormalBeanInitializingBean
 *
 * @author kuma
 * @version 2026.01
 * @since 2025-12-19 09:30:45
 */
public class Test10NormalBeanInitializingBean implements InitializingBean {

    @Override
    public void afterPropertiesSet() throws Exception {
        LogUtils.info("[InitializingBean] NormalBeanA");
    }
}
