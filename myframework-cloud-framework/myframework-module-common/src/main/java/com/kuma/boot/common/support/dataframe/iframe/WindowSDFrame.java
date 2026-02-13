/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.common.support.dataframe.iframe;

import com.kuma.boot.common.support.dataframe.iframe.SDFrame;
import com.kuma.boot.common.support.dataframe.iframe.function.SetFunction;
import com.kuma.boot.common.support.dataframe.iframe.item.FI2;
import java.math.BigDecimal;
import java.util.function.Function;

public interface WindowSDFrame<T>
extends SDFrame<T> {
    @Override
    public SDFrame<FI2<T, Integer>> overRowNumber();

    @Override
    public WindowSDFrame<T> overRowNumberS(SetFunction<T, Integer> var1);

    public SDFrame<FI2<T, Integer>> overRank();

    public WindowSDFrame<T> overRankS(SetFunction<T, Integer> var1);

    public SDFrame<FI2<T, Integer>> overDenseRank();

    public WindowSDFrame<T> overDenseRankS(SetFunction<T, Integer> var1);

    public SDFrame<FI2<T, BigDecimal>> overPercentRank();

    public WindowSDFrame<T> overPercentRankS(SetFunction<T, BigDecimal> var1);

    public SDFrame<FI2<T, BigDecimal>> overCumeDist();

    public WindowSDFrame<T> overCumeDistS(SetFunction<T, BigDecimal> var1);

    @Override
    public <F> SDFrame<FI2<T, F>> overLag(Function<T, F> var1, int var2);

    @Override
    public <F> WindowSDFrame<T> overLagS(SetFunction<T, F> var1, Function<T, F> var2, int var3);

    @Override
    public <F> SDFrame<FI2<T, F>> overLead(Function<T, F> var1, int var2);

    @Override
    public <F> WindowSDFrame<T> overLeadS(SetFunction<T, F> var1, Function<T, F> var2, int var3);

    @Override
    public <F> SDFrame<FI2<T, F>> overNthValue(Function<T, F> var1, int var2);

    @Override
    public <F> WindowSDFrame<T> overNthValueS(SetFunction<T, F> var1, Function<T, F> var2, int var3);

    @Override
    public <F> SDFrame<FI2<T, F>> overFirstValue(Function<T, F> var1);

    @Override
    public <F> WindowSDFrame<T> overFirstValueS(SetFunction<T, F> var1, Function<T, F> var2);

    @Override
    public <F> SDFrame<FI2<T, F>> overLastValue(Function<T, F> var1);

    @Override
    public <F> WindowSDFrame<T> overLastValueS(SetFunction<T, F> var1, Function<T, F> var2);

    @Override
    public <F> SDFrame<FI2<T, BigDecimal>> overSum(Function<T, F> var1);

    @Override
    public <F> WindowSDFrame<T> overSumS(SetFunction<T, BigDecimal> var1, Function<T, F> var2);

    @Override
    public <F> SDFrame<FI2<T, BigDecimal>> overAvg(Function<T, F> var1);

    @Override
    public <F> WindowSDFrame<T> overAvgS(SetFunction<T, BigDecimal> var1, Function<T, F> var2);

    @Override
    public <F extends Comparable<? super F>> SDFrame<FI2<T, F>> overMaxValue(Function<T, F> var1);

    @Override
    public <F extends Comparable<? super F>> WindowSDFrame<T> overMaxValueS(SetFunction<T, F> var1, Function<T, F> var2);

    @Override
    public <F extends Comparable<? super F>> SDFrame<FI2<T, F>> overMinValue(Function<T, F> var1);

    @Override
    public <F extends Comparable<? super F>> WindowSDFrame<T> overMinValueS(SetFunction<T, F> var1, Function<T, F> var2);

    @Override
    public SDFrame<FI2<T, Integer>> overCount();

    @Override
    public WindowSDFrame<T> overCountS(SetFunction<T, Integer> var1);

    @Override
    public SDFrame<FI2<T, Integer>> overNtile(int var1);

    @Override
    public WindowSDFrame<T> overNtileS(SetFunction<T, Integer> var1, int var2);
}

