/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.common.support.dataframe.iframe.window.round;

import com.kuma.boot.common.support.dataframe.iframe.window.round.WindowRange;
import java.util.List;

public class AfterRange
implements WindowRange {
    protected int n = 0;

    public AfterRange() {
    }

    public AfterRange(int n) {
        this.n = n;
    }

    @Override
    public void check() {
        if (this.n < 0) {
            throw new IllegalArgumentException("Boundary parameter values cannot be negative");
        }
    }

    @Override
    public <T> Integer getStartIndex(Integer currentRowIndex, List<T> windowList) {
        return null;
    }

    @Override
    public <T> Integer getEndIndex(Integer currentRowIndex, List<T> windowList) {
        return currentRowIndex + this.n;
    }
}

