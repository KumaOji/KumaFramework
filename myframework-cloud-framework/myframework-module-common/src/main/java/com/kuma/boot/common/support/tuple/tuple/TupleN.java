/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.common.support.tuple.tuple;

import com.kuma.boot.common.support.tuple.tuple.Tuple;
import java.util.Objects;

public final class TupleN
extends Tuple {
    private TupleN(Object ... args) {
        super(args);
    }

    @Override
    public TupleN reverse() {
        Object[] array = new Object[this.size()];
        this.forEachWithIndex((index, obj) -> {
            array[array.length - 1 - index.intValue()] = obj;
        });
        return new TupleN(array);
    }

    public static TupleN with(Object ... args) {
        Objects.requireNonNull(args, "args is null");
        return new TupleN(args);
    }
}

