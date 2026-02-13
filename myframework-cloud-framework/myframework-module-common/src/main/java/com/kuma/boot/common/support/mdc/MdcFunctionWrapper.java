/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.common.support.mdc;

import com.kuma.boot.common.support.mdc.MdcAttr;
import com.kuma.boot.common.support.mdc.MdcFunction;
import java.util.function.Function;

public class MdcFunctionWrapper<T, R>
extends MdcFunction<T, R> {
    private Function<T, R> f;

    public MdcFunctionWrapper(Function<T, R> f) {
        this.f = f;
    }

    public MdcFunctionWrapper(Function<T, R> f, MdcAttr mdcAttr) {
        super(mdcAttr);
        this.f = f;
    }

    @Override
    public R doApply(T t) {
        return this.f.apply(t);
    }

    public static <T, R> MdcFunctionWrapper<T, R> of(Function<T, R> f) {
        return new MdcFunctionWrapper<T, R>(f);
    }

    public Function<T, R> getF() {
        return this.f;
    }
}

