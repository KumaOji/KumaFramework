/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.common.support.cron.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.StringTokenizer;

public abstract class StringUtils {
    public static boolean hasLength(CharSequence str) {
        return str != null && str.length() > 0;
    }

    public static boolean hasLength(String str) {
        return StringUtils.hasLength((CharSequence)str);
    }

    public static String replace(String inString, String oldPattern, String newPattern) {
        if (!StringUtils.hasLength(inString) || !StringUtils.hasLength(oldPattern) || newPattern == null) {
            return inString;
        }
        int index = inString.indexOf(oldPattern);
        if (index == -1) {
            return inString;
        }
        int capacity = inString.length();
        if (newPattern.length() > oldPattern.length()) {
            capacity += 16;
        }
        StringBuilder sb = new StringBuilder(capacity);
        int pos = 0;
        int patLen = oldPattern.length();
        while (index >= 0) {
            sb.append(inString.substring(pos, index));
            sb.append(newPattern);
            pos = index + patLen;
            index = inString.indexOf(oldPattern, pos);
        }
        sb.append(inString.substring(pos));
        return sb.toString();
    }

    public static String deleteAny(String inString, String charsToDelete) {
        if (!StringUtils.hasLength(inString) || !StringUtils.hasLength(charsToDelete)) {
            return inString;
        }
        StringBuilder sb = new StringBuilder(inString.length());
        for (int i = 0; i < inString.length(); ++i) {
            char c = inString.charAt(i);
            if (charsToDelete.indexOf(c) != -1) continue;
            sb.append(c);
        }
        return sb.toString();
    }

    public static String[] toStringArray(Collection<String> collection) {
        if (collection == null) {
            return null;
        }
        return collection.toArray(new String[collection.size()]);
    }

    public static String[] tokenizeToStringArray(String str, String delimiters) {
        return StringUtils.tokenizeToStringArray(str, delimiters, true, true);
    }

    public static String[] tokenizeToStringArray(String str, String delimiters, boolean trimTokens, boolean ignoreEmptyTokens) {
        if (str == null) {
            return null;
        }
        StringTokenizer st = new StringTokenizer(str, delimiters);
        ArrayList<String> tokens = new ArrayList<String>();
        while (st.hasMoreTokens()) {
            String token = st.nextToken();
            if (trimTokens) {
                token = token.trim();
            }
            if (ignoreEmptyTokens && token.length() <= 0) continue;
            tokens.add(token);
        }
        return StringUtils.toStringArray(tokens);
    }

    public static String[] delimitedListToStringArray(String str, String delimiter) {
        return StringUtils.delimitedListToStringArray(str, delimiter, null);
    }

    public static String[] delimitedListToStringArray(String str, String delimiter, String charsToDelete) {
        if (str == null) {
            return new String[0];
        }
        if (delimiter == null) {
            return new String[]{str};
        }
        ArrayList<String> result = new ArrayList<String>();
        if ("".equals(delimiter)) {
            for (int i = 0; i < str.length(); ++i) {
                result.add(StringUtils.deleteAny(str.substring(i, i + 1), charsToDelete));
            }
        } else {
            int delPos;
            int pos = 0;
            while ((delPos = str.indexOf(delimiter, pos)) != -1) {
                result.add(StringUtils.deleteAny(str.substring(pos, delPos), charsToDelete));
                pos = delPos + delimiter.length();
            }
            if (str.length() > 0 && pos <= str.length()) {
                result.add(StringUtils.deleteAny(str.substring(pos), charsToDelete));
            }
        }
        return StringUtils.toStringArray(result);
    }

    public static String[] commaDelimitedListToStringArray(String str) {
        return StringUtils.delimitedListToStringArray(str, ",");
    }
}

