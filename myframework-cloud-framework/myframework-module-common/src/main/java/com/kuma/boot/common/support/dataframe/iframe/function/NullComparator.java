/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.common.support.dataframe.iframe.function;

import java.io.Serializable;
import java.util.Comparator;
import java.util.Objects;
import java.util.function.Function;

public interface NullComparator<T>
extends Comparator<T> {
    public int nulCompare(T var1, T var2);

    @Override
    default public int compare(T left, T right) {
        if (left == null && right == null) {
            return 0;
        }
        if (left == null) {
            return -1;
        }
        if (right == null) {
            return 1;
        }
        return this.nulCompare(left, right);
    }

    public static <T, U extends Comparable<? super U>> NullComparator<T> comparing(Function<? super T, ? extends U> keyExtractor) {
        Objects.requireNonNull(keyExtractor);
        return (t1, t2) -> {
            if (t1 == null && t2 == null) {
                return 0;
            }
            if (t1 == null) {
                return -1;
            }
            if (t2 == null) {
                return 1;
            }
            Comparable t1Value = (Comparable)keyExtractor.apply(t1);
            Comparable t2Value = (Comparable)keyExtractor.apply(t2);
            if (t1Value == null && t2Value == null) {
                return 0;
            }
            if (t1Value == null) {
                return -1;
            }
            if (t2Value == null) {
                return 1;
            }
            return t1Value.compareTo(t2Value);
        };
    }

    public static <T, U> NullComparator<T> comparing(Function<? super T, ? extends U> keyExtractor, Comparator<? super U> keyComparator) {
        Objects.requireNonNull(keyExtractor);
        Objects.requireNonNull(keyComparator);
        return (NullComparator<Object> & Serializable)(c1, c2) -> keyComparator.compare((Object)keyExtractor.apply(c1), (Object)keyExtractor.apply(c2));
    }

    @Override
    default public NullComparator<T> thenComparing(Comparator<? super T> other) {
        Objects.requireNonNull(other);
        return (NullComparator<Object> & Serializable)(c1, c2) -> {
            int res = this.compare(c1, c2);
            return res != 0 ? res : other.compare(c1, c2);
        };
    }

    @Override
    default public <U> NullComparator<T> thenComparing(Function<? super T, ? extends U> keyExtractor, Comparator<? super U> keyComparator) {
        return this.thenComparing(NullComparator.comparing(keyExtractor, keyComparator));
    }

    @Override
    default public <U extends Comparable<? super U>> NullComparator<T> thenComparing(Function<? super T, ? extends U> keyExtractor) {
        return this.thenComparing(NullComparator.comparing(keyExtractor));
    }
}

