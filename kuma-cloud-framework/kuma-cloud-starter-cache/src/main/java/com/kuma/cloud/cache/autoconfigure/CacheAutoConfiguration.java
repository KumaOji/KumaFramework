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

package com.kuma.cloud.cache.autoconfigure;

import com.kuma.cloud.cache.api.Cache;
import com.kuma.cloud.cache.api.CacheEvict;
import com.kuma.cloud.cache.api.CachePersist;
import com.kuma.cloud.cache.bs.CacheBs;
import com.kuma.cloud.cache.support.evict.CacheEvicts;
import com.kuma.cloud.cache.support.persist.CachePersists;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

/**
 * Cache Spring Boot 自动配置
 *
 * @author kuma
 * @since 2025.01
 */
@AutoConfiguration
@EnableConfigurationProperties(CacheProperties.class)
public class CacheAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean(Cache.class)
    public Cache<Object, Object> cache(CacheProperties properties) {
        return CacheBs.<Object, Object>newInstance()
                .size(properties.getSize())
                .evict(buildEvict(properties.getEvict()))
                .persist(buildPersist(properties.getPersist()))
                .build();
    }

    private CacheEvict<Object, Object> buildEvict(String evict) {
        return switch (evict.toLowerCase()) {
            case "none"                -> CacheEvicts.none();
            case "fifo"                -> CacheEvicts.fifo();
            case "lru-double-list-map" -> CacheEvicts.lruDoubleListMap();
            case "lru-linked-hash-map" -> CacheEvicts.lruLinkedHashMap();
            case "lru-2q"              -> CacheEvicts.lru2Q();
            case "lru-2"               -> CacheEvicts.lru2();
            case "lfu"                 -> CacheEvicts.lfu();
            case "clock"               -> CacheEvicts.clock();
            default                    -> CacheEvicts.lru();
        };
    }

    private CachePersist<Object, Object> buildPersist(CacheProperties.Persist persist) {
        return switch (persist.getType().toLowerCase()) {
            case "db-json" -> CachePersists.dbJson(persist.getPath());
            case "aof"     -> CachePersists.aof(persist.getPath());
            default        -> CachePersists.none();
        };
    }
}
