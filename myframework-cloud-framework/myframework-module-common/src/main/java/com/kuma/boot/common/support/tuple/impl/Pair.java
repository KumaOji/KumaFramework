/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.common.support.tuple.impl;

import com.kuma.boot.common.support.tuple.ValueOne;
import com.kuma.boot.common.support.tuple.ValueTwo;
import com.kuma.boot.common.support.tuple.impl.AbstractTuple;

public class Pair<A, B>
extends AbstractTuple
implements ValueOne<A>,
ValueTwo<B> {
    private final A a;
    private final B b;

    public Pair(A a, B b) {
        super(a, b);
        this.a = a;
        this.b = b;
    }

    public static <A, B> Pair<A, B> of(A a, B b) {
        return new Pair<A, B>(a, b);
    }

    @Override
    public A getValueOne() {
        return this.a;
    }

    @Override
    public B getValueTwo() {
        return this.b;
    }

    public String toString() {
        return "Pair{a=" + String.valueOf(this.a) + ", b=" + String.valueOf(this.b) + "}";
    }
}

