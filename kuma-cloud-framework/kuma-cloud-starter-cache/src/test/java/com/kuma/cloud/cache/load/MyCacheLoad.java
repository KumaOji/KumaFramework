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

package com.kuma.cloud.cache.load;

import com.kuma.cloud.cache.api.Cache;
import com.kuma.cloud.cache.api.CacheLoad;

/**
 * @author kuma
 * @since 2024.06
 */
public class MyCacheLoad implements CacheLoad<String, String> {

    @Override
    public void load( Cache<String, String> cache) {
        cache.put("1", "1");
        cache.put("2", "2");
    }
}
