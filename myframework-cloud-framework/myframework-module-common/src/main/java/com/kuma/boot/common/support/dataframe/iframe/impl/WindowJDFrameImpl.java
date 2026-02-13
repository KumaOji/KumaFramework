/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.common.support.dataframe.iframe.impl;

import com.kuma.boot.common.support.dataframe.iframe.WindowJDFrame;
import com.kuma.boot.common.support.dataframe.iframe.function.SetFunction;
import com.kuma.boot.common.support.dataframe.iframe.impl.JDFrameImpl;
import com.kuma.boot.common.support.dataframe.iframe.item.FI2;
import com.kuma.boot.common.support.dataframe.iframe.window.Window;
import java.math.BigDecimal;
import java.util.List;
import java.util.function.Function;

public class WindowJDFrameImpl<T>
extends JDFrameImpl<T>
implements WindowJDFrame<T> {
    public WindowJDFrameImpl(Window<T> window, List<T> data) {
        super(data);
        this.window = window;
    }

    protected <R> WindowJDFrameImpl<R> returnWDF(Window<R> window, List<R> stream) {
        WindowJDFrameImpl<R> frame = new WindowJDFrameImpl<R>(window, stream);
        this.transmitMember(this, frame);
        return frame;
    }

    @Override
    public JDFrameImpl<FI2<T, Integer>> overRowNumber() {
        return super.overRowNumber(this.window);
    }

    @Override
    public WindowJDFrameImpl<T> overRowNumberS(SetFunction<T, Integer> setFunction) {
        return this.returnWDF(this.window, ((JDFrameImpl)this.overRowNumberS((SetFunction)setFunction, this.window)).viewList());
    }

    @Override
    public JDFrameImpl<FI2<T, Integer>> overRank() {
        return super.overRank(this.window);
    }

    @Override
    public WindowJDFrameImpl<T> overRankS(SetFunction<T, Integer> setFunction) {
        return this.returnWDF(this.window, ((JDFrameImpl)this.overRankS((SetFunction)setFunction, this.window)).viewList());
    }

    @Override
    public JDFrameImpl<FI2<T, Integer>> overDenseRank() {
        return super.overDenseRank(this.window);
    }

    @Override
    public WindowJDFrameImpl<T> overDenseRankS(SetFunction<T, Integer> setFunction) {
        return this.returnWDF(this.window, ((JDFrameImpl)this.overDenseRankS((SetFunction)setFunction, this.window)).viewList());
    }

    @Override
    public JDFrameImpl<FI2<T, BigDecimal>> overPercentRank() {
        return super.overPercentRank(this.window);
    }

    @Override
    public WindowJDFrameImpl<T> overPercentRankS(SetFunction<T, BigDecimal> setFunction) {
        return this.returnWDF(this.window, ((JDFrameImpl)this.overPercentRankS((SetFunction)setFunction, this.window)).viewList());
    }

    @Override
    public JDFrameImpl<FI2<T, BigDecimal>> overCumeDist() {
        return super.overCumeDist(this.window);
    }

    @Override
    public WindowJDFrameImpl<T> overCumeDistS(SetFunction<T, BigDecimal> setFunction) {
        return this.returnWDF(this.window, ((JDFrameImpl)this.overCumeDistS((SetFunction)setFunction, this.window)).viewList());
    }

    @Override
    public <F> JDFrameImpl<FI2<T, F>> overLag(Function<T, F> field, int n) {
        return super.overLag(this.window, (Function)field, n);
    }

    @Override
    public <F> WindowJDFrameImpl<T> overLagS(SetFunction<T, F> setFunction, Function<T, F> field, int n) {
        return this.returnWDF(this.window, ((JDFrameImpl)super.overLagS((SetFunction)setFunction, this.window, (Function)field, n)).viewList());
    }

    @Override
    public <F> JDFrameImpl<FI2<T, F>> overLead(Function<T, F> field, int n) {
        return super.overLead(this.window, (Function)field, n);
    }

    @Override
    public <F> WindowJDFrameImpl<T> overLeadS(SetFunction<T, F> setFunction, Function<T, F> field, int n) {
        return this.returnWDF(this.window, ((JDFrameImpl)super.overLeadS((SetFunction)setFunction, this.window, (Function)field, n)).viewList());
    }

    @Override
    public <F> JDFrameImpl<FI2<T, F>> overNthValue(Function<T, F> field, int n) {
        return super.overNthValue(this.window, (Function)field, n);
    }

    @Override
    public <F> WindowJDFrameImpl<T> overNthValueS(SetFunction<T, F> setFunction, Function<T, F> field, int n) {
        return this.returnWDF(this.window, ((JDFrameImpl)super.overNthValueS((SetFunction)setFunction, this.window, (Function)field, n)).viewList());
    }

    @Override
    public <F> WindowJDFrameImpl<T> overFirstValueS(SetFunction<T, F> setFunction, Function<T, F> field) {
        return this.returnWDF(this.window, ((JDFrameImpl)super.overFirstValueS((SetFunction)setFunction, this.window, (Function)field)).viewList());
    }

    @Override
    public <F> JDFrameImpl<FI2<T, F>> overFirstValue(Function<T, F> field) {
        return super.overNthValue(this.window, (Function)field, 1);
    }

    @Override
    public <F> JDFrameImpl<FI2<T, F>> overLastValue(Function<T, F> field) {
        return super.overNthValue(this.window, (Function)field, -1);
    }

    @Override
    public <F> WindowJDFrameImpl<T> overLastValueS(SetFunction<T, F> setFunction, Function<T, F> field) {
        return this.returnWDF(this.window, ((JDFrameImpl)super.overLastValueS((SetFunction)setFunction, this.window, (Function)field)).viewList());
    }

    @Override
    public <F> JDFrameImpl<FI2<T, BigDecimal>> overSum(Function<T, F> field) {
        return super.overSum(this.window, (Function)field);
    }

    @Override
    public <F> WindowJDFrameImpl<T> overSumS(SetFunction<T, BigDecimal> setFunction, Function<T, F> field) {
        return this.returnWDF(this.window, ((JDFrameImpl)this.overSumS((SetFunction)setFunction, this.window, (Function)field)).viewList());
    }

    @Override
    public <F> JDFrameImpl<FI2<T, BigDecimal>> overAvg(Function<T, F> field) {
        return this.overAvg(this.window, (Function)field);
    }

    @Override
    public <F> WindowJDFrameImpl<T> overAvgS(SetFunction<T, BigDecimal> setFunction, Function<T, F> field) {
        return this.returnWDF(this.window, ((JDFrameImpl)this.overAvgS((SetFunction)setFunction, this.window, (Function)field)).viewList());
    }

    @Override
    public <F extends Comparable<? super F>> JDFrameImpl<FI2<T, F>> overMaxValue(Function<T, F> field) {
        return super.overMaxValue(this.window, (Function)field);
    }

    @Override
    public <F extends Comparable<? super F>> WindowJDFrameImpl<T> overMaxValueS(SetFunction<T, F> setFunction, Function<T, F> field) {
        return this.returnWDF(this.window, ((JDFrameImpl)this.overMaxValueS((SetFunction)setFunction, this.window, (Function)field)).viewList());
    }

    @Override
    public <F extends Comparable<? super F>> JDFrameImpl<FI2<T, F>> overMinValue(Function<T, F> field) {
        return super.overMinValue(this.window, (Function)field);
    }

    @Override
    public <F extends Comparable<? super F>> WindowJDFrameImpl<T> overMinValueS(SetFunction<T, F> setFunction, Function<T, F> field) {
        return this.returnWDF(this.window, ((JDFrameImpl)this.overMinValueS((SetFunction)setFunction, this.window, (Function)field)).viewList());
    }

    @Override
    public JDFrameImpl<FI2<T, Integer>> overCount() {
        return super.overCount(this.window);
    }

    @Override
    public WindowJDFrameImpl<T> overCountS(SetFunction<T, Integer> setFunction) {
        return this.returnWDF(this.window, ((JDFrameImpl)this.overCountS((SetFunction)setFunction, this.window)).viewList());
    }

    @Override
    public JDFrameImpl<FI2<T, Integer>> overNtile(int n) {
        return super.overNtile(this.window, n);
    }

    @Override
    public WindowJDFrameImpl<T> overNtileS(SetFunction<T, Integer> setFunction, int n) {
        return this.returnWDF(this.window, ((JDFrameImpl)super.overNtileS((SetFunction)setFunction, n)).viewList());
    }
}

