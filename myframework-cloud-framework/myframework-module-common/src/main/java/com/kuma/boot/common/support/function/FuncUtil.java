/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.common.support.function;

import java.util.function.Predicate;
import java.util.function.Supplier;

public class FuncUtil {
    public static <T> T predicate(T t, Predicate<T> predicate, T defaultValue) {
        return predicate.test(t) ? defaultValue : t;
    }

    public static <T> T predicate(T t, Supplier<Boolean> supplier, T defaultValue) {
        return supplier.get() != false ? defaultValue : t;
    }
}

