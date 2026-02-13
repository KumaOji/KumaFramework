/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.common.support.dataframe.iframe.window.round;

import com.kuma.boot.common.support.dataframe.iframe.window.round.WindowRange;
import java.util.List;

public class StartRowRange
implements WindowRange {
    @Override
    public boolean isFixedStartIndex() {
        return true;
    }

    @Override
    public <T> Integer getStartIndex(Integer currentRowIndex, List<T> windowList) {
        return 0;
    }

    @Override
    public <T> Integer getEndIndex(Integer currentRowIndex, List<T> windowList) {
        return null;
    }
}

