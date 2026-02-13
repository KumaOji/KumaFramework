/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.common.support.tuple.tuple;

import com.kuma.boot.common.support.tuple.tuple.Tuple;

public final class Tuple0
extends Tuple {
    private static final Object[] EMPTY = new Object[0];
    private static final Tuple0 INSTANCE = new Tuple0();

    private Tuple0() {
        super(EMPTY);
    }

    @Override
    public Tuple0 reverse() {
        return this;
    }

    public static Tuple0 with() {
        return INSTANCE;
    }
}

