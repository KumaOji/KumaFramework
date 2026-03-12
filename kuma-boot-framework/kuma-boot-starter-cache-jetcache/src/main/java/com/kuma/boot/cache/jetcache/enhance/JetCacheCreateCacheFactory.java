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

package com.kuma.boot.cache.jetcache.enhance;

import com.alicp.jetcache.Cache;
import com.alicp.jetcache.CacheManager;
import com.alicp.jetcache.anno.CacheType;
import com.alicp.jetcache.template.QuickConfig;
import com.kuma.boot.common.utils.lang.StringUtils;
import org.jspecify.annotations.Nullable;

import java.time.Duration;

/**
 * JetCache {@link Cache} 创建工厂，对 {@link QuickConfig} 构建进行封装，提供多种便捷重载。
 *
 * @author kuma
 * @since 2022-07-03
 */
public class JetCacheCreateCacheFactory {

    private final CacheManager cacheManager;

    public JetCacheCreateCacheFactory(CacheManager cacheManager) {
        this.cacheManager = cacheManager;
    }

    public <K, V> Cache<K, V> create(String name) {
        return create(name, Duration.ofHours(2));
    }

    public <K, V> Cache<K, V> create(String name, Duration expire) {
        return create(name, expire, true);
    }

    public <K, V> Cache<K, V> create(String name, Duration expire, Boolean cacheNullValue) {
        return create(name, expire, cacheNullValue, null);
    }

    public <K, V> Cache<K, V> create(String name, Duration expire, Boolean cacheNullValue, @Nullable Boolean syncLocal) {
        return create(name, CacheType.BOTH, expire, cacheNullValue, syncLocal);
    }

    public <K, V> Cache<K, V> create(String name, CacheType cacheType) {
        return create(name, cacheType, null);
    }

    public <K, V> Cache<K, V> create(String name, CacheType cacheType, @Nullable Duration expire) {
        return create(name, cacheType, expire, true);
    }

    public <K, V> Cache<K, V> create(String name, CacheType cacheType, @Nullable Duration expire, Boolean cacheNullValue) {
        return create(name, cacheType, expire, cacheNullValue, null);
    }

    public <K, V> Cache<K, V> create(
            String name, CacheType cacheType, @Nullable Duration expire,
            Boolean cacheNullValue, @Nullable Boolean syncLocal) {
        return create(null, name, cacheType, expire, cacheNullValue, syncLocal);
    }

    public <K, V> Cache<K, V> create(
            @Nullable String area, String name, CacheType cacheType, @Nullable Duration expire,
            Boolean cacheNullValue, @Nullable Boolean syncLocal) {
        return create(area, name, cacheType, expire, cacheNullValue, syncLocal, null);
    }

    public <K, V> Cache<K, V> create(
            @Nullable String area, String name, CacheType cacheType, @Nullable Duration expire,
            Boolean cacheNullValue, @Nullable Boolean syncLocal, @Nullable Duration localExpire) {
        return create(area, name, cacheType, expire, cacheNullValue, syncLocal, localExpire, null);
    }

    public <K, V> Cache<K, V> create(
            @Nullable String area, String name, CacheType cacheType, @Nullable Duration expire,
            Boolean cacheNullValue, @Nullable Boolean syncLocal,
            @Nullable Duration localExpire, @Nullable Integer localLimit) {
        return create(area, name, cacheType, expire, cacheNullValue, syncLocal, localExpire, localLimit, false);
    }

    public <K, V> Cache<K, V> create(
            @Nullable String area, String name, CacheType cacheType, @Nullable Duration expire,
            Boolean cacheNullValue, @Nullable Boolean syncLocal,
            @Nullable Duration localExpire, @Nullable Integer localLimit,
            Boolean useAreaInPrefix) {
        return create(area, name, cacheType, expire, cacheNullValue, syncLocal,
                localExpire, localLimit, useAreaInPrefix, false, null);
    }

    public <K, V> Cache<K, V> create(
            @Nullable String area, String name, CacheType cacheType, @Nullable Duration expire,
            Boolean cacheNullValue, @Nullable Boolean syncLocal,
            @Nullable Duration localExpire, @Nullable Integer localLimit,
            Boolean useAreaInPrefix, Boolean penetrationProtect,
            @Nullable Duration penetrationProtectTimeout) {
        QuickConfig.Builder builder = StringUtils.isEmpty(area)
                ? QuickConfig.newBuilder(name)
                : QuickConfig.newBuilder(area, name);
        builder.cacheType(cacheType);
        builder.expire(expire);
        if (cacheType == CacheType.BOTH) {
            builder.syncLocal(syncLocal);
        }
        builder.localExpire(localExpire);
        builder.localLimit(localLimit);
        builder.cacheNullValue(cacheNullValue);
        builder.useAreaInPrefix(useAreaInPrefix);
        if (penetrationProtect != null) {
            builder.penetrationProtect(penetrationProtect);
            if (Boolean.TRUE.equals(penetrationProtect) && penetrationProtectTimeout != null) {
                builder.penetrationProtectTimeout(penetrationProtectTimeout);
            }
        }
        return cacheManager.getOrCreateCache(builder.build());
    }
}
