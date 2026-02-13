/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.common.support.mdc;

import com.kuma.boot.common.support.mdc.MdcAttr;
import com.kuma.boot.common.support.mdc.MdcRunnable;

public class MdcRunnableWrapper
extends MdcRunnable {
    private Runnable r;

    public MdcRunnableWrapper(Runnable r) {
        this.r = r;
    }

    public MdcRunnableWrapper(Runnable r, MdcAttr mdcAttr) {
        super(mdcAttr);
        this.r = r;
    }

    @Override
    public void doRun() {
        this.r.run();
    }

    public static MdcRunnableWrapper of(Runnable r) {
        return new MdcRunnableWrapper(r);
    }

    public Runnable getR() {
        return this.r;
    }
}

