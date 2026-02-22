/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.StringUtils
 */
package com.kuma.boot.security.spring.enums;

import org.apache.commons.lang3.StringUtils;

public enum Category {
    WILDCARD,
    PLACEHOLDER,
    FULL_PATH;


    public static Category getCategory(String url) {
        if (StringUtils.containsAny((CharSequence)url, (CharSequence[])new String[]{"*", "?"})) {
            return WILDCARD;
        }
        if (StringUtils.contains((CharSequence)url, (CharSequence)"{")) {
            return PLACEHOLDER;
        }
        return FULL_PATH;
    }
}

