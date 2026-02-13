/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.common.model;

public class Callable {

    public static interface Func2<T0, T1, T2> {
        public T0 invoke(T1 var1, T2 var2);
    }

    public static interface Func1<T0, T1> {
        public T0 invoke(T1 var1);
    }

    public static interface Func0<T0> {
        public T0 invoke();
    }

    public static interface Action3<T1, T2, T3> {
        public void invoke(T1 var1, T2 var2, T3 var3);
    }

    public static interface Action2<T1, T2> {
        public void invoke(T1 var1, T2 var2);
    }

    public static interface Action1<T1> {
        public void invoke(T1 var1);
    }

    public static interface Action0 {
        public void invoke();
    }
}

