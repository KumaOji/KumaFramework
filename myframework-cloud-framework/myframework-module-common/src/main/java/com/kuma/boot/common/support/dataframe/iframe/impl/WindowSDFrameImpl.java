/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.common.support.dataframe.iframe.impl;

import com.kuma.boot.common.support.dataframe.iframe.WindowSDFrame;
import com.kuma.boot.common.support.dataframe.iframe.function.SetFunction;
import com.kuma.boot.common.support.dataframe.iframe.impl.SDFrameImpl;
import com.kuma.boot.common.support.dataframe.iframe.item.FI2;
import com.kuma.boot.common.support.dataframe.iframe.window.Window;
import java.math.BigDecimal;
import java.util.function.Function;
import java.util.stream.Stream;

public class WindowSDFrameImpl<T>
extends SDFrameImpl<T>
implements WindowSDFrame<T> {
    public WindowSDFrameImpl(Window<T> window, Stream<T> data) {
        super(data);
        this.window = window;
    }

    protected <R> WindowSDFrameImpl<R> returnWDF(Window<R> window, Stream<R> stream) {
        WindowSDFrameImpl<R> frame = new WindowSDFrameImpl<R>(window, stream);
        this.transmitMember(this, frame);
        return frame;
    }

    @Override
    public SDFrameImpl<FI2<T, Integer>> overRowNumber() {
        return super.overRowNumber(this.window);
    }

    @Override
    public WindowSDFrameImpl<T> overRowNumberS(SetFunction<T, Integer> setFunction) {
        return this.returnWDF(this.window, ((SDFrameImpl)this.overRowNumberS((SetFunction)setFunction, this.window)).stream());
    }

    @Override
    public SDFrameImpl<FI2<T, Integer>> overRank() {
        return super.overRank(this.window);
    }

    @Override
    public WindowSDFrameImpl<T> overRankS(SetFunction<T, Integer> setFunction) {
        return this.returnWDF(this.window, ((SDFrameImpl)this.overRankS((SetFunction)setFunction, this.window)).stream());
    }

    @Override
    public SDFrameImpl<FI2<T, Integer>> overDenseRank() {
        return super.overDenseRank(this.window);
    }

    @Override
    public WindowSDFrameImpl<T> overDenseRankS(SetFunction<T, Integer> setFunction) {
        return this.returnWDF(this.window, ((SDFrameImpl)this.overDenseRankS((SetFunction)setFunction, this.window)).stream());
    }

    @Override
    public SDFrameImpl<FI2<T, BigDecimal>> overPercentRank() {
        return super.overPercentRank(this.window);
    }

    @Override
    public WindowSDFrameImpl<T> overPercentRankS(SetFunction<T, BigDecimal> setFunction) {
        return this.returnWDF(this.window, ((SDFrameImpl)this.overPercentRankS((SetFunction)setFunction, this.window)).stream());
    }

    @Override
    public SDFrameImpl<FI2<T, BigDecimal>> overCumeDist() {
        return super.overCumeDist(this.window);
    }

    @Override
    public WindowSDFrameImpl<T> overCumeDistS(SetFunction<T, BigDecimal> setFunction) {
        return this.returnWDF(this.window, ((SDFrameImpl)this.overCumeDistS((SetFunction)setFunction, this.window)).stream());
    }

    @Override
    public <F> SDFrameImpl<FI2<T, F>> overLag(Function<T, F> field, int n) {
        return super.overLag(this.window, (Function)field, n);
    }

    @Override
    public <F> WindowSDFrameImpl<T> overLagS(SetFunction<T, F> setFunction, Function<T, F> field, int n) {
        return this.returnWDF(this.window, super.overLagS(setFunction, this.window, field, n).stream());
    }

    @Override
    public <F> SDFrameImpl<FI2<T, F>> overLead(Function<T, F> field, int n) {
        return super.overLead(this.window, (Function)field, n);
    }

    @Override
    public <F> WindowSDFrame<T> overLeadS(SetFunction<T, F> setFunction, Function<T, F> field, int n) {
        return this.returnWDF(this.window, super.overLeadS(setFunction, this.window, field, n).stream());
    }

    @Override
    public <F> SDFrameImpl<FI2<T, F>> overNthValue(Function<T, F> field, int n) {
        return super.overNthValue(this.window, (Function)field, n);
    }

    @Override
    public <F> WindowSDFrameImpl<T> overNthValueS(SetFunction<T, F> setFunction, Function<T, F> field, int n) {
        return this.returnWDF(this.window, super.overNthValueS(setFunction, this.window, field, n).stream());
    }

    @Override
    public <F> WindowSDFrameImpl<T> overFirstValueS(SetFunction<T, F> setFunction, Function<T, F> field) {
        return this.returnWDF(this.window, super.overFirstValueS(setFunction, this.window, field).stream());
    }

    @Override
    public <F> SDFrameImpl<FI2<T, F>> overFirstValue(Function<T, F> field) {
        return super.overNthValue(this.window, (Function)field, 1);
    }

    @Override
    public <F> SDFrameImpl<FI2<T, F>> overLastValue(Function<T, F> field) {
        return super.overNthValue(this.window, (Function)field, -1);
    }

    @Override
    public <F> WindowSDFrameImpl<T> overLastValueS(SetFunction<T, F> setFunction, Function<T, F> field) {
        return this.returnWDF(this.window, ((SDFrameImpl)super.overLastValueS((SetFunction)setFunction, this.window, (Function)field)).stream());
    }

    @Override
    public <F> SDFrameImpl<FI2<T, BigDecimal>> overSum(Function<T, F> field) {
        return super.overSum(this.window, (Function)field);
    }

    @Override
    public <F> WindowSDFrameImpl<T> overSumS(SetFunction<T, BigDecimal> setFunction, Function<T, F> field) {
        return this.returnWDF(this.window, ((SDFrameImpl)this.overSumS((SetFunction)setFunction, this.window, (Function)field)).stream());
    }

    @Override
    public <F> SDFrameImpl<FI2<T, BigDecimal>> overAvg(Function<T, F> field) {
        return this.overAvg(this.window, (Function)field);
    }

    @Override
    public <F> WindowSDFrameImpl<T> overAvgS(SetFunction<T, BigDecimal> setFunction, Function<T, F> field) {
        return this.returnWDF(this.window, ((SDFrameImpl)this.overAvgS((SetFunction)setFunction, this.window, (Function)field)).stream());
    }

    @Override
    public <F extends Comparable<? super F>> SDFrameImpl<FI2<T, F>> overMaxValue(Function<T, F> field) {
        return super.overMaxValue(this.window, (Function)field);
    }

    @Override
    public <F extends Comparable<? super F>> WindowSDFrameImpl<T> overMaxValueS(SetFunction<T, F> setFunction, Function<T, F> field) {
        return this.returnWDF(this.window, ((SDFrameImpl)this.overMaxValueS((SetFunction)setFunction, this.window, (Function)field)).stream());
    }

    @Override
    public <F extends Comparable<? super F>> SDFrameImpl<FI2<T, F>> overMinValue(Function<T, F> field) {
        return super.overMinValue(this.window, (Function)field);
    }

    @Override
    public <F extends Comparable<? super F>> WindowSDFrameImpl<T> overMinValueS(SetFunction<T, F> setFunction, Function<T, F> field) {
        return this.returnWDF(this.window, ((SDFrameImpl)this.overMinValueS((SetFunction)setFunction, this.window, (Function)field)).stream());
    }

    @Override
    public SDFrameImpl<FI2<T, Integer>> overCount() {
        return super.overCount(this.window);
    }

    @Override
    public WindowSDFrameImpl<T> overCountS(SetFunction<T, Integer> setFunction) {
        return this.returnWDF(this.window, ((SDFrameImpl)this.overCountS((SetFunction)setFunction, this.window)).stream());
    }

    @Override
    public SDFrameImpl<FI2<T, Integer>> overNtile(int n) {
        return super.overNtile(this.window, n);
    }

    @Override
    public WindowSDFrameImpl<T> overNtileS(SetFunction<T, Integer> setFunction, int n) {
        return this.returnWDF(this.window, ((SDFrameImpl)super.overNtileS((SetFunction)setFunction, n)).stream());
    }
}

