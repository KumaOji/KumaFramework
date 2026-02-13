/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.common.support.tuple.tuple;

import com.kuma.boot.common.support.tuple.tuple.Tuple;

public final class Tuple4<A, B, C, D>
extends Tuple {
    public final A first;
    public final B second;
    public final C third;
    public final D fourth;

    private Tuple4(A first, B second, C third, D fourth) {
        super(first, second, third, fourth);
        this.first = first;
        this.second = second;
        this.third = third;
        this.fourth = fourth;
    }

    public static <A, B, C, D> Tuple4<A, B, C, D> with(A first, B second, C third, D fourth) {
        return new Tuple4<A, B, C, D>(first, second, third, fourth);
    }

    @Override
    public Tuple4<D, C, B, A> reverse() {
        return new Tuple4<D, C, B, A>(this.fourth, this.third, this.second, this.first);
    }
}

