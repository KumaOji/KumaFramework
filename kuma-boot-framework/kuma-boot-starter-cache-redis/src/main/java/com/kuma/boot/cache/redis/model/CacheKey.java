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

package com.kuma.boot.cache.redis.model;

import java.time.Duration;
import java.util.Objects;
import org.jspecify.annotations.NonNull;

/**
 * CacheKey
 *
 * @author kuma
 * @version 2021.9
 * @since 2021-09-07 21:15:45
 */
public class CacheKey {

    /** redis key */
    private final String key;

    /** 超时时间 秒 */
    private final Duration expire;

    public CacheKey(final @NonNull String key) {
        this.key = key;
        this.expire = null;
    }

    public CacheKey(@NonNull String key, Duration expire) {
        this.key = key;
        this.expire = expire;
    }

    @Override
    public String toString() {
        return "CacheKey{" + "key='" + key + '\'' + ", expire=" + expire + '}';
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

    @NonNull
    public String getKey() {
        return key;
    }

    public Duration getExpire() {
        return expire;
    }
}
