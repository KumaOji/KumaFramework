/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.common.support.mdc;

import com.kuma.boot.common.support.mdc.MdcAttr;
import com.kuma.boot.common.support.mdc.MdcConsumer;
import java.util.function.Consumer;

public class MdcConsumerWrapper<T>
extends MdcConsumer<T> {
    private Consumer<T> c;

    public MdcConsumerWrapper(Consumer<T> c) {
        this.c = c;
    }

    public MdcConsumerWrapper(Consumer<T> c, MdcAttr mdcAttr) {
        super(mdcAttr);
        this.c = c;
    }

    @Override
    public void doAccept(T t) {
        this.c.accept(t);
    }

    public static <T> MdcConsumerWrapper<T> of(Consumer<T> c) {
        return new MdcConsumerWrapper<T>(c);
    }

    public Consumer<T> getC() {
        return this.c;
    }
}

