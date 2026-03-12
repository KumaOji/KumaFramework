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

package com.kuma.boot.cache.caffeine.repository;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.kuma.boot.cache.caffeine.model.CacheHashKey;
import com.kuma.boot.cache.caffeine.model.CacheKey;
import com.kuma.boot.common.constant.StrPoolConstants;
import org.jspecify.annotations.NonNull;
import org.springframework.util.StringUtils;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import java.util.regex.Pattern;

/**
 * 基于 Caffeine 实现的内存缓存，主要用于开发、测试、演示环境。
 *
 * <p>以嵌套结构存储数据：外层 Cache 以 key 字符串为索引，内层 Cache 持有该 key 的值并负责 TTL。
 *
 * @author kuma
 * @since 2022-04-27
 */
public class CaffeineRepository {

    /** 外层缓存最大条目数 */
    private static final long DEF_MAX_SIZE = 1_000;

    private final Cache<String, Cache<String, Object>> cacheMap =
            Caffeine.newBuilder().maximumSize(DEF_MAX_SIZE).build();

    // ---- KV 操作 ----

    public Long del(@NonNull CacheKey... keys) {
        for (CacheKey key : keys) {
            cacheMap.invalidate(key.getKey());
        }
        return (long) keys.length;
    }

    public Long del(String... keys) {
        for (String key : keys) {
            cacheMap.invalidate(key);
        }
        return (long) keys.length;
    }

    public void set(@NonNull CacheKey key, Object value) {
        if (value == null) {
            return;
        }
        Cache<String, Object> cache = buildInnerCache(key);
        cache.put(key.getKey(), value);
        cacheMap.put(key.getKey(), cache);
    }

    @SuppressWarnings("unchecked")
    public <T> T get(@NonNull CacheKey key) {
        Cache<String, Object> inner = cacheMap.getIfPresent(key.getKey());
        if (inner == null) {
            return null;
        }
        return (T) inner.getIfPresent(key.getKey());
    }

    @SuppressWarnings("unchecked")
    public <T> T get(String key) {
        Cache<String, Object> inner = cacheMap.getIfPresent(key);
        if (inner == null) {
            return null;
        }
        return (T) inner.getIfPresent(key);
    }

    @SuppressWarnings("unchecked")
    public <T> List<T> find(@NonNull Collection<CacheKey> keys) {
        return keys.stream()
                .map(k -> (T) get(k))
                .filter(Objects::nonNull)
                .toList();
    }

    @SuppressWarnings("unchecked")
    public <T> T get(@NonNull CacheKey key, Function<CacheKey, ? extends T> loader) {
        Cache<String, Object> cache = cacheMap.get(key.getKey(), k -> {
            Cache<String, Object> newCache = buildInnerCache(key);
            T loaded = loader.apply(new CacheKey(k, key.getExpire()));
            if (loaded != null) {
                newCache.put(k, loaded);
            }
            return newCache;
        });
        return (T) Objects.requireNonNull(cache).getIfPresent(key.getKey());
    }

    public void flushDb() {
        cacheMap.invalidateAll();
    }

    public Boolean exists(@NonNull CacheKey key) {
        Cache<String, Object> cache = cacheMap.getIfPresent(key.getKey());
        if (cache == null) {
            return false;
        }
        cache.cleanUp();
        return cache.estimatedSize() > 0;
    }

    // ---- 计数器操作（线程安全） ----

    public Long incr(@NonNull CacheKey key) {
        return adjustLong(key, 1L);
    }

    public Long incrBy(@NonNull CacheKey key, long increment) {
        return adjustLong(key, increment);
    }

    public Long decr(@NonNull CacheKey key) {
        return adjustLong(key, -1L);
    }

    public Long decrBy(@NonNull CacheKey key, long decrement) {
        return adjustLong(key, -decrement);
    }

    public Double incrByFloat(@NonNull CacheKey key, double increment) {
        AtomicReference<Double> result = new AtomicReference<>();
        cacheMap.asMap().compute(key.getKey(), (k, existing) -> {
            Cache<String, Object> cache = existing != null ? existing : buildInnerCache(key);
            Object current = cache.getIfPresent(k);
            double newVal = (current instanceof Double d ? d : 0D) + increment;
            cache.put(k, newVal);
            result.set(newVal);
            return cache;
        });
        return result.get();
    }

    public Long getCounter(@NonNull CacheKey key, Function<CacheKey, Long> loader) {
        Cache<String, Object> inner = cacheMap.getIfPresent(key.getKey());
        if (inner != null) {
            Object val = inner.getIfPresent(key.getKey());
            if (val instanceof Long l) {
                return l;
            }
        }
        return loader != null ? loader.apply(key) : 0L;
    }

    // ---- key 扫描（仅限开发/测试环境）----

    /**
     * 按 Redis glob 模式匹配 key。支持 {@code *}、{@code ?}、{@code [ae]} 语法。
     *
     * <p><b>注意：</b>仅在开发/测试环境使用，不支持集群。
     */
    public Set<String> keys(@NonNull String pattern) {
        if (!StringUtils.hasText(pattern)) {
            return Collections.emptySet();
        }
        ConcurrentMap<String, Cache<String, Object>> map = cacheMap.asMap();
        if (StrPoolConstants.ASTERISK.equals(pattern)) {
            return new HashSet<>(map.keySet());
        }
        Pattern regex = Pattern.compile(globToRegex(pattern));
        Set<String> result = new HashSet<>();
        map.keySet().forEach(k -> {
            if (regex.matcher(k).matches()) {
                result.add(k);
            }
        });
        return result;
    }

    /** Redis glob 转 Java 正则 */
    private static String globToRegex(String glob) {
        StringBuilder sb = new StringBuilder("^");
        for (int i = 0; i < glob.length(); i++) {
            char c = glob.charAt(i);
            switch (c) {
                case '*' -> sb.append(".*");
                case '?' -> sb.append('.');
                case '[', ']' -> sb.append(c);
                case '.', '(', ')', '+', '{', '}', '^', '$', '|', '\\' -> sb.append('\\').append(c);
                default -> sb.append(c);
            }
        }
        sb.append('$');
        return sb.toString();
    }

    // ---- 占位方法（Caffeine 无原生支持，语义不适用）----

    public Boolean expire(@NonNull CacheKey key) {
        return true;
    }

    public Boolean persist(@NonNull CacheKey key) {
        return true;
    }

    public String type(@NonNull CacheKey key) {
        return "caffeine";
    }

    public Long ttl(@NonNull CacheKey key) {
        return -1L;
    }

    public Long pTtl(@NonNull CacheKey key) {
        return -1L;
    }

    // ---- Hash 操作（扁平化实现）----

    public void hSet(@NonNull CacheHashKey key, Object value) {
        set(key.tran(), value);
    }

    public <T> T hGet(@NonNull CacheHashKey key) {
        return get(key.tran());
    }

    public <T> T hGet(@NonNull CacheHashKey key, Function<CacheHashKey, T> loader) {
        return get(key.tran(), k -> loader.apply(key));
    }

    public Boolean hExists(@NonNull CacheHashKey key) {
        return exists(key.tran());
    }

    public Long hDel(@NonNull String key, Object... fields) {
        for (Object field : fields) {
            cacheMap.invalidate(key + StrPoolConstants.COLON + field);
        }
        return (long) fields.length;
    }

    public Long hDel(@NonNull CacheHashKey key) {
        cacheMap.invalidate(key.tran().getKey());
        return 1L;
    }

    public Long hLen(@NonNull CacheHashKey key) {
        return 0L;
    }

    public Long hIncrBy(@NonNull CacheHashKey key, long increment) {
        return incrBy(key.tran(), increment);
    }

    public Double hIncrBy(@NonNull CacheHashKey key, double increment) {
        return incrByFloat(key.tran(), increment);
    }

    public Set<Object> hKeys(@NonNull CacheHashKey key) {
        return Collections.emptySet();
    }

    public List<Object> hVals(@NonNull CacheHashKey key) {
        return Collections.emptyList();
    }

    public <K, V> Map<K, V> hGetAll(CacheHashKey key) {
        return Collections.emptyMap();
    }

    public <K, V> Map<K, V> hGetAll(CacheHashKey key, Function<CacheHashKey, Map<K, V>> loader) {
        return Collections.emptyMap();
    }

    // ---- Set 操作（占位）----

    public Long sAdd(@NonNull CacheKey key, Object value) {
        return 0L;
    }

    public Long sRem(@NonNull CacheKey key, Object... members) {
        return 0L;
    }

    public Set<Object> sMembers(@NonNull CacheKey key) {
        return Collections.emptySet();
    }

    public <T> T sPop(@NonNull CacheKey key) {
        return null;
    }

    public Long sCard(@NonNull CacheKey key) {
        return 0L;
    }

    // ---- 私有工具方法 ----

    private static Cache<String, Object> buildInnerCache(@NonNull CacheKey key) {
        Caffeine<Object, Object> builder = Caffeine.newBuilder().maximumSize(DEF_MAX_SIZE);
        if (key.getExpire() != null) {
            builder.expireAfterWrite(key.getExpire());
        }
        return builder.build();
    }

    private Long adjustLong(@NonNull CacheKey key, long delta) {
        AtomicLong result = new AtomicLong();
        cacheMap.asMap().compute(key.getKey(), (k, existing) -> {
            Cache<String, Object> cache = existing != null ? existing : buildInnerCache(key);
            Object current = cache.getIfPresent(k);
            long newVal = (current instanceof Long l ? l : 0L) + delta;
            cache.put(k, newVal);
            result.set(newVal);
            return cache;
        });
        return result.get();
    }
}
