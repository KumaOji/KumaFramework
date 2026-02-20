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

package com.kuma.boot.spring.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

/**
 * Caffeine 缓存配置属性
 *
 * @author kuma
 */
@Data
@ConfigurationProperties(prefix = "kuma.cache.caffeine")
public class CaffeineCacheProperties {

    /**
     * 缓存名称列表
     */
    private List<String> cacheNames = List.of("default");

    /**
     * 初始容量
     */
    private int initialCapacity = 100;

    /**
     * 最大容量
     */
    private long maximumSize = 1000;

    /**
     * 写入后过期时间（分钟）
     */
    private long expireAfterWriteMinutes = 30;

    /**
     * 访问后过期时间（分钟）
     */
    private long expireAfterAccessMinutes = 30;
}
