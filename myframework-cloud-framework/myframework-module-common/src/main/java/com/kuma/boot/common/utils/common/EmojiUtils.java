/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.common.utils.common;

import com.kuma.boot.common.utils.lang.StringUtils;

public final class EmojiUtils {
    private EmojiUtils() {
    }

    public static String replaceEmoji(String text, String replaceText) {
        if (StringUtils.isEmpty(text)) {
            return text;
        }
        return text.replaceAll("[\ud800\udc00-\udbff\udfff\ud800-\udfff]", replaceText);
    }

    public static String replaceEmoji(String text) {
        return EmojiUtils.replaceEmoji(text, "");
    }
}

