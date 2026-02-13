/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 */
package com.kuma.boot.common.utils.collection;

import com.google.common.collect.Lists;
import com.kuma.boot.common.support.handler.Handler;
import com.kuma.boot.common.support.handler.MapEntryHandler;
import com.kuma.boot.common.support.handler.MapHandler;
import com.kuma.boot.common.utils.lang.ObjectUtils;
import com.kuma.boot.common.utils.lang.StringUtils;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public final class MapUtils {
    private static final Charset DEFAULT_ENCODING = StandardCharsets.UTF_8;

    private MapUtils() {
    }

    public static boolean isEmpty(Map<?, ?> map) {
        return null == map || 0 == map.size();
    }

    public static boolean isNotEmpty(Map<?, ?> map) {
        return !MapUtils.isEmpty(map);
    }

    public static <K, V> Map<K, V> toMap(Collection<V> values, Handler<? super V, K> keyFunction) {
        if (ObjectUtils.isNull(values)) {
            return Collections.emptyMap();
        }
        HashMap<K, V> map = new HashMap<K, V>(values.size());
        for (V value : values) {
            K key = keyFunction.handle(value);
            map.put(key, value);
        }
        return map;
    }

    public static <K, V, O> Map<K, V> toMap(Collection<O> values, MapHandler<K, V, O> mapHandler) {
        if (ObjectUtils.isNull(values)) {
            return Collections.emptyMap();
        }
        HashMap<K, V> map = new HashMap<K, V>(values.size());
        for (O line : values) {
            K key = mapHandler.getKey(line);
            V value = mapHandler.getValue(line);
            map.put(key, value);
        }
        return map;
    }

    public static <K, V, T> List<T> toList(Map<K, V> map, MapEntryHandler<K, V, T> entryHandler) {
        if (MapUtils.isEmpty(map)) {
            return Collections.emptyList();
        }
        ArrayList resultList = Lists.newArrayList();
        for (Map.Entry<K, V> entry : map.entrySet()) {
            T result = entryHandler.handler(entry);
            resultList.add(result);
        }
        return resultList;
    }

    public static <V> Map<Integer, V> toIndexMap(Collection<V> values) {
        if (ObjectUtils.isNull(values)) {
            return Collections.emptyMap();
        }
        HashMap<Integer, V> map = new HashMap<Integer, V>(values.size());
        for (V v : values) {
            map.put(map.size(), v);
        }
        return map;
    }

    public static String getMapValue(Map<String, String> map, String key) {
        if (MapUtils.isEmpty(map)) {
            return key;
        }
        String value = map.get(key);
        if (StringUtils.isEmpty(value)) {
            return key;
        }
        return value;
    }

    public static <K, V> V getMapValue(Map<K, V> map, K key, V defaultValue) {
        if (MapUtils.isEmpty(map)) {
            return defaultValue;
        }
        V value = map.get(key);
        if (ObjectUtils.isNull(value)) {
            return defaultValue;
        }
        return value;
    }

    public static Map.Entry<String, Object> getFirstEntry(Map<String, Object> map) {
        if (MapUtils.isEmpty(map)) {
            return null;
        }
        Iterator<Map.Entry<String, Object>> iterator = map.entrySet().iterator();
        if (iterator.hasNext()) {
            Map.Entry<String, Object> entry = iterator.next();
            return entry;
        }
        return null;
    }

    public static String parseMapToString(Map<String, String> params, boolean encode) {
        if (null == params || params.isEmpty()) {
            return "";
        }
        ArrayList paramList = new ArrayList();
        params.forEach((k, v) -> {
            if (null == v) {
                paramList.add(k + "=");
            } else {
                paramList.add(k + "=" + (encode ? MapUtils.urlEncode(v) : v));
            }
        });
        return String.join((CharSequence)"&", paramList);
    }

    public static String urlEncode(String value) {
        if (value == null) {
            return "";
        }
        try {
            String encoded = URLEncoder.encode(value, DEFAULT_ENCODING.displayName());
            return encoded.replace("+", "%20").replace("*", "%2A").replace("~", "%7E").replace("/", "%2F");
        }
        catch (UnsupportedEncodingException e) {
            throw new RuntimeException("Failed To Encode Uri", e);
        }
    }
}

