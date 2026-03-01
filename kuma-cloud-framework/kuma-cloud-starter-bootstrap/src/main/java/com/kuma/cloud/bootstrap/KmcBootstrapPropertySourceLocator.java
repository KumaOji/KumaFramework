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

package com.kuma.cloud.bootstrap;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import org.springframework.cloud.bootstrap.config.PropertySourceLocator;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.CompositePropertySource;
import org.springframework.core.env.Environment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.PropertySource;

/**
 * 自定义 Bootstrap 配置属性源（实现 SpringCloud 的接口）
 *
 * @author kuma
 * @version 2022.09
 * @since 2023-01-05 14:13
 */
@Order(1993)
public class KmcBootstrapPropertySourceLocator implements PropertySourceLocator {

    @Override
    public PropertySource<?> locate(Environment environment) {

        Map<String, Object> property = new HashMap<>();
        CompositePropertySource composite = new CompositePropertySource("kmc");

        property.put("kmcProperty", "kmcProperty");
        composite.addPropertySource(new MapPropertySource("kmc", property));
        return composite;
    }

    @Override
    public Collection<PropertySource<?>> locateCollection(Environment environment) {
        return PropertySourceLocator.super.locateCollection(environment);
    }
}
