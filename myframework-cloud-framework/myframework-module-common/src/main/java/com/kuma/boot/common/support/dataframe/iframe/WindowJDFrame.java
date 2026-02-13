/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.common.support.dataframe.iframe;

import com.kuma.boot.common.support.dataframe.iframe.JDFrame;
import com.kuma.boot.common.support.dataframe.iframe.function.SetFunction;
import com.kuma.boot.common.support.dataframe.iframe.item.FI2;
import java.math.BigDecimal;
import java.util.function.Function;

public interface WindowJDFrame<T>
extends JDFrame<T> {
    @Override
    public JDFrame<FI2<T, Integer>> overRowNumber();

    @Override
    public WindowJDFrame<T> overRowNumberS(SetFunction<T, Integer> var1);

    public JDFrame<FI2<T, Integer>> overRank();

    public WindowJDFrame<T> overRankS(SetFunction<T, Integer> var1);

    public JDFrame<FI2<T, Integer>> overDenseRank();

    public WindowJDFrame<T> overDenseRankS(SetFunction<T, Integer> var1);

    public JDFrame<FI2<T, BigDecimal>> overPercentRank();

    public WindowJDFrame<T> overPercentRankS(SetFunction<T, BigDecimal> var1);

    public JDFrame<FI2<T, BigDecimal>> overCumeDist();

    public WindowJDFrame<T> overCumeDistS(SetFunction<T, BigDecimal> var1);

    @Override
    public <F> JDFrame<FI2<T, F>> overLag(Function<T, F> var1, int var2);

    @Override
    public <F> WindowJDFrame<T> overLagS(SetFunction<T, F> var1, Function<T, F> var2, int var3);

    @Override
    public <F> JDFrame<FI2<T, F>> overLead(Function<T, F> var1, int var2);

    @Override
    public <F> WindowJDFrame<T> overLeadS(SetFunction<T, F> var1, Function<T, F> var2, int var3);

    @Override
    public <F> JDFrame<FI2<T, F>> overNthValue(Function<T, F> var1, int var2);

    @Override
    public <F> WindowJDFrame<T> overNthValueS(SetFunction<T, F> var1, Function<T, F> var2, int var3);

    @Override
    public <F> JDFrame<FI2<T, F>> overFirstValue(Function<T, F> var1);

    @Override
    public <F> WindowJDFrame<T> overFirstValueS(SetFunction<T, F> var1, Function<T, F> var2);

    @Override
    public <F> JDFrame<FI2<T, F>> overLastValue(Function<T, F> var1);

    @Override
    public <F> WindowJDFrame<T> overLastValueS(SetFunction<T, F> var1, Function<T, F> var2);

    @Override
    public <F> JDFrame<FI2<T, BigDecimal>> overSum(Function<T, F> var1);

    @Override
    public <F> WindowJDFrame<T> overSumS(SetFunction<T, BigDecimal> var1, Function<T, F> var2);

    @Override
    public <F> JDFrame<FI2<T, BigDecimal>> overAvg(Function<T, F> var1);

    @Override
    public <F> WindowJDFrame<T> overAvgS(SetFunction<T, BigDecimal> var1, Function<T, F> var2);

    @Override
    public <F extends Comparable<? super F>> JDFrame<FI2<T, F>> overMaxValue(Function<T, F> var1);

    @Override
    public <F extends Comparable<? super F>> WindowJDFrame<T> overMaxValueS(SetFunction<T, F> var1, Function<T, F> var2);

    @Override
    public <F extends Comparable<? super F>> JDFrame<FI2<T, F>> overMinValue(Function<T, F> var1);

    @Override
    public <F extends Comparable<? super F>> WindowJDFrame<T> overMinValueS(SetFunction<T, F> var1, Function<T, F> var2);

    @Override
    public JDFrame<FI2<T, Integer>> overCount();

    @Override
    public WindowJDFrame<T> overCountS(SetFunction<T, Integer> var1);

    @Override
    public JDFrame<FI2<T, Integer>> overNtile(int var1);

    @Override
    public WindowJDFrame<T> overNtileS(SetFunction<T, Integer> var1, int var2);
}

