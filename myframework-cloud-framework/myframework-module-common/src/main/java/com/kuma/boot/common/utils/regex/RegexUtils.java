/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.common.utils.regex;

public final class RegexUtils {
    private RegexUtils() {
    }

    public static String escape(String text) {
        return text.replace("\\", "\\\\").replace("*", "\\*").replace("+", "\\+").replace("{", "\\{").replace("}", "\\}").replace("(", "\\(").replace(")", "\\)").replace("^", "\\^").replace("$", "\\$").replace("[", "\\[").replace("]", "\\]").replace("?", "\\?").replace(",", "\\,").replace(".", "\\.").replace("&", "\\&").replace("|", "\\|");
    }
}

