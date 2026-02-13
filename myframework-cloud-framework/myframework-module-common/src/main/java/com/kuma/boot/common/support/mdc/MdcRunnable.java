/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.common.support.mdc;

import com.kuma.boot.common.support.mdc.MdcAttr;

public abstract class MdcRunnable
implements Runnable {
    private final MdcAttr mdcAttr;

    protected abstract void doRun();

    public MdcRunnable() {
        this(MdcAttr.fromMdc());
    }

    public MdcRunnable(MdcAttr mdcAttr) {
        this.mdcAttr = mdcAttr;
    }

    @Override
    public void run() {
        try {
            this.mdcAttr.putMdc();
            this.doRun();
        }
        finally {
            this.mdcAttr.removeMdc();
        }
    }

    public MdcAttr getMdcAttr() {
        return this.mdcAttr;
    }
}

