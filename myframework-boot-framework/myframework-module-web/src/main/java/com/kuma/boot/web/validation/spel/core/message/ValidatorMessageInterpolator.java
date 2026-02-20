/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.web.validation.spel.core.message;

import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ValidatorMessageInterpolator {
    private static final Pattern LEFT_BRACE = Pattern.compile("\\{", 16);
    private static final Pattern RIGHT_BRACE = Pattern.compile("\\}", 16);
    private static final Pattern SLASH = Pattern.compile("\\\\", 16);

    public String interpolate(String message, Locale locale, Object ... args) {
        return this.interpolateMessage(message, locale, args);
    }

    private String interpolateMessage(String message, Locale locale, Object ... args) {
        if (message.indexOf(123) < 0) {
            return this.replaceEscapedLiterals(message);
        }
        String resolvedMessage = this.resolveMessage(message, locale, args);
        resolvedMessage = this.replaceEscapedLiterals(resolvedMessage);
        return resolvedMessage;
    }

    private String resolveMessage(String message, Locale locale, Object ... args) {
        if (message.charAt(0) != '{' || message.charAt(message.length() - 1) != '}') {
            return message;
        }
        String key = message.substring(1, message.length() - 1);
        return ResourceBundleMessageResolver.getMessage(key, locale, args);
    }

    private String replaceEscapedLiterals(String resolvedMessage) {
        if (resolvedMessage.indexOf(92) > -1) {
            resolvedMessage = LEFT_BRACE.matcher(resolvedMessage).replaceAll("{");
            resolvedMessage = RIGHT_BRACE.matcher(resolvedMessage).replaceAll("}");
            resolvedMessage = SLASH.matcher(resolvedMessage).replaceAll(Matcher.quoteReplacement("\\"));
        }
        return resolvedMessage;
    }
}

