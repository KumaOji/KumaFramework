/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.common.support.dataframe.iframe.window.round;

import com.kuma.boot.common.support.dataframe.iframe.window.round.AfterRange;
import com.kuma.boot.common.support.dataframe.iframe.window.round.BeforeRange;
import com.kuma.boot.common.support.dataframe.iframe.window.round.CurrentRowRange;
import com.kuma.boot.common.support.dataframe.iframe.window.round.EndRowRange;
import com.kuma.boot.common.support.dataframe.iframe.window.round.StartRowRange;
import com.kuma.boot.common.support.dataframe.iframe.window.round.WindowRange;

public class Range {
    public static final WindowRange START_ROW = new StartRowRange();
    public static final WindowRange BEFORE_ROW = new BeforeRange(0);
    public static final WindowRange CURRENT_ROW = new CurrentRowRange();
    public static final WindowRange AFTER_ROW = new AfterRange(0);
    public static final WindowRange END_ROW = new EndRowRange();

    private Range() {
    }

    public static WindowRange BEFORE(int n) {
        return new BeforeRange(n);
    }

    public static WindowRange AFTER(int n) {
        return new AfterRange(n);
    }
}

