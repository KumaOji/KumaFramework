/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.common.utils.collection;

import com.kuma.boot.common.utils.collection.CollectionUtils;
import java.util.ArrayList;
import java.util.Set;

public final class SetUtils {
    private SetUtils() {
    }

    public static <T> T getFirst(Set<T> set) {
        if (CollectionUtils.isEmpty(set)) {
            return null;
        }
        ArrayList<T> list = new ArrayList<T>(set);
        return (T)list.get(0);
    }
}

