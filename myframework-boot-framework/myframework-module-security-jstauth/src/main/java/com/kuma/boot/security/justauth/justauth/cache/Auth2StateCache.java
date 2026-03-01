/*
 * Copyright (c) 2020-2030, kuma (2569277704@qq.com & https://blog.kumacloud.top/).
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

package com.kuma.boot.security.justauth.justauth.cache;

import com.kuma.boot.security.justauth.justauth.enums.CacheKeyStrategy;
import me.zhyd.oauth.cache.AuthStateCache;

/**
 * {@link AuthStateCache} 的扩展, 添加自定义 Cache key 的方法
 */
public interface Auth2StateCache extends AuthStateCache {
    /**
     * 获取 缓存 key 的策略
     * @return CacheKeyStrategy
     */
    CacheKeyStrategy getCacheKeyStrategy();
}
