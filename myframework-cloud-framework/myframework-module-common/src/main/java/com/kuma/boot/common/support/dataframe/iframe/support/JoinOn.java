/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.common.support.dataframe.iframe.support;

import java.util.function.Function;

public interface JoinOn<L, R> {
    public boolean on(L var1, R var2);

    public static <T, K, V1, V2> JoinOn<T, K> on(Function<T, V1> leftField, Function<K, V2> rightField) {
        return (t, k) -> {
            if (t == null || k == null) {
                return false;
            }
            Object leftFieldValue = leftField.apply(t);
            Object rightFieldValue = rightField.apply(k);
            if (leftFieldValue == null && rightFieldValue == null) {
                return true;
            }
            if (leftFieldValue == null) {
                return false;
            }
            return leftFieldValue.equals(rightFieldValue);
        };
    }

    default public <V1, V2> JoinOn<L, R> thenOn(Function<L, V1> leftField, Function<R, V2> rightField) {
        return (t, k) -> this.on(t, k) && JoinOn.on(leftField, rightField).on(t, k);
    }
}

