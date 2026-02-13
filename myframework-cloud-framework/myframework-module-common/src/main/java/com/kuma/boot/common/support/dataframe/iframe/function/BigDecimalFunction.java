/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.common.support.dataframe.iframe.function;

import java.math.BigDecimal;

@FunctionalInterface
public interface BigDecimalFunction<T> {
    public BigDecimal applyAsBigDecimal(T var1);
}

