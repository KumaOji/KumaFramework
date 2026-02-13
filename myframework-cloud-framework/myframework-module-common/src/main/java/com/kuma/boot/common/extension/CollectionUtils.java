/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.collections4.CollectionUtils
 *  org.apache.commons.lang3.ArrayUtils
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

public final class CollectionUtils {
    public static boolean isEmpty(Collection<?> coll) {
        return org.apache.commons.collections4.CollectionUtils.isEmpty(coll);
    }

    public static boolean isNotEmpty(Collection<?> coll) {
        return org.apache.commons.collections4.CollectionUtils.isNotEmpty(coll);
    }

    public static boolean isEmpty(Map<?, ?> map) {
        return map == null || map.isEmpty();
    }

    public static boolean isNotEmpty(Map<?, ?> map) {
        return !CollectionUtils.isEmpty(map);
    }

    public static Map<String, String> toStringMap(String ... pairs) {
        HashMap<String, String> parameters = new HashMap<String, String>();
        if (ArrayUtils.isEmpty((Object[])pairs)) {
            return parameters;
        }
        if (pairs.length > 0) {
            if (pairs.length % 2 != 0) {
                throw new IllegalArgumentException("pairs must be even.");
            }
            for (int i = 0; i < pairs.length; i += 2) {
                parameters.put(pairs[i], pairs[i + 1]);
            }
        }
        return parameters;
    }

    @SuppressWarnings("unchecked")
    public static <K, V> Map<K, V> toMap(Object ... pairs) {
        HashMap<Object, Object> ret = new HashMap<Object, Object>();
        if (pairs == null || pairs.length == 0) {
            return ret;
        }
        if (pairs.length % 2 != 0) {
            throw new IllegalArgumentException("Map pairs can not be odd number.");
        }
        int len = pairs.length / 2;
        for (int i = 0; i < len; ++i) {
            Object k = pairs[2 * i];
            Object v = pairs[2 * i + 1];
            ret.put(k, v);
        }
        return (Map<K, V>) (Map<?, ?>) ret;
    }

    @SafeVarargs
    public static <T> Set<T> ofSet(T ... values) {
        int size = values == null ? 0 : values.length;
        if (size < 1) {
            return Collections.emptySet();
        }
        float loadFactor = 1.0f / ((float)(size + 1) * 1.0f);
        if (loadFactor > 0.75f) {
            loadFactor = 0.75f;
        }
        LinkedHashSet<T> elements = new LinkedHashSet<T>(size, loadFactor);
        elements.addAll(Arrays.asList(values).subList(0, size));
        return Collections.unmodifiableSet(elements);
    }
}

