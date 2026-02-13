/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.common.extension.strategy;

import java.util.Comparator;

public interface Prioritized
extends Comparable<Prioritized> {
    public static final int MAX_PRIORITY = Integer.MIN_VALUE;
    public static final int MIN_PRIORITY = Integer.MAX_VALUE;
    public static final int NORMAL_PRIORITY = 0;
    public static final Comparator<Object> COMPARATOR = (one, two) -> {
        boolean b1 = one instanceof Prioritized;
        boolean b2 = two instanceof Prioritized;
        if (b1 && !b2) {
            return -1;
        }
        if (b2 && !b1) {
            return 1;
        }
        if (b1) {
            return ((Prioritized)one).compareTo((Prioritized)two);
        }
        return 0;
    };

    default public int getPriority() {
        return 0;
    }

    @Override
    default public int compareTo(Prioritized that) {
        return Integer.compare(this.getPriority(), that.getPriority());
    }
}

