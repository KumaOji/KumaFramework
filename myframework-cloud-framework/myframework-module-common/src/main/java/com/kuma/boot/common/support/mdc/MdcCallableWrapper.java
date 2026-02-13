/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.common.support.mdc;

import com.kuma.boot.common.support.mdc.MdcAttr;
import com.kuma.boot.common.support.mdc.MdcCallable;
import java.util.concurrent.Callable;

public class MdcCallableWrapper<T>
extends MdcCallable<T> {
    private Callable<T> c;

    public MdcCallableWrapper(Callable<T> c) {
        this.c = c;
    }

    public MdcCallableWrapper(Callable<T> c, MdcAttr mdcAttr) {
        super(mdcAttr);
        this.c = c;
    }

    @Override
    public T doCall() throws Exception {
        return this.c.call();
    }

    public static <T> MdcCallableWrapper<T> of(Callable<T> c) {
        return new MdcCallableWrapper<T>(c);
    }

    public Callable<T> getC() {
        return this.c;
    }
}

