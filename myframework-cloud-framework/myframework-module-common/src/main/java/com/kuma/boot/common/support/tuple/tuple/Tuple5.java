/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.common.support.tuple.tuple;

import com.kuma.boot.common.support.tuple.tuple.Tuple;

public final class Tuple5<A, B, C, D, E>
extends Tuple {
    public final A first;
    public final B second;
    public final C third;
    public final D fourth;
    public final E fifth;

    private Tuple5(A first, B second, C third, D fourth, E fifth) {
        super(first, second, third, fourth, fifth);
        this.first = first;
        this.second = second;
        this.third = third;
        this.fourth = fourth;
        this.fifth = fifth;
    }

    public static <A, B, C, D, E> Tuple5<A, B, C, D, E> with(A first, B second, C third, D fourth, E fifth) {
        return new Tuple5<A, B, C, D, E>(first, second, third, fourth, fifth);
    }

    @Override
    public Tuple5<E, D, C, B, A> reverse() {
        return new Tuple5<E, D, C, B, A>(this.fifth, this.fourth, this.third, this.second, this.first);
    }
}

