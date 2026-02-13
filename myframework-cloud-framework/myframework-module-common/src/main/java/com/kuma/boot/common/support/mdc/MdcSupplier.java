/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.common.support.mdc;

import com.kuma.boot.common.support.mdc.MdcAttr;
import java.util.function.Supplier;

public abstract class MdcSupplier<T>
implements Supplier<T> {
    private final MdcAttr mdcAttr;

    protected abstract T doGet();

    public MdcSupplier() {
        this(MdcAttr.fromMdc());
    }

    public MdcSupplier(MdcAttr mdcAttr) {
        this.mdcAttr = mdcAttr;
    }

    @Override
    public T get() {
        try {
            this.mdcAttr.putMdc();
            T t = this.doGet();
            return t;
        }
        finally {
            this.mdcAttr.removeMdc();
        }
    }

    public MdcAttr getMdcAttr() {
        return this.mdcAttr;
    }
}

