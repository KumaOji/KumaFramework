/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.common.support.dataframe.iframe;

import com.kuma.boot.common.support.dataframe.iframe.SDFrame;
import com.kuma.boot.common.support.dataframe.iframe.function.SetFunction;
import com.kuma.boot.common.support.dataframe.iframe.item.FI2;
import com.kuma.boot.common.support.dataframe.iframe.window.Window;
import java.math.BigDecimal;
import java.util.function.Function;

public interface OverSDFrame<T> {
    public SDFrame<FI2<T, Integer>> overRowNumber(Window<T> var1);

    public SDFrame<FI2<T, Integer>> overRowNumber();

    public SDFrame<T> overRowNumberS(SetFunction<T, Integer> var1, Window<T> var2);

    public SDFrame<T> overRowNumberS(SetFunction<T, Integer> var1);

    public SDFrame<FI2<T, Integer>> overRank(Window<T> var1);

    public SDFrame<T> overRankS(SetFunction<T, Integer> var1, Window<T> var2);

    public SDFrame<FI2<T, Integer>> overDenseRank(Window<T> var1);

    public SDFrame<T> overDenseRankS(SetFunction<T, Integer> var1, Window<T> var2);

    public SDFrame<FI2<T, BigDecimal>> overPercentRank(Window<T> var1);

    public SDFrame<T> overPercentRankS(SetFunction<T, BigDecimal> var1, Window<T> var2);

    public SDFrame<FI2<T, BigDecimal>> overCumeDist(Window<T> var1);

    public SDFrame<T> overCumeDistS(SetFunction<T, BigDecimal> var1, Window<T> var2);

    public <F> SDFrame<FI2<T, F>> overLag(Window<T> var1, Function<T, F> var2, int var3);

    public <F> SDFrame<T> overLagS(SetFunction<T, F> var1, Window<T> var2, Function<T, F> var3, int var4);

    public <F> SDFrame<FI2<T, F>> overLag(Function<T, F> var1, int var2);

    public <F> SDFrame<T> overLagS(SetFunction<T, F> var1, Function<T, F> var2, int var3);

    public <F> SDFrame<FI2<T, F>> overLead(Window<T> var1, Function<T, F> var2, int var3);

    public <F> SDFrame<T> overLeadS(SetFunction<T, F> var1, Window<T> var2, Function<T, F> var3, int var4);

    public <F> SDFrame<FI2<T, F>> overLead(Function<T, F> var1, int var2);

    public <F> SDFrame<T> overLeadS(SetFunction<T, F> var1, Function<T, F> var2, int var3);

    public <F> SDFrame<FI2<T, F>> overNthValue(Window<T> var1, Function<T, F> var2, int var3);

    public <F> SDFrame<T> overNthValueS(SetFunction<T, F> var1, Window<T> var2, Function<T, F> var3, int var4);

    public <F> SDFrame<FI2<T, F>> overNthValue(Function<T, F> var1, int var2);

    public <F> SDFrame<T> overNthValueS(SetFunction<T, F> var1, Function<T, F> var2, int var3);

    public <F> SDFrame<FI2<T, F>> overFirstValue(Window<T> var1, Function<T, F> var2);

    public <F> SDFrame<T> overFirstValueS(SetFunction<T, F> var1, Window<T> var2, Function<T, F> var3);

    public <F> SDFrame<FI2<T, F>> overFirstValue(Function<T, F> var1);

    public <F> SDFrame<T> overFirstValueS(SetFunction<T, F> var1, Function<T, F> var2);

    public <F> SDFrame<FI2<T, F>> overLastValue(Window<T> var1, Function<T, F> var2);

    public <F> SDFrame<T> overLastValueS(SetFunction<T, F> var1, Window<T> var2, Function<T, F> var3);

    public <F> SDFrame<FI2<T, F>> overLastValue(Function<T, F> var1);

    public <F> SDFrame<T> overLastValueS(SetFunction<T, F> var1, Function<T, F> var2);

    public <F> SDFrame<FI2<T, BigDecimal>> overSum(Window<T> var1, Function<T, F> var2);

    public <F> SDFrame<FI2<T, BigDecimal>> overSum(Function<T, F> var1);

    public <F> SDFrame<T> overSumS(SetFunction<T, BigDecimal> var1, Window<T> var2, Function<T, F> var3);

    public <F> SDFrame<T> overSumS(SetFunction<T, BigDecimal> var1, Function<T, F> var2);

    public <F> SDFrame<FI2<T, BigDecimal>> overAvg(Window<T> var1, Function<T, F> var2);

    public <F> SDFrame<FI2<T, BigDecimal>> overAvg(Function<T, F> var1);

    public <F> SDFrame<T> overAvgS(SetFunction<T, BigDecimal> var1, Window<T> var2, Function<T, F> var3);

    public <F> SDFrame<T> overAvgS(SetFunction<T, BigDecimal> var1, Function<T, F> var2);

    public <F extends Comparable<? super F>> SDFrame<FI2<T, F>> overMaxValue(Window<T> var1, Function<T, F> var2);

    public <F extends Comparable<? super F>> SDFrame<FI2<T, F>> overMaxValue(Function<T, F> var1);

    public <F extends Comparable<? super F>> SDFrame<T> overMaxValueS(SetFunction<T, F> var1, Window<T> var2, Function<T, F> var3);

    public <F extends Comparable<? super F>> SDFrame<T> overMaxValueS(SetFunction<T, F> var1, Function<T, F> var2);

    public <F extends Comparable<? super F>> SDFrame<FI2<T, F>> overMinValue(Window<T> var1, Function<T, F> var2);

    public <F extends Comparable<? super F>> SDFrame<FI2<T, F>> overMinValue(Function<T, F> var1);

    public <F extends Comparable<? super F>> SDFrame<T> overMinValueS(SetFunction<T, F> var1, Window<T> var2, Function<T, F> var3);

    public <F extends Comparable<? super F>> SDFrame<T> overMinValueS(SetFunction<T, F> var1, Function<T, F> var2);

    public SDFrame<FI2<T, Integer>> overCount(Window<T> var1);

    public SDFrame<FI2<T, Integer>> overCount();

    public SDFrame<T> overCountS(SetFunction<T, Integer> var1, Window<T> var2);

    public SDFrame<T> overCountS(SetFunction<T, Integer> var1);

    public SDFrame<FI2<T, Integer>> overNtile(int var1);

    public SDFrame<FI2<T, Integer>> overNtile(Window<T> var1, int var2);

    public SDFrame<T> overNtileS(SetFunction<T, Integer> var1, Window<T> var2, int var3);

    public SDFrame<T> overNtileS(SetFunction<T, Integer> var1, int var2);
}

