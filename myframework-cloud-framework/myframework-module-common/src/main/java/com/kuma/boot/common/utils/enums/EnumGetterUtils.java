/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.HashBasedTable
 *  com.google.common.collect.Table
 */
package com.kuma.boot.common.utils.enums;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

public final class EnumGetterUtils {
    private static Table<Class<?>, Function<?, ?>, Map<?, Enum<?>>> cache = HashBasedTable.create();

    private EnumGetterUtils() {
    }

    public static <E extends Enum<E>, P> E get(Class<E> enumClass, Function<E, P> getterMethodReference, P value) {
        Map enumMap = (Map)cache.get(enumClass, getterMethodReference);
        if (enumMap == null) {
            enumMap = EnumSet.allOf(enumClass).stream().collect(HashMap::new, (m, e) -> m.put(getterMethodReference.apply(e), e), Map::putAll);
            cache.put(enumClass, getterMethodReference, (Object)enumMap);
        }
        return (E)((Enum)enumMap.get(value));
    }

    public static <E extends Enum<E>, P> E getOrDefault(Class<E> enumClass, Function<E, P> getterMethodReference, P value, E defaultValue) {
        return (E)((Enum)Optional.ofNullable(EnumGetterUtils.get(enumClass, getterMethodReference, value)).orElse(defaultValue));
    }

    public static <E extends Enum<E>, P, R> R getEnumPropertyValue(Class<E> enumClass, Function<E, P> keyMethodReference, P key, Function<E, R> valueMethodReference) {
        return EnumGetterUtils.getEnumPropertyValue(enumClass, keyMethodReference, key, valueMethodReference, null);
    }

    public static <E extends Enum<E>, P, R> R getEnumPropertyValue(Class<E> enumClass, Function<E, P> keyMethodReference, P key, Function<E, R> valueMethodReference, R defaultValue) {
        return Optional.ofNullable(key).map(x -> EnumGetterUtils.get(enumClass, keyMethodReference, x)).map(valueMethodReference).orElse(defaultValue);
    }
}

