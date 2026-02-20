/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  com.kuma.boot.common.utils.lang.StringUtils
 *  org.jspecify.annotations.Nullable
 */
package com.kuma.boot.web.utils;

import com.kuma.boot.common.utils.lang.StringUtils;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Collection;
import org.jspecify.annotations.Nullable;

public class CollectionUtils {
    public static Collection<? extends Serializable> stringToCollection(String str) {
        if (StringUtils.isBlank((String)str)) {
            return null;
        }
        String[] strArray = str.split(",");
        Long[] longs = new Long[strArray.length];
        for (int i = 0; i < strArray.length; ++i) {
            longs[i] = CollectionUtils.strToLong(strArray[i], 0L);
        }
        return CollectionUtils.arrayToCollection(longs);
    }

    public static Collection<? extends Serializable> arrayToCollection(Long[] longArray) {
        return Arrays.asList(longArray);
    }

    public static long strToLong(@Nullable String str, long defaultValue) {
        if (str == null) {
            return defaultValue;
        }
        try {
            return Long.parseLong(str);
        }
        catch (NumberFormatException nfe) {
            return defaultValue;
        }
    }

    public static long objectToLong(@Nullable Object str, long defaultValue) {
        if (str == null) {
            return defaultValue;
        }
        try {
            return Long.parseLong(String.valueOf(str));
        }
        catch (NumberFormatException nfe) {
            return defaultValue;
        }
    }
}

