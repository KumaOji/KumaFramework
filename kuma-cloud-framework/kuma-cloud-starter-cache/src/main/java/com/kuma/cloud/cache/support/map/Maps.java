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

package com.kuma.cloud.cache.support.map;

import java.util.HashMap;
import java.util.Map;

/**
 * @author kuma
 * @since 2024.06
 */
public final class Maps {

    private Maps() {}

    /**
     * hashMap 实现策略
     * @param <K> key
     * @param <V> value
     * @return map 实现
     * @since 2024.06
     */
    public static <K, V> Map<K, V> hashMap() {
        return new HashMap<>();
    }
}
