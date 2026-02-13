/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.common.support.cron.util;

import com.kuma.boot.common.support.cron.pojo.CronPosition;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;

public class CompareUtil {
    public static <T extends Comparable<T>> T findNext(T current, List<T> sortedList) {
        for (Comparable item : sortedList) {
            if (item.compareTo(current) < 0) continue;
            return (T)item;
        }
        throw new IllegalArgumentException("\u8d85\u51fa\u8303\u56f4\u4e86");
    }

    public static <T> boolean inList(T num, List<T> list) {
        for (T tmp : list) {
            if (!tmp.equals(num)) continue;
            return true;
        }
        return false;
    }

    public static <T> void removeDuplicate(Collection<T> list) {
        LinkedHashSet<T> set = new LinkedHashSet<T>(list.size());
        set.addAll(list);
        list.clear();
        list.addAll(set);
    }

    public static void assertSize(int left, int right) {
        if (left > right) {
            throw new IllegalArgumentException("right should bigger than left , but find " + left + " > " + right);
        }
    }

    public static void assertRange(CronPosition cronPosition, int value) {
        int min = cronPosition.getMin();
        int max = cronPosition.getMax();
        if (value < min || value > max) {
            throw new IllegalArgumentException(cronPosition.name() + " \u57df[" + min + " , " + max + "],  but find " + value);
        }
    }
}

