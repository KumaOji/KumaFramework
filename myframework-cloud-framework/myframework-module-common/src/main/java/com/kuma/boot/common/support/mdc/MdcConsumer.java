/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.common.support.mdc;

import com.kuma.boot.common.support.mdc.MdcAttr;
import java.util.function.Consumer;

public abstract class MdcConsumer<T>
implements Consumer<T> {
    private final MdcAttr mdcAttr;

    protected abstract void doAccept(T var1);

    public MdcConsumer() {
        this(MdcAttr.fromMdc());
    }

    public MdcConsumer(MdcAttr mdcAttr) {
        this.mdcAttr = mdcAttr;
    }

    @Override
    public void accept(T t) {
        try {
            this.mdcAttr.putMdc();
            this.doAccept(t);
        }
        finally {
            this.mdcAttr.removeMdc();
        }
    }

    public MdcAttr getMdcAttr() {
        return this.mdcAttr;
    }
}

