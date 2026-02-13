/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.common.support.dataframe.util;

import java.util.List;
import java.util.function.Function;

public class FieldValueList<T, F> {
    private final List<T> data;
    private final Function<T, F> field;

    public FieldValueList(List<T> data, Function<T, F> field) {
        this.data = data;
        this.field = field;
    }

    public F get(Integer index) {
        if (index == null) {
            return null;
        }
        return this.field.apply(this.data.get(index));
    }
}

