/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.web.mvc.converter;

import java.util.Map;
import java.util.Set;
import java.util.function.Function;

public abstract class BaseDateConverter<T> {
    public T convert(String source, Function<String, T> function) {
        if (source == null || source.isEmpty()) {
            return null;
        }
        String sourceTrim = source.trim();
        Set<Map.Entry<String, String>> entries = this.getFormat().entrySet();
        for (Map.Entry<String, String> entry : entries) {
            if (!sourceTrim.matches(entry.getValue())) continue;
            return function.apply(entry.getKey());
        }
        throw new IllegalArgumentException("\u65e0\u6548\u7684\u65e5\u671f\u53c2\u6570\u683c\u5f0f:'" + sourceTrim + "'");
    }

    protected abstract Map<String, String> getFormat();
}

