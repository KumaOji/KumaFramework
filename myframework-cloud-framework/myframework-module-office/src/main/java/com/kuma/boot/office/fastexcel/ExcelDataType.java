/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  org.springframework.core.ResolvableType
 */
package com.kuma.boot.office.fastexcel;

import java.util.Collection;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;
import org.springframework.core.ResolvableType;

public enum ExcelDataType {
    COLLECTION(resolvableType -> resolvableType.resolveGeneric(new int[]{0}), Collection.class::isAssignableFrom),
    MAP(resolvableType -> resolvableType.getGeneric(new int[]{1}).resolveGeneric(new int[]{0}), Map.class::isAssignableFrom);

    private final Function<ResolvableType, Class<?>> function;
    private final Predicate<Class<?>> assignableFrom;

    private ExcelDataType(Function<ResolvableType, Class<?>> function, Predicate<Class<?>> assignableFrom) {
        this.function = function;
        this.assignableFrom = assignableFrom;
    }

    public static ExcelDataType match(Class<?> type) {
        for (ExcelDataType excelDataType : ExcelDataType.values()) {
            if (!excelDataType.assignableFrom.test(type)) continue;
            return excelDataType;
        }
        throw new IllegalArgumentException();
    }

    public Function<ResolvableType, Class<?>> getFunction() {
        return this.function;
    }
}

