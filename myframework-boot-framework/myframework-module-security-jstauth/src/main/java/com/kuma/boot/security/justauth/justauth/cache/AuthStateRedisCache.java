/*
 * Copyright (c) 2020-2030, kuma (2569277704@qq.com & https://blog.kumacloud.top/).
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

package com.kuma.boot.security.justauth.justauth.cache;

import com.kuma.boot.security.justauth.justauth.JustAuthProperties;
import com.kuma.boot.security.justauth.justauth.enums.CacheKeyStrategy;
import java.time.Duration;
import java.util.concurrent.TimeUnit;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.util.StringUtils;

/**
 * auth state redis cache, 适用单机与分布式
 */
public class AuthStateRedisCache implements com.kuma.boot.security.justauth.justauth.cache.Auth2StateCache {

    private final StringRedisTemplate stringRedisTemplate;
    private final Duration timeout;
    private final String cacheKeyPrefix;

    public AuthStateRedisCache(JustAuthProperties justAuth, Object stringRedisTemplate) {
        this.stringRedisTemplate = (StringRedisTemplate) stringRedisTemplate;
        this.timeout = justAuth.getTimeout();
        this.cacheKeyPrefix = justAuth.getCacheKeyPrefix();
    }

    @Override
    public void cache(String key, String value) {
        stringRedisTemplate.opsForValue().set(parsingKey(key), value, this.timeout);
    }

    @Override
    public void cache(String key, String value, long timeout) {
        stringRedisTemplate
                .opsForValue()
                .set(parsingKey(key), value, timeout, TimeUnit.MILLISECONDS);
    }

    @Override
    public String get(String key) {
        return stringRedisTemplate.opsForValue().get(parsingKey(key));
    }

    @Override
    public boolean containsKey(String key) {
        return StringUtils.hasText(stringRedisTemplate.opsForValue().get(parsingKey(key)));
    }

    @Override
    public CacheKeyStrategy getCacheKeyStrategy() {
        return CacheKeyStrategy.UUID;
    }

    private String parsingKey(String key) {
        return this.cacheKeyPrefix + key;
    }
}
