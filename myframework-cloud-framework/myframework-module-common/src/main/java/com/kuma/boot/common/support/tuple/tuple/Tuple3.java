/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.common.support.tuple.tuple;

import com.kuma.boot.common.support.tuple.tuple.Tuple;

public final class Tuple3<A, B, C>
extends Tuple {
    public final A first;
    public final B second;
    public final C third;

    private Tuple3(A first, B second, C third) {
        super(first, second, third);
        this.first = first;
        this.second = second;
        this.third = third;
    }

    public static <A, B, C> Tuple3<A, B, C> with(A first, B second, C third) {
        return new Tuple3<A, B, C>(first, second, third);
    }

    @Override
    public Tuple3<C, B, A> reverse() {
        return new Tuple3<C, B, A>(this.third, this.second, this.first);
    }
}

