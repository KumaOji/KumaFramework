/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.common.support.dataframe.util;

import java.util.Collection;

public class ListUtils {
    private ListUtils() {
    }

    public static boolean isEmpty(Collection<?> coll) {
        return coll == null || coll.isEmpty();
    }

    public static boolean isNotEmpty(Collection<?> coll) {
        return !ListUtils.isEmpty(coll);
    }
}

