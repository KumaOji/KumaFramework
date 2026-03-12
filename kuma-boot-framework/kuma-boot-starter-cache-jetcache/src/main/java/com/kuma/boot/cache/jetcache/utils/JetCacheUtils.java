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

package com.kuma.boot.cache.jetcache.utils;

import com.alicp.jetcache.Cache;
import com.alicp.jetcache.anno.CacheType;
import com.kuma.boot.cache.jetcache.enhance.JetCacheCreateCacheFactory;
import org.jspecify.annotations.Nullable;

import java.time.Duration;

/**
 * JetCache 静态工具类，持有 {@link JetCacheCreateCacheFactory} 单例引用，
 * 供无法通过依赖注入获取工厂的场景（如 MyBatis 二级缓存）使用。
 *
 * @author kuma
 * @since 2022-07-03
 */
public final class JetCacheUtils {

    private static volatile JetCacheUtils instance;
    private JetCacheCreateCacheFactory factory;

    private JetCacheUtils() {}

    public static JetCacheUtils getInstance() {
        if (instance == null) {
            synchronized (JetCacheUtils.class) {
                if (instance == null) {
                    instance = new JetCacheUtils();
                }
            }
        }
        return instance;
    }

    public static void setJetCacheCreateCacheFactory(JetCacheCreateCacheFactory jetCacheCreateCacheFactory) {
        getInstance().factory = jetCacheCreateCacheFactory;
    }

    public static <K, V> Cache<K, V> create(String name) {
        return getInstance().factory.create(name);
    }

    public static <K, V> Cache<K, V> create(String name, Duration expire) {
        return create(name, expire, true);
    }

    public static <K, V> Cache<K, V> create(String name, Duration expire, Boolean cacheNullValue) {
        return create(name, expire, cacheNullValue, null);
    }

    public static <K, V> Cache<K, V> create(
            String name, Duration expire, Boolean cacheNullValue, @Nullable Boolean syncLocal) {
        return create(name, CacheType.BOTH, expire, cacheNullValue, syncLocal);
    }

    public static <K, V> Cache<K, V> create(String name, CacheType cacheType) {
        return create(name, cacheType, null);
    }

    public static <K, V> Cache<K, V> create(String name, CacheType cacheType, @Nullable Duration expire) {
        return create(name, cacheType, expire, true);
    }

    public static <K, V> Cache<K, V> create(
            String name, CacheType cacheType, @Nullable Duration expire, Boolean cacheNullValue) {
        return create(name, cacheType, expire, cacheNullValue, null);
    }

    public static <K, V> Cache<K, V> create(
            String name, CacheType cacheType, @Nullable Duration expire,
            Boolean cacheNullValue, @Nullable Boolean syncLocal) {
        return getInstance().factory.create(name, cacheType, expire, cacheNullValue, syncLocal);
    }
}
