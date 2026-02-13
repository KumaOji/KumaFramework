/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.common.extension.active;

import com.kuma.boot.common.extension.Activate;
import java.util.Comparator;

public class ActivateComparator
implements Comparator<Object> {
    public static final Comparator<Object> COMPARATOR = new ActivateComparator();

    @Override
    public int compare(Object o1, Object o2) {
        if (o1 == null && o2 == null) {
            return 0;
        }
        if (o1 == null) {
            return -1;
        }
        if (o2 == null) {
            return 1;
        }
        if (o1.equals(o2)) {
            return 0;
        }
        return this.getOrder(o1.getClass()) > this.getOrder(o2.getClass()) ? 1 : -1;
    }

    private int getOrder(Class<?> clazz) {
        if (clazz.isAnnotationPresent(Activate.class)) {
            Activate activate = clazz.getAnnotation(Activate.class);
            return activate.order();
        }
        return 0;
    }
}

