/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.common.support.dataframe.iframe.window;

import com.kuma.boot.common.support.dataframe.iframe.item.FI2;
import java.util.List;

public interface SupplierFunction<T, V> {
    public List<FI2<T, V>> get(List<T> var1);
}

