/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.collections4.CollectionUtils
 */
package com.kuma.boot.security.spring.utils;

import java.util.List;
import org.apache.commons.collections4.CollectionUtils;

public class ListUtils {
    public static List<String> merge(List<String> appendResources, List<String> defaultResources) {
        if (CollectionUtils.isEmpty(appendResources)) {
            return defaultResources;
        }
        return CollectionUtils.collate(defaultResources, appendResources);
    }

    public static String[] toStringArray(List<String> resources) {
        if (CollectionUtils.isNotEmpty(resources)) {
            String[] result = new String[resources.size()];
            return resources.toArray(result);
        }
        return new String[0];
    }
}

