/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.common.support.mdc;

import com.kuma.boot.common.support.mdc.MdcAttr;
import java.util.concurrent.RecursiveTask;

public abstract class MdcRecursiveTask<T>
extends RecursiveTask<T> {
    private final MdcAttr mdcAttr;

    protected abstract T doCompute();

    public MdcRecursiveTask() {
        this(MdcAttr.fromMdc());
    }

    public MdcRecursiveTask(MdcAttr mdcAttr) {
        this.mdcAttr = mdcAttr;
    }

    @Override
    protected T compute() {
        try {
            this.mdcAttr.putMdc();
            T t = this.doCompute();
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

