/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.common.support.mdc;

import com.kuma.boot.common.support.mdc.MdcAttr;
import com.kuma.boot.common.support.mdc.MdcSupplier;
import java.util.function.Supplier;

public class MdcSupplierWrapper<T>
extends MdcSupplier<T> {
    private Supplier<T> s;

    public MdcSupplierWrapper(Supplier<T> s) {
        this.s = s;
    }

    public MdcSupplierWrapper(Supplier<T> s, MdcAttr mdcAttr) {
        super(mdcAttr);
        this.s = s;
    }

    @Override
    public T doGet() {
        return this.s.get();
    }

    public static <T> MdcSupplierWrapper<T> of(Supplier<T> s) {
        return new MdcSupplierWrapper<T>(s);
    }

    public Supplier<T> getS() {
        return this.s;
    }
}

