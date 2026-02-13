/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.common.support.tuple.tuple;

import com.kuma.boot.common.support.tuple.tuple.Tuple;

public final class Tuple1<A>
extends Tuple {
    public final A first;

    private Tuple1(A first) {
        super(first);
        this.first = first;
    }

    public static <A> Tuple1<A> with(A first) {
        return new Tuple1<A>(first);
    }

    @Override
    public Tuple1<A> reverse() {
        return new Tuple1<A>(this.first);
    }
}

