/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.common.support.dataframe.iframe;

import com.kuma.boot.common.support.dataframe.iframe.JDFrame;
import com.kuma.boot.common.support.dataframe.iframe.function.SetFunction;
import com.kuma.boot.common.support.dataframe.iframe.item.FI2;
import com.kuma.boot.common.support.dataframe.iframe.window.Window;
import java.math.BigDecimal;
import java.util.function.Function;

public interface OverJDFrame<T> {
    public JDFrame<FI2<T, Integer>> overRowNumber(Window<T> var1);

    public JDFrame<FI2<T, Integer>> overRowNumber();

    public JDFrame<T> overRowNumberS(SetFunction<T, Integer> var1, Window<T> var2);

    public JDFrame<T> overRowNumberS(SetFunction<T, Integer> var1);

    public JDFrame<FI2<T, Integer>> overRank(Window<T> var1);

    public JDFrame<T> overRankS(SetFunction<T, Integer> var1, Window<T> var2);

    public JDFrame<FI2<T, Integer>> overDenseRank(Window<T> var1);

    public JDFrame<T> overDenseRankS(SetFunction<T, Integer> var1, Window<T> var2);

    public JDFrame<FI2<T, BigDecimal>> overPercentRank(Window<T> var1);

    public JDFrame<T> overPercentRankS(SetFunction<T, BigDecimal> var1, Window<T> var2);

    public JDFrame<FI2<T, BigDecimal>> overCumeDist(Window<T> var1);

    public JDFrame<T> overCumeDistS(SetFunction<T, BigDecimal> var1, Window<T> var2);

    public <F> JDFrame<FI2<T, F>> overLag(Window<T> var1, Function<T, F> var2, int var3);

    public <F> JDFrame<T> overLagS(SetFunction<T, F> var1, Window<T> var2, Function<T, F> var3, int var4);

    public <F> JDFrame<FI2<T, F>> overLag(Function<T, F> var1, int var2);

    public <F> JDFrame<T> overLagS(SetFunction<T, F> var1, Function<T, F> var2, int var3);

    public <F> JDFrame<FI2<T, F>> overLead(Window<T> var1, Function<T, F> var2, int var3);

    public <F> JDFrame<T> overLeadS(SetFunction<T, F> var1, Window<T> var2, Function<T, F> var3, int var4);

    public <F> JDFrame<FI2<T, F>> overLead(Function<T, F> var1, int var2);

    public <F> JDFrame<T> overLeadS(SetFunction<T, F> var1, Function<T, F> var2, int var3);

    public <F> JDFrame<FI2<T, F>> overNthValue(Window<T> var1, Function<T, F> var2, int var3);

    public <F> JDFrame<T> overNthValueS(SetFunction<T, F> var1, Window<T> var2, Function<T, F> var3, int var4);

    public <F> JDFrame<FI2<T, F>> overNthValue(Function<T, F> var1, int var2);

    public <F> JDFrame<T> overNthValueS(SetFunction<T, F> var1, Function<T, F> var2, int var3);

    public <F> JDFrame<FI2<T, F>> overFirstValue(Window<T> var1, Function<T, F> var2);

    public <F> JDFrame<T> overFirstValueS(SetFunction<T, F> var1, Window<T> var2, Function<T, F> var3);

    public <F> JDFrame<FI2<T, F>> overFirstValue(Function<T, F> var1);

    public <F> JDFrame<T> overFirstValueS(SetFunction<T, F> var1, Function<T, F> var2);

    public <F> JDFrame<FI2<T, F>> overLastValue(Window<T> var1, Function<T, F> var2);

    public <F> JDFrame<T> overLastValueS(SetFunction<T, F> var1, Window<T> var2, Function<T, F> var3);

    public <F> JDFrame<FI2<T, F>> overLastValue(Function<T, F> var1);

    public <F> JDFrame<T> overLastValueS(SetFunction<T, F> var1, Function<T, F> var2);

    public <F> JDFrame<FI2<T, BigDecimal>> overSum(Window<T> var1, Function<T, F> var2);

    public <F> JDFrame<FI2<T, BigDecimal>> overSum(Function<T, F> var1);

    public <F> JDFrame<T> overSumS(SetFunction<T, BigDecimal> var1, Window<T> var2, Function<T, F> var3);

    public <F> JDFrame<T> overSumS(SetFunction<T, BigDecimal> var1, Function<T, F> var2);

    public <F> JDFrame<FI2<T, BigDecimal>> overAvg(Window<T> var1, Function<T, F> var2);

    public <F> JDFrame<FI2<T, BigDecimal>> overAvg(Function<T, F> var1);

    public <F> JDFrame<T> overAvgS(SetFunction<T, BigDecimal> var1, Window<T> var2, Function<T, F> var3);

    public <F> JDFrame<T> overAvgS(SetFunction<T, BigDecimal> var1, Function<T, F> var2);

    public <F extends Comparable<? super F>> JDFrame<FI2<T, F>> overMaxValue(Window<T> var1, Function<T, F> var2);

    public <F extends Comparable<? super F>> JDFrame<FI2<T, F>> overMaxValue(Function<T, F> var1);

    public <F extends Comparable<? super F>> JDFrame<T> overMaxValueS(SetFunction<T, F> var1, Window<T> var2, Function<T, F> var3);

    public <F extends Comparable<? super F>> JDFrame<T> overMaxValueS(SetFunction<T, F> var1, Function<T, F> var2);

    public <F extends Comparable<? super F>> JDFrame<FI2<T, F>> overMinValue(Window<T> var1, Function<T, F> var2);

    public <F extends Comparable<? super F>> JDFrame<FI2<T, F>> overMinValue(Function<T, F> var1);

    public <F extends Comparable<? super F>> JDFrame<T> overMinValueS(SetFunction<T, F> var1, Window<T> var2, Function<T, F> var3);

    public <F extends Comparable<? super F>> JDFrame<T> overMinValueS(SetFunction<T, F> var1, Function<T, F> var2);

    public JDFrame<FI2<T, Integer>> overCount(Window<T> var1);

    public JDFrame<FI2<T, Integer>> overCount();

    public JDFrame<T> overCountS(SetFunction<T, Integer> var1, Window<T> var2);

    public JDFrame<T> overCountS(SetFunction<T, Integer> var1);

    public JDFrame<FI2<T, Integer>> overNtile(int var1);

    public JDFrame<FI2<T, Integer>> overNtile(Window<T> var1, int var2);

    public JDFrame<T> overNtileS(SetFunction<T, Integer> var1, Window<T> var2, int var3);

    public JDFrame<T> overNtileS(SetFunction<T, Integer> var1, int var2);
}

