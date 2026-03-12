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

import static com.kuma.boot.common.constant.StrPoolConstants.COLON;

/**
 * Hash 缓存 key，包含 key、field 以及可选过期时间。
 *
 * <p>通过 {@link #tran()} 将 hash key 扁平化为 {@code key:field} 格式的 {@link CacheKey}，
 * 以便在 Caffeine 平铺结构中存储 hash 语义数据。
 *
 * @author kuma
 * @since 2021-09-07
 */
public class CacheHashKey extends CacheKey {

    @NonNull
    private final Object field;

    public CacheHashKey(@NonNull String key, @NonNull Object field) {
        super(key);
        this.field = field;
    }

    public CacheHashKey(@NonNull String key, @NonNull Object field, @Nullable Duration expire) {
        super(key, expire);
        this.field = field;
    }

    /** 将 hash key 转换为 {@code key:field} 格式的普通 {@link CacheKey}。 */
    public CacheKey tran() {
        return new CacheKey(getKey() + COLON + field, getExpire());
    }

    @NonNull
    public Object getField() {
        return field;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }
        CacheHashKey that = (CacheHashKey) o;
        return field.equals(that.field);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), field);
    }

    @Override
    public String toString() {
        return "CacheHashKey{field=" + field + "} " + super.toString();
    }
}
