/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.common.support.tuple.tuple;

import com.kuma.boot.common.support.tuple.tuple.Tuple;

public final class Tuple2<A, B>
extends Tuple {
    public final A first;
    public final B second;

    private Tuple2(A first, B second) {
        super(first, second);
        this.first = first;
        this.second = second;
    }

    public static <A, B> Tuple2<A, B> with(A first, B second) {
        return new Tuple2<A, B>(first, second);
    }

    @Override
    public Tuple2<B, A> reverse() {
        return new Tuple2<B, A>(this.second, this.first);
    }
}

