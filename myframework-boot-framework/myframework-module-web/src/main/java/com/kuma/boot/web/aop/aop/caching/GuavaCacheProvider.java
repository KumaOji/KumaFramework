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

package com.kuma.boot.web.aop.aop.caching;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * 缓存的提供者
 */
public class GuavaCacheProvider implements com.kuma.boot.web.aop.aop.caching.CacheProvider {

    private static final ConcurrentHashMap<Long, Cache<String, Optional<Object>>>
            expireMillisToCache = new ConcurrentHashMap<>();

    @Override
    public <T> void put(String key, T value, long expireMillis) {
        Cache<String, Optional<Object>> cache = expireMillisToCache.get(expireMillis);
        if (cache == null) {
            cache =
                    expireMillisToCache.computeIfAbsent(
                            expireMillis,
                            k ->
                                    CacheBuilder.newBuilder()
                                            .expireAfterWrite(k, TimeUnit.MILLISECONDS)
                                            .build());
        }
        cache.put(key, Optional.ofNullable(value));
    }

    @Override
    public <T> T get(String key, long expireMillis) {
        Cache<String, Optional<Object>> cache = expireMillisToCache.get(expireMillis);
        if (cache == null) {
            return null;
        }
        Optional<Object> optional = cache.getIfPresent(key);
        if (optional == null || optional.isEmpty()) {
            return null;
        }
        return (T) optional.get();
    }
}
