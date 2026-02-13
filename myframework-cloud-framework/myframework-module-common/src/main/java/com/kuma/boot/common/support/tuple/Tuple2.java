/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.common.support.tuple;

import java.io.Serializable;

public class Tuple2<T1, T2>
implements Serializable {
    final T1 _1;
    final T2 _2;

    public Tuple2(T1 _1, T2 _2) {
        this._1 = _1;
        this._2 = _2;
    }

    public T1 _1() {
        return this._1;
    }

    public T2 _2() {
        return this._2;
    }
}

