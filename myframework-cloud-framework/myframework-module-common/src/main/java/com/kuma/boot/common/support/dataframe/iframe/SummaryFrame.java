/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.common.support.dataframe.iframe;

import com.kuma.boot.common.support.dataframe.iframe.support.MaxMin;
import java.math.BigDecimal;
import java.util.Comparator;
import java.util.function.Function;

public interface SummaryFrame<T> {
    public <R> BigDecimal sum(Function<T, R> var1);

    public <R> BigDecimal avg(Function<T, R> var1);

    public <R extends Comparable<? super R>> MaxMin<T> maxMin(Function<T, R> var1);

    public <R extends Comparable<? super R>> MaxMin<R> maxMinValue(Function<T, R> var1);

    public <R extends Comparable<R>> T max(Function<T, R> var1);

    public <R extends Comparable<? super R>> R maxValue(Function<T, R> var1);

    public <R extends Comparable<? super R>> R minValue(Function<T, R> var1);

    public <R extends Comparable<R>> T min(Function<T, R> var1);

    public long count();

    public long countDistinct(Comparator<T> var1);

    public <R extends Comparable<R>> long countDistinct(Function<T, R> var1);
}

