/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.common.support.dataframe.iframe.window.round;

import com.kuma.boot.common.support.dataframe.iframe.window.round.WindowRange;
import java.util.List;

public class EndRowRange
implements WindowRange {
    @Override
    public boolean isFixedEndIndex() {
        return true;
    }

    @Override
    public <T> Integer getStartIndex(Integer currentRowIndex, List<T> windowList) {
        return null;
    }

    @Override
    public <T> Integer getEndIndex(Integer currentRowIndex, List<T> windowList) {
        return windowList.size() - 1;
    }
}

