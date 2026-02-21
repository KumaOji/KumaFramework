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

package com.kuma.boot.web.support.holidays.config;

import com.kuma.boot.web.support.holidays.core.HolidaysApi;
import com.kuma.boot.web.support.holidays.impl.HolidaysApiImpl;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ResourceLoader;

/**
 * 配置
 */
@AutoConfiguration
@EnableConfigurationProperties(HolidaysApiProperties.class)
public class HolidaysApiConfiguration {

    @Bean
    public HolidaysApi holidaysApi(
            ResourceLoader resourceLoader, HolidaysApiProperties properties) {
        return new HolidaysApiImpl(resourceLoader, properties);
    }
}
