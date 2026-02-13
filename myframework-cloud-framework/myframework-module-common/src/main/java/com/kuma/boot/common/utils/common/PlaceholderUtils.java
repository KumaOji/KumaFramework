/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.common.utils.common;

import com.kuma.boot.common.support.tuple.impl.Pair;
import com.kuma.boot.common.utils.lang.StringUtils;

public final class PlaceholderUtils {
    public static final String DEFAULT_PLACEHOLDER_PREFIX = "${";
    public static final String DEFAULT_PLACEHOLDER_SUFFIX = "}";
    public static final String DEFAULT_VALUE_SEPARATOR = ":";

    private PlaceholderUtils() {
    }

    public static Pair<String, String> parsePlaceholder(String rawPlaceholder) {
        String trim = StringUtils.trim(rawPlaceholder);
        if (StringUtils.isEmpty(trim)) {
            return Pair.of(null, null);
        }
        if (!trim.startsWith(DEFAULT_PLACEHOLDER_PREFIX) || !trim.endsWith(DEFAULT_PLACEHOLDER_SUFFIX)) {
            return Pair.of(null, null);
        }
        String removePrefix = trim.substring(DEFAULT_PLACEHOLDER_PREFIX.length());
        String removeSuffix = removePrefix.substring(0, removePrefix.length() - DEFAULT_PLACEHOLDER_SUFFIX.length());
        int splitIndex = removeSuffix.indexOf(DEFAULT_VALUE_SEPARATOR);
        if (splitIndex < 0) {
            return Pair.of(removeSuffix, null);
        }
        String key = removeSuffix.substring(0, splitIndex);
        String defaultValue = removeSuffix.substring(splitIndex + 1);
        return Pair.of(key, defaultValue);
    }
}

