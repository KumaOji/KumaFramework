/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.common.support.tuple.tuple;

import com.kuma.boot.common.support.tuple.tuple.Tuple;
import com.kuma.boot.common.support.tuple.tuple.Tuple0;
import com.kuma.boot.common.support.tuple.tuple.Tuple1;
import com.kuma.boot.common.support.tuple.tuple.Tuple2;
import com.kuma.boot.common.support.tuple.tuple.Tuple3;
import com.kuma.boot.common.support.tuple.tuple.Tuple4;
import com.kuma.boot.common.support.tuple.tuple.Tuple5;
import com.kuma.boot.common.support.tuple.tuple.TupleN;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

public final class Tuples {
    private Tuples() {
    }

    public static Tuple0 tuple() {
        return Tuple0.with();
    }

    public static <A> Tuple1<A> tuple(A first) {
        return Tuple1.with(first);
    }

    public static <A, B> Tuple2<A, B> tuple(A first, B second) {
        return Tuple2.with(first, second);
    }

    public static <A, B, C> Tuple3<A, B, C> tuple(A first, B second, C third) {
        return Tuple3.with(first, second, third);
    }

    public static <A, B, C, D> Tuple4<A, B, C, D> tuple(A first, B second, C third, D fourth) {
        return Tuple4.with(first, second, third, fourth);
    }

    public static <A, B, C, D, E> Tuple5<A, B, C, D, E> tuple(A first, B second, C third, D fourth, E fifth) {
        return Tuple5.with(first, second, third, fourth, fifth);
    }

    public static TupleN tuple(Object ... args) {
        return TupleN.with(args);
    }

    public static <T> void sort(List<? extends Tuple> list, int index, Comparator<T> comparator) {
        Objects.requireNonNull(list, "list is null");
        if (list.size() < 2) {
            return;
        }
        Objects.requireNonNull(comparator, "comparator is null");
        if (index < 0) {
            throw new IllegalArgumentException("index must >= 0");
        }
        list.sort(Comparator.comparing(t -> t.get(index), comparator));
    }

    public static <T> void sort(Tuple[] array, int index, Comparator<T> comparator) {
        Objects.requireNonNull(array, "array is null");
        if (array.length < 2) {
            return;
        }
        Objects.requireNonNull(comparator, "comparator is null");
        if (index < 0) {
            throw new IllegalArgumentException("index must >= 0");
        }
        Arrays.sort(array, Comparator.comparing(t -> t.get(index), comparator));
    }
}

