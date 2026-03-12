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

import com.kuma.boot.cache.jetcache.utils.JetCacheUtils;
import org.apache.ibatis.cache.Cache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * 基于 JetCache 的 MyBatis 二级缓存实现。
 *
 * <p>MyBatis 要求 {@link Cache} 实现类提供单参数 {@code String id} 的构造器，
 * 因此通过 {@link JetCacheUtils} 静态工具获取 {@link JetCacheCreateCacheFactory}，
 * 无需依赖 Spring 上下文注入。
 *
 * @author kuma
 * @since 2022-07-03
 */
public class HerodotusMybatisCache implements Cache {

    private static final Logger log = LoggerFactory.getLogger(HerodotusMybatisCache.class);

    private final String id;
    private final com.alicp.jetcache.Cache<Object, Object> cache;
    private final AtomicInteger counter = new AtomicInteger(0);

    public HerodotusMybatisCache(String id) {
        this.id = id;
        this.cache = JetCacheUtils.create(id);
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public void putObject(Object key, Object value) {
        cache.put(key, value);
        counter.incrementAndGet();
        log.debug("[kmc] |- CACHE - Put into MyBatis cache, key: [{}]", key);
    }

    @Override
    public Object getObject(Object key) {
        Object obj = cache.get(key);
        log.debug("[kmc] |- CACHE - Get from MyBatis cache, key: [{}]", key);
        return obj;
    }

    @Override
    public Object removeObject(Object key) {
        Object obj = cache.remove(key);
        counter.decrementAndGet();
        log.debug("[kmc] |- CACHE - Remove from MyBatis cache, key: [{}]", key);
        return obj;
    }

    @Override
    public void clear() {
        cache.close();
        log.debug("[kmc] |- CACHE - Clear MyBatis cache.");
    }

    @Override
    public int getSize() {
        return counter.get();
    }
}
