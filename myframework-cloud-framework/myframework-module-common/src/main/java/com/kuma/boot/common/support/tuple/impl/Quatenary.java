/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.common.support.tuple.impl;

import com.kuma.boot.common.support.tuple.ValueFour;
import com.kuma.boot.common.support.tuple.ValueOne;
import com.kuma.boot.common.support.tuple.ValueThree;
import com.kuma.boot.common.support.tuple.ValueTwo;
import com.kuma.boot.common.support.tuple.impl.AbstractTuple;

public class Quatenary<A, B, C, D>
extends AbstractTuple
implements ValueOne<A>,
ValueTwo<B>,
ValueThree<C>,
ValueFour<D> {
    private final A a;
    private final B b;
    private final C c;
    private final D d;

    public Quatenary(A a, B b, C c, D d) {
        super(a, b, c, d);
        this.a = a;
        this.b = b;
        this.c = c;
        this.d = d;
    }

    public static <A, B, C, D> Quatenary<A, B, C, D> of(A a, B b, C c, D d) {
        return new Quatenary<A, B, C, D>(a, b, c, d);
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

    @Override
    public D getValueFour() {
        return this.d;
    }

    public String toString() {
        return "Quatenary{a=" + String.valueOf(this.a) + ", b=" + String.valueOf(this.b) + ", c=" + String.valueOf(this.c) + ", d=" + String.valueOf(this.d) + "}";
    }
}

