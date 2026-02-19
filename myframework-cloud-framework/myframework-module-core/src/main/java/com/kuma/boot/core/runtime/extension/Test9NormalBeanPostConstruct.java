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

import com.kuma.boot.common.utils.log.LogUtils;
import jakarta.annotation.PostConstruct;

/**
 * 这个并不算一个扩展点，其实就是一个标注。其作用是在bean的初始化阶段，如果对一个方法标注了@PostConstruct，会先调用这个方法。这里重点是要关注下这个标准的触发点，这个触发点是在postProcessBeforeInitialization之后，InitializingBean.afterPropertiesSet之前。
 *
 * 使用场景：用户可以对某一方法进行标注，来进行初始化某一个属性
 */
public class Test9NormalBeanPostConstruct {
    public Test9NormalBeanPostConstruct() {
        LogUtils.info("NormalBean constructor");
    }

    @PostConstruct
    public void init() {
        LogUtils.info("[PostConstruct] NormalBeanA");
    }
}
