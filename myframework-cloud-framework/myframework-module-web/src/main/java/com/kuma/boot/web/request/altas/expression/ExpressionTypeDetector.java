/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.web.request.altas.expression;

import java.util.regex.Pattern;

public class ExpressionTypeDetector {
    private static final Pattern SPEL_PLACEHOLDER_PATTERN = Pattern.compile("#\\{([^}]+)\\}");
    private static final Pattern PURE_SPEL_PATTERN = Pattern.compile("^#\\{[^}]+\\}$");

    public static ExpressionType detectType(String expression) {
        if (expression == null || expression.trim().isEmpty()) {
            return ExpressionType.PLAIN_TEXT;
        }
        String trimmed = expression.trim();
        if (PURE_SPEL_PATTERN.matcher(trimmed).matches()) {
            return ExpressionType.PURE_SPEL;
        }
        if (SPEL_PLACEHOLDER_PATTERN.matcher(trimmed).find()) {
            return ExpressionType.TEMPLATE;
        }
        return ExpressionType.PLAIN_TEXT;
    }

    public static boolean containsSpelPlaceholder(String expression) {
        if (expression == null || expression.trim().isEmpty()) {
            return false;
        }
        return SPEL_PLACEHOLDER_PATTERN.matcher(expression).find();
    }

    public static boolean isPureSpel(String expression) {
        if (expression == null || expression.trim().isEmpty()) {
            return false;
        }
        return PURE_SPEL_PATTERN.matcher(expression.trim()).matches();
    }

    public static Pattern getSpelPlaceholderPattern() {
        return SPEL_PLACEHOLDER_PATTERN;
    }
}

