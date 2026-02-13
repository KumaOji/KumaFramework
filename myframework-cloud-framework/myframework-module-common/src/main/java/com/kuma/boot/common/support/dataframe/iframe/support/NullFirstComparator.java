/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.common.support.dataframe.iframe.support;

import java.util.Comparator;
import java.util.Objects;
import java.util.function.Function;

public interface NullFirstComparator<T>
extends Comparator<T> {
    public static <T, U extends Comparable<? super U>> Comparator<T> comparing(Function<? super T, ? extends U> keyExtractor) {
        Objects.requireNonNull(keyExtractor);
        return (t1, t2) -> {
            if (t1 == null && t2 == null) {
                return 0;
            }
            if (t1 == null) {
                return 1;
            }
            if (t2 == null) {
                return -1;
            }
            Comparable t1Value = (Comparable)keyExtractor.apply(t1);
            Comparable t2Value = (Comparable)keyExtractor.apply(t2);
            if (t1Value == null && t2Value == null) {
                return 0;
            }
            if (t1Value == null) {
                return 1;
            }
            if (t2Value == null) {
                return -1;
            }
            return t1Value.compareTo(t2Value);
        };
    }
}

