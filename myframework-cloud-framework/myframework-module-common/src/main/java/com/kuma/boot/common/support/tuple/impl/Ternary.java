/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.common.support.tuple.impl;

import com.kuma.boot.common.support.tuple.ValueOne;
import com.kuma.boot.common.support.tuple.ValueThree;
import com.kuma.boot.common.support.tuple.ValueTwo;
import com.kuma.boot.common.support.tuple.impl.AbstractTuple;

public class Ternary<A, B, C>
extends AbstractTuple
implements ValueOne<A>,
ValueTwo<B>,
ValueThree<C> {
    private final A a;
    private final B b;
    private final C c;

    public Ternary(A a, B b, C c) {
        super(a, b, c);
        this.a = a;
        this.b = b;
        this.c = c;
    }

    public static <A, B, C> Ternary<A, B, C> of(A a, B b, C c) {
        return new Ternary<A, B, C>(a, b, c);
    }

    @Override
    public A getValueOne() {
        return this.a;
    }

    @Override
    public B getValueTwo() {
        return this.b;
    }

    @Override
    public C getValueThree() {
        return this.c;
    }

    public String toString() {
        return "Ternary{a=" + String.valueOf(this.a) + ", b=" + String.valueOf(this.b) + ", c=" + String.valueOf(this.c) + "}";
    }
}

