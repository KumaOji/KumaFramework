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

import com.kuma.boot.common.constant.StrPoolConstants;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import static com.kuma.boot.common.constant.StrPoolConstants.COLON;

/**
 * 缓存 key 构建器，支持 KV 和 Hash 两种模式，兼容 Caffeine 与 Redis 存储后端。
 *
 * @author kuma
 * @since 2021-09-07
 */
@FunctionalInterface
public interface CacheKeyBuilder {

    /**
     * 租户编码；非租户模式返回空字符串。
     *
     * @return 租户编码
     */
    @NonNull
    default String getTenant() {
        return "";
    }

    /**
     * key 前缀（必须实现）。
     *
     * @return key 前缀
     */
    @NonNull
    String getPrefix();

    /**
     * 超时时间；{@code null} 表示不过期。
     *
     * @return 超时时间
     */
    @Nullable
    default Duration getExpire() {
        return null;
    }

    /**
     * 构建 KV 模式的 {@link CacheKey}，兼容 Caffeine 与 Redis。
     *
     * @param suffix 动态参数
     * @return cache key
     */
    default CacheKey key(Object... suffix) {
        String field = suffix.length > 0 && suffix[0] != null
                ? String.valueOf(suffix[0])
                : StrPoolConstants.EMPTY;
        return hashFieldKey(field, suffix);
    }

    /**
     * 构建带 field 的 hash {@link CacheHashKey}。
     *
     * @param field  hash field
     * @param suffix 动态参数
     * @return hash cache key
     */
    default CacheHashKey hashFieldKey(@NonNull Object field, Object... suffix) {
        return new CacheHashKey(getKey(suffix), field, getExpire());
    }

    /**
     * 构建仅含 key 的 hash {@link CacheKey}（无 field），适用于 HGETALL 等整体操作。
     *
     * @param suffix 动态参数
     * @return cache key
     */
    default CacheKey hashKey(Object... suffix) {
        return new CacheKey(getKey(suffix), getExpire());
    }

    /**
     * 将租户、前缀和动态参数拼接为完整 key。
     *
     * @param suffix 动态参数
     * @return 完整 key 字符串
     */
    default String getKey(Object... suffix) {
        List<String> parts = new ArrayList<>();
        String tenant = getTenant();
        if (!tenant.isEmpty()) {
            parts.add(tenant);
        }
        parts.add(getPrefix());
        for (Object s : suffix) {
            if (s != null) {
                String str = String.valueOf(s);
                if (!str.isEmpty()) {
                    parts.add(str);
                }
            }
        }
        return String.join(COLON, parts);
    }
}
