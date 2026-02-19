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

package com.kuma.boot.core.autoconfigure;

import com.kuma.boot.common.support.property.YamlPropertySourceFactory;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.PropertySource;

/**
 * gradle配置
 *
 * @author kuma
 * @version 2022.03
 * @since 2021/06/17 17:21
 */
@AutoConfiguration
@PropertySource(
        ignoreResourceNotFound = true,
        encoding = "UTF8",
        value = {"classpath:gradle/gradle.properties"})
@PropertySource(
        ignoreResourceNotFound = true,
        encoding = "UTF8",
        value = {"classpath:gradle/gradle.yml"},
        factory = YamlPropertySourceFactory.class)
public class GradleAutoConfiguration {

}
