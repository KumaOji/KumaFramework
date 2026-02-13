/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.common.support.dataframe.iframe.window.round;

import java.util.List;

public interface WindowRange {
    default public void check() {
    }

    default public boolean isFixedStartIndex() {
        return false;
    }

    default public boolean isFixedEndIndex() {
        return false;
    }

    public <T> Integer getStartIndex(Integer var1, List<T> var2);

    public <T> Integer getEndIndex(Integer var1, List<T> var2);

    default public boolean eq(Object obj) {
        if (obj == null) {
            return false;
        }
        if (this == obj) {
            return true;
        }
        return this.getClass() == obj.getClass();
    }
}

