/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.common.support.complete;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class EmptyUtil {
    private EmptyUtil() {
        throw new AssertionError();
    }

    public static boolean isEmpty(Collection<?> collection) {
        return collection == null || collection.isEmpty();
    }

    public static boolean isNotEmpty(Collection<?> collection) {
        return !EmptyUtil.isEmpty(collection);
    }

    public static boolean isEmpty(Object[] arrays) {
        return arrays == null || arrays.length == 0;
    }

    public static boolean isNotEmpty(Object[] arrays) {
        return !EmptyUtil.isEmpty(arrays);
    }

    public static boolean isEmpty(Map<?, ?> map) {
        return map == null || map.isEmpty();
    }

    public static boolean isNotEmpty(Map<?, ?> map) {
        return !EmptyUtil.isEmpty(map);
    }

    public static boolean isEmpty(String str) {
        return str == null || str.trim().isEmpty();
    }

    public static boolean isNotEmpty(String str) {
        return !EmptyUtil.isEmpty(str);
    }

    public static boolean isEmpty(Object bean) {
        return Objects.isNull(bean);
    }

    public static boolean isNotEmpty(Object bean) {
        return !EmptyUtil.isEmpty(bean);
    }

    public static <T> List<T> emptyList() {
        return Collections.emptyList();
    }

    public static <T> Set<T> emptySet() {
        return Collections.emptySet();
    }

    public static <K, V> Map<K, V> emptyMap() {
        return Collections.emptyMap();
    }
}

