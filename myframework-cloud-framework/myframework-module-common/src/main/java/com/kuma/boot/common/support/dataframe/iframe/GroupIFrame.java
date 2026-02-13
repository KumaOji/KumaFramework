/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.common.support.dataframe.iframe;

import com.kuma.boot.common.support.dataframe.iframe.IFrame;
import com.kuma.boot.common.support.dataframe.iframe.function.NumberFunction;
import com.kuma.boot.common.support.dataframe.iframe.item.FI2;
import com.kuma.boot.common.support.dataframe.iframe.item.FI3;
import com.kuma.boot.common.support.dataframe.iframe.item.FI4;
import com.kuma.boot.common.support.dataframe.iframe.support.MaxMin;
import java.math.BigDecimal;
import java.util.List;
import java.util.function.Function;

public interface GroupIFrame<T> {
    public <K> IFrame<FI2<K, List<T>>> group(Function<? super T, ? extends K> var1);

    public <K, R extends Number> IFrame<FI2<K, BigDecimal>> groupBySum(Function<T, K> var1, NumberFunction<T, R> var2);

    public <K, J, R extends Number> IFrame<FI3<K, J, BigDecimal>> groupBySum(Function<T, K> var1, Function<T, J> var2, NumberFunction<T, R> var3);

    public <K, J, H, R extends Number> IFrame<FI4<K, J, H, BigDecimal>> groupBySum(Function<T, K> var1, Function<T, J> var2, Function<T, H> var3, NumberFunction<T, R> var4);

    public <K> IFrame<FI2<K, Long>> groupByCount(Function<T, K> var1);

    public <K, J> IFrame<FI3<K, J, Long>> groupByCount(Function<T, K> var1, Function<T, J> var2);

    public <K, J, H> IFrame<FI4<K, J, H, Long>> groupByCount(Function<T, K> var1, Function<T, J> var2, Function<T, H> var3);

    public <K, R extends Number> IFrame<FI3<K, BigDecimal, Long>> groupBySumCount(Function<T, K> var1, NumberFunction<T, R> var2);

    public <K, J, R extends Number> IFrame<FI4<K, J, BigDecimal, Long>> groupBySumCount(Function<T, K> var1, Function<T, J> var2, NumberFunction<T, R> var3);

    public <K, R extends Number> IFrame<FI2<K, BigDecimal>> groupByAvg(Function<T, K> var1, NumberFunction<T, R> var2);

    public <K, J, R extends Number> IFrame<FI3<K, J, BigDecimal>> groupByAvg(Function<T, K> var1, Function<T, J> var2, NumberFunction<T, R> var3);

    public <K, J, H, R extends Number> IFrame<FI4<K, J, H, BigDecimal>> groupByAvg(Function<T, K> var1, Function<T, J> var2, Function<T, H> var3, NumberFunction<T, R> var4);

    public <K, V extends Comparable<? super V>> IFrame<FI2<K, T>> groupByMax(Function<T, K> var1, Function<T, V> var2);

    public <K, J, V extends Comparable<? super V>> IFrame<FI3<K, J, T>> groupByMax(Function<T, K> var1, Function<T, J> var2, Function<T, V> var3);

    public <K, V extends Comparable<? super V>> IFrame<FI2<K, V>> groupByMaxValue(Function<T, K> var1, Function<T, V> var2);

    public <K, J, V extends Comparable<? super V>> IFrame<FI3<K, J, V>> groupByMaxValue(Function<T, K> var1, Function<T, J> var2, Function<T, V> var3);

    public <K, V extends Comparable<? super V>> IFrame<FI2<K, T>> groupByMin(Function<T, K> var1, Function<T, V> var2);

    public <K, J, V extends Comparable<? super V>> IFrame<FI3<K, J, T>> groupByMin(Function<T, K> var1, Function<T, J> var2, Function<T, V> var3);

    public <K, V extends Comparable<? super V>> IFrame<FI2<K, V>> groupByMinValue(Function<T, K> var1, Function<T, V> var2);

    public <K, J, V extends Comparable<? super V>> IFrame<FI3<K, J, V>> groupByMinValue(Function<T, K> var1, Function<T, J> var2, Function<T, V> var3);

    public <K, V extends Comparable<? super V>> IFrame<FI2<K, MaxMin<V>>> groupByMaxMinValue(Function<T, K> var1, Function<T, V> var2);

    public <K, J, V extends Comparable<? super V>> IFrame<FI3<K, J, MaxMin<V>>> groupByMaxMinValue(Function<T, K> var1, Function<T, J> var2, Function<T, V> var3);

    public <K, V extends Comparable<? super V>> IFrame<FI2<K, MaxMin<T>>> groupByMaxMin(Function<T, K> var1, Function<T, V> var2);

    public <K, J, V extends Comparable<? super V>> IFrame<FI3<K, J, MaxMin<T>>> groupByMaxMin(Function<T, K> var1, Function<T, J> var2, Function<T, V> var3);
}

