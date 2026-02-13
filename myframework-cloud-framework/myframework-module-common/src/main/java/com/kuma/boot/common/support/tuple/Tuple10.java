/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.common.support.tuple;

import java.io.Serializable;

public class Tuple10<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10>
implements Serializable {
    final T1 _1;
    final T2 _2;
    final T3 _3;
    final T4 _4;
    final T5 _5;
    final T6 _6;
    final T7 _7;
    final T8 _8;
    final T9 _9;
    final T10 _10;

    public Tuple10(T1 _1, T2 _2, T3 _3, T4 _4, T5 _5, T6 _6, T7 _7, T8 _8, T9 _9, T10 _10) {
        this._1 = _1;
        this._2 = _2;
        this._3 = _3;
        this._4 = _4;
        this._5 = _5;
        this._6 = _6;
        this._7 = _7;
        this._8 = _8;
        this._9 = _9;
        this._10 = _10;
    }

    public T1 _1() {
        return this._1;
    }

    public T2 _2() {
        return this._2;
    }

    public T3 _3() {
        return this._3;
    }

    public T4 _4() {
        return this._4;
    }

    public T5 _5() {
        return this._5;
    }

    public T6 _6() {
        return this._6;
    }

    public T7 _7() {
        return this._7;
    }

    public T8 _8() {
        return this._8;
    }

    public T9 _9() {
        return this._9;
    }

    public T10 _10() {
        return this._10;
    }
}

