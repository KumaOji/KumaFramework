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
import com.kuma.boot.common.constant.SymbolConstants;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.CacheManager;

import java.time.Duration;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 基于 JetCache 的 Spring {@link CacheManager} 实现。
 *
 * <p>支持动态创建 cache（{@code dynamic = true}）和预定义 cache name 两种模式。
 * cache name 末尾会自动补全 {@code :} 分隔符以保持命名一致性。
 *
 * @author kuma
 * @since 2022-07-03
 */
public class JetCacheSpringCacheManager implements CacheManager {

    private static final Logger log = LoggerFactory.getLogger(JetCacheSpringCacheManager.class);

    private boolean dynamic = true;
    private boolean allowNullValues = true;

    private final Map<String, org.springframework.cache.Cache> cacheMap = new ConcurrentHashMap<>(16);
    private final JetCacheCreateCacheFactory jetCacheCreateCacheFactory;

    public JetCacheSpringCacheManager(JetCacheCreateCacheFactory jetCacheCreateCacheFactory) {
        this.jetCacheCreateCacheFactory = jetCacheCreateCacheFactory;
    }

    public JetCacheSpringCacheManager(JetCacheCreateCacheFactory jetCacheCreateCacheFactory, String... cacheNames) {
        this.jetCacheCreateCacheFactory = jetCacheCreateCacheFactory;
        setCacheNames(Arrays.asList(cacheNames));
    }

    public void setAllowNullValues(boolean allowNullValues) {
        this.allowNullValues = allowNullValues;
    }

    public boolean isAllowNullValues() {
        return allowNullValues;
    }

    private void setCacheNames(@Nullable Collection<String> cacheNames) {
        if (cacheNames != null) {
            for (String name : cacheNames) {
                cacheMap.put(name, createJetCache(name));
            }
            dynamic = false;
        } else {
            dynamic = true;
        }
    }

    protected org.springframework.cache.Cache createJetCache(String name) {
        Cache<Object, Object> cache = jetCacheCreateCacheFactory.create(name);
        log.debug("[kmc] |- CACHE - Herodotus cache [{}] is CREATED.", name);
        return new JetCacheSpringCache(name, cache, allowNullValues);
    }

    protected org.springframework.cache.Cache createJetCache(String name, Duration expire) {
        Cache<Object, Object> cache = jetCacheCreateCacheFactory.create(name, expire, allowNullValues, true);
        log.debug("[kmc] |- CACHE - Herodotus cache [{}] with expire is CREATED.", name);
        return new JetCacheSpringCache(name, cache, allowNullValues);
    }

    /** 确保 cache name 以 {@code :} 结尾，与 JetCache 命名约定保持一致。 */
    private String availableCacheName(String name) {
        return name.endsWith(SymbolConstants.COLON) ? name : name + SymbolConstants.COLON;
    }

    @Override
    public org.springframework.cache.@Nullable Cache getCache(String name) {
        String usedName = availableCacheName(name);
        return cacheMap.computeIfAbsent(usedName, cacheName -> dynamic ? createJetCache(cacheName) : null);
    }

    @Override
    public Collection<String> getCacheNames() {
        return Collections.unmodifiableSet(cacheMap.keySet());
    }
}
