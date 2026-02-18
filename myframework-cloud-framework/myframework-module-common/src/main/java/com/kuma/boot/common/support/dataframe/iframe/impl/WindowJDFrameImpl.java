/*
 * Copyright (c) 2020-2030, kuma (2569277704@qq.com & https://blog.kumacloud.top/).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.kuma.boot.common.support.dataframe.iframe.impl;

import com.kuma.boot.common.support.dataframe.iframe.WindowJDFrame;
import com.kuma.boot.common.support.dataframe.iframe.function.SetFunction;
import com.kuma.boot.common.support.dataframe.iframe.item.FI2;
import com.kuma.boot.common.support.dataframe.iframe.window.Window;
import java.math.BigDecimal;
import java.util.List;
import java.util.function.Function;

/**
 * @author caizhihao
 * @param <T>
 */
public class WindowJDFrameImpl<T> extends JDFrameImpl<T> implements WindowJDFrame<T> {

    public WindowJDFrameImpl(Window<T> window, List<T> data) {
        super(data);
        this.window = window;
    }

    protected <R> WindowJDFrameImpl<R> returnWDF(Window<R> window, List<R> stream) {
        WindowJDFrameImpl<R> frame = new WindowJDFrameImpl<>(window, stream);
        transmitMember(this, frame);
        return frame;
    }

    @Override
    public JDFrameImpl<FI2<T, Integer>> overRowNumber() {
        return super.overRowNumber(this.window);
    }

    @Override
    public WindowJDFrameImpl<T> overRowNumberS(SetFunction<T, Integer> setFunction) {
        return returnWDF(this.window, overRowNumberS(setFunction, this.window).viewList());
    }

    @Override
    public JDFrameImpl<FI2<T, Integer>> overRank() {
        return super.overRank(this.window);
    }

    @Override
    public WindowJDFrameImpl<T> overRankS(SetFunction<T, Integer> setFunction) {
        return returnWDF(this.window, overRankS(setFunction, this.window).viewList());
    }

    @Override
    public JDFrameImpl<FI2<T, Integer>> overDenseRank() {
        return super.overDenseRank(this.window);
    }

    @Override
    public WindowJDFrameImpl<T> overDenseRankS(SetFunction<T, Integer> setFunction) {
        return returnWDF(this.window, overDenseRankS(setFunction, this.window).viewList());
    }

    @Override
    public JDFrameImpl<FI2<T, BigDecimal>> overPercentRank() {
        return super.overPercentRank(this.window);
    }

    @Override
    public WindowJDFrameImpl<T> overPercentRankS(SetFunction<T, BigDecimal> setFunction) {
        ;
        return returnWDF(this.window, overPercentRankS(setFunction, this.window).viewList());
    }

    @Override
    public JDFrameImpl<FI2<T, BigDecimal>> overCumeDist() {
        return super.overCumeDist(this.window);
    }

    @Override
    public WindowJDFrameImpl<T> overCumeDistS(SetFunction<T, BigDecimal> setFunction) {
        return returnWDF(this.window, overCumeDistS(setFunction, this.window).viewList());
    }

    @Override
    public <F> JDFrameImpl<FI2<T, F>> overLag(Function<T, F> field, int n) {
        return super.overLag(this.window, field, n);
    }

    @Override
    public <F> WindowJDFrameImpl<T> overLagS(
            SetFunction<T, F> setFunction, Function<T, F> field, int n) {
        return returnWDF(
                this.window, super.overLagS(setFunction, this.window, field, n).viewList());
    }

    @Override
    public <F> JDFrameImpl<FI2<T, F>> overLead(Function<T, F> field, int n) {
        return super.overLead(this.window, field, n);
    }

    @Override
    public <F> WindowJDFrameImpl<T> overLeadS(
            SetFunction<T, F> setFunction, Function<T, F> field, int n) {
        return returnWDF(
                this.window, super.overLeadS(setFunction, this.window, field, n).viewList());
    }

    @Override
    public <F> JDFrameImpl<FI2<T, F>> overNthValue(Function<T, F> field, int n) {
        return super.overNthValue(this.window, field, n);
    }

    @Override
    public <F> WindowJDFrameImpl<T> overNthValueS(
            SetFunction<T, F> setFunction, Function<T, F> field, int n) {
        return returnWDF(
                this.window, super.overNthValueS(setFunction, this.window, field, n).viewList());
    }

    @Override
    public <F> WindowJDFrameImpl<T> overFirstValueS(
            SetFunction<T, F> setFunction, Function<T, F> field) {
        return returnWDF(
                this.window, super.overFirstValueS(setFunction, this.window, field).viewList());
    }

    @Override
    public <F> JDFrameImpl<FI2<T, F>> overFirstValue(Function<T, F> field) {
        return super.overNthValue(this.window, field, 1);
    }

    @Override
    public <F> JDFrameImpl<FI2<T, F>> overLastValue(Function<T, F> field) {
        return super.overNthValue(this.window, field, -1);
    }

    @Override
    public <F> WindowJDFrameImpl<T> overLastValueS(
            SetFunction<T, F> setFunction, Function<T, F> field) {
        return returnWDF(
                this.window, super.overLastValueS(setFunction, this.window, field).viewList());
    }

    @Override
    public <F> JDFrameImpl<FI2<T, BigDecimal>> overSum(Function<T, F> field) {
        return super.overSum(this.window, field);
    }

    @Override
    public <F> WindowJDFrameImpl<T> overSumS(
            SetFunction<T, BigDecimal> setFunction, Function<T, F> field) {
        return returnWDF(this.window, overSumS(setFunction, this.window, field).viewList());
    }

    @Override
    public <F> JDFrameImpl<FI2<T, BigDecimal>> overAvg(Function<T, F> field) {
        return overAvg(this.window, field);
    }

    @Override
    public <F> WindowJDFrameImpl<T> overAvgS(
            SetFunction<T, BigDecimal> setFunction, Function<T, F> field) {
        return returnWDF(this.window, overAvgS(setFunction, this.window, field).viewList());
    }

    @Override
    public <F extends Comparable<? super F>> JDFrameImpl<FI2<T, F>> overMaxValue(
            Function<T, F> field) {
        return super.overMaxValue(this.window, field);
    }

    @Override
    public <F extends Comparable<? super F>> WindowJDFrameImpl<T> overMaxValueS(
            SetFunction<T, F> setFunction, Function<T, F> field) {
        return returnWDF(this.window, overMaxValueS(setFunction, this.window, field).viewList());
    }

    @Override
    public <F extends Comparable<? super F>> JDFrameImpl<FI2<T, F>> overMinValue(
            Function<T, F> field) {
        return super.overMinValue(this.window, field);
    }

    @Override
    public <F extends Comparable<? super F>> WindowJDFrameImpl<T> overMinValueS(
            SetFunction<T, F> setFunction, Function<T, F> field) {
        return returnWDF(this.window, overMinValueS(setFunction, this.window, field).viewList());
    }

    @Override
    public JDFrameImpl<FI2<T, Integer>> overCount() {
        return super.overCount(this.window);
    }

    @Override
    public WindowJDFrameImpl<T> overCountS(SetFunction<T, Integer> setFunction) {
        return returnWDF(this.window, overCountS(setFunction, this.window).viewList());
    }

    @Override
    public JDFrameImpl<FI2<T, Integer>> overNtile(int n) {
        return super.overNtile(this.window, n);
    }

    @Override
    public WindowJDFrameImpl<T> overNtileS(SetFunction<T, Integer> setFunction, int n) {
        return returnWDF(this.window, super.overNtileS(setFunction, n).viewList());
    }
}
