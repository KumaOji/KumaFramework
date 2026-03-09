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

package com.kuma.cloud.cache.support.evict;

import com.kuma.cloud.cache.api.CacheEntry;
import com.kuma.cloud.cache.api.CacheEvict;
import com.kuma.cloud.cache.api.CacheEvictContext;

/**
 * 丢弃策略-抽象实现类
 * @author kuma
 * @since 2024.06
 */
public abstract class AbstractCacheEvict<K, V> implements CacheEvict<K, V> {

    @Override
    public CacheEntry<K, V> evict( CacheEvictContext<K, V> context) {
        // 3. 返回结果
        return doEvict(context);
    }

    /**
     * 执行移除
     * @param context 上下文
     * @return 结果
     * @since 2024.06
     */
    protected abstract CacheEntry<K, V> doEvict( CacheEvictContext<K, V> context);

    @Override
    public void updateKey(K key) {}

    @Override
    public void removeKey(K key) {}
}
