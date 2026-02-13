/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.common.support.mdc;

import com.kuma.boot.common.support.mdc.MdcAttr;
import java.util.function.Function;

public abstract class MdcFunction<T, R>
implements Function<T, R> {
    private final MdcAttr mdcAttr;

    protected abstract R doApply(T var1);

    public MdcFunction() {
        this(MdcAttr.fromMdc());
    }

    public MdcFunction(MdcAttr mdcAttr) {
        this.mdcAttr = mdcAttr;
    }

    @Override
    public R apply(T t) {
        try {
            this.mdcAttr.putMdc();
            R r = this.doApply(t);
            return r;
        }
        finally {
            this.mdcAttr.removeMdc();
        }
    }

    public MdcAttr getMdcAttr() {
        return this.mdcAttr;
    }
}

