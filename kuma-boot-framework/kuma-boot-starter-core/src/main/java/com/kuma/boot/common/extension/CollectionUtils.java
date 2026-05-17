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

package com.kuma.boot.common.extension;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import org.apache.commons.lang3.ArrayUtils;

/**
 * 集合工具集
 *
 * @see org.apache.commons.collections4.CollectionUtils
 */
public final class CollectionUtils {

    public static boolean isEmpty(final Collection<?> coll) {
        return org.apache.commons.collections4.CollectionUtils.isEmpty(coll);
    }

    public static boolean isNotEmpty(final Collection<?> coll) {
        return org.apache.commons.collections4.CollectionUtils.isNotEmpty(coll);
    }

    public static boolean isEmpty(final Map<?, ?> map) {
        return map == null || map.isEmpty();
    }

    public static boolean isNotEmpty(final Map<?, ?> map) {
        return !isEmpty(map);
    }

    public static Map<String, String> toStringMap(String... pairs) {
        Map<String, String> parameters = new HashMap<>();
        if (ArrayUtils.isEmpty(pairs)) {
            return parameters;
        }

        if (pairs.length > 0) {
            if (pairs.length % 2 != 0) {
                throw new IllegalArgumentException("pairs must be even.");
            }
            for (int i = 0; i < pairs.length; i = i + 2) {
                parameters.put(pairs[i], pairs[i + 1]);
            }
        }
        return parameters;
    }

    public static <K, V> Map<K, V> toMap(Object... pairs) {
        Map<K, V> ret = new HashMap<>();
        if (pairs == null || pairs.length == 0) {
            return ret;
        }

        if (pairs.length % 2 != 0) {
            throw new IllegalArgumentException("Map pairs can not be odd number.");
        }
        int len = pairs.length / 2;
        for (int i = 0; i < len; i++) {
            @SuppressWarnings("unchecked")
            K k = (K) pairs[2 * i];
            @SuppressWarnings("unchecked")
            V v = (V) pairs[2 * i + 1];
            ret.put(k, v);
        }
        return ret;
    }

    @SafeVarargs
    public static <T> Set<T> ofSet(T... values) {
        int size = values == null ? 0 : values.length;
        if (size < 1) {
            return Collections.emptySet();
        }

        float loadFactor = 1f / ((size + 1) * 1.0f);

        if (loadFactor > 0.75f) {
            loadFactor = 0.75f;
        }

        Set<T> elements = new LinkedHashSet<>(size, loadFactor);
        elements.addAll(Arrays.asList(values).subList(0, size));
        return Collections.unmodifiableSet(elements);
    }
}
