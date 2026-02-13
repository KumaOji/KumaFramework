/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.common.support.mdc;

import com.kuma.boot.common.support.mdc.MdcAttr;
import java.util.concurrent.Callable;

public abstract class MdcCallable<T>
implements Callable<T> {
    private MdcAttr mdcAttr;

    protected abstract T doCall() throws Exception;

    public MdcCallable() {
        this(MdcAttr.fromMdc());
    }

    public MdcCallable(MdcAttr mdcAttr) {
        this.mdcAttr = mdcAttr;
    }

    @Override
    public T call() throws Exception {
        try {
            this.mdcAttr.putMdc();
            T t = this.doCall();
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

