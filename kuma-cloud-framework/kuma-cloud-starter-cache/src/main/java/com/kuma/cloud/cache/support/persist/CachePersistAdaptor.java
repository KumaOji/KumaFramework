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

package com.kuma.cloud.cache.support.persist;

import com.kuma.cloud.cache.api.Cache;
import com.kuma.cloud.cache.api.CachePersist;
import java.util.concurrent.TimeUnit;

/**
 * 缓存持久化-适配器模式
 * @author kuma
 * @since 2024.06
 */
public class CachePersistAdaptor<K, V> implements CachePersist<K, V> {

    /**
     * 持久化
     * key长度 key+value
     * 第一个空格，获取 key 的长度，然后截取
     * @param cache 缓存
     */
    @Override
    public void persist( Cache<K, V> cache) {}

    @Override
    public long delay() {
        return this.period();
    }

    @Override
    public long period() {
        return 1;
    }

    @Override
    public TimeUnit timeUnit() {
        return TimeUnit.SECONDS;
    }
}
