/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.common.support.mdc;

import com.kuma.boot.common.support.mdc.MdcAttr;
import java.util.concurrent.RecursiveAction;

public abstract class MdcRecursiveAction
extends RecursiveAction {
    private final MdcAttr mdcAttr;

    protected abstract void doCompute();

    public MdcRecursiveAction() {
        this(MdcAttr.fromMdc());
    }

    public MdcRecursiveAction(MdcAttr mdcAttr) {
        this.mdcAttr = mdcAttr;
    }

    @Override
    protected void compute() {
        try {
            this.mdcAttr.putMdc();
            this.doCompute();
        }
        finally {
            this.mdcAttr.removeMdc();
        }
    }

    public MdcAttr getMdcAttr() {
        return this.mdcAttr;
    }
}

