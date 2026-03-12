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

package com.kuma.boot.cache.caffeine.model;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.time.Duration;
import java.util.Objects;

/**
 * 缓存 key，包含 key 字符串和可选的过期时间。
 *
 * @author kuma
 * @since 2021-09-07
 */
public class CacheKey {

    @NonNull
    private final String key;

    @Nullable
    private final Duration expire;

    public CacheKey(@NonNull String key) {
        this.key = key;
        this.expire = null;
    }

    public CacheKey(@NonNull String key, @Nullable Duration expire) {
        this.key = key;
        this.expire = expire;
    }

    @NonNull
    public String getKey() {
        return key;
    }

    @Nullable
    public Duration getExpire() {
        return expire;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        CacheKey cacheKey = (CacheKey) o;
        return key.equals(cacheKey.key) && Objects.equals(expire, cacheKey.expire);
    }

    @Override
    public int hashCode() {
        return Objects.hash(key, expire);
    }

    @Override
    public String toString() {
        return "CacheKey{key='" + key + "', expire=" + expire + '}';
    }
}
