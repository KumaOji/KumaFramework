/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.common.utils.lambda;

import java.util.function.Function;

public final class FunctionUtils {
    private FunctionUtils() {
    }

    public static <X, U, Y> Function<X, Y> compose(Function<U, Y> fnY, Function<X, U> fnU) {
        return fnY.compose(fnU);
    }

    public static <X, U, V, Y> Function<X, Y> compose(Function<V, Y> fnY, Function<U, V> fnV, Function<X, U> fnU) {
        return fnY.compose(fnV).compose(fnU);
    }
}

