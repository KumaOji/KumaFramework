/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.common.utils.spider;

import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public final class HTMLUtils {
    private static final Pattern TAG_ALL = Pattern.compile("<(/?)(.*?)(/?)>");
    private static final List<String> NO_CLOSED_TAGS;
    private static final Pattern HTML_TAG_REGEX;
    private static final Pattern HTML_SCRIPT_REGEX;
    private static final Pattern HTML_STYLE_REGEX;

    private HTMLUtils() {
    }

    public static String removeTags(String html) {
        if (html == null) {
            throw new IllegalArgumentException("HTML can not be null");
        }
        if (html.length() == 0) {
            return html;
        }
        String value = html;
        for (Pattern pattern : Arrays.asList(HTML_SCRIPT_REGEX, HTML_STYLE_REGEX)) {
            value = pattern.matcher(value).replaceAll("$1$3");
        }
        return HTML_TAG_REGEX.matcher(value).replaceAll("");
    }

    public static String summarize(String html, int length, boolean retainTags) {
        if (html == null) {
            throw new IllegalArgumentException("HTML can not be null");
        }
        if (length <= 0) {
            throw new IllegalArgumentException("Length must be positive");
        }
        if (html.length() == 0) {
            return html;
        }
        LinkedList<String> stack = retainTags ? new LinkedList<String>() : null;
        StringBuilder stringBuilder = new StringBuilder(length);
        int index = 0;
        int count = 0;
        Matcher matcher = TAG_ALL.matcher(html);
        while (matcher.find()) {
            String str = html.substring(index, matcher.start()).replace(" ", "");
            int available = str.length();
            if (available > 0) {
                int need = length - count;
                if (need >= available) {
                    stringBuilder.append(str);
                    count += str.length();
                } else {
                    stringBuilder.append(str, 0, need);
                    count += need;
                    break;
                }
            }
            if (retainTags) {
                boolean isEnd = !matcher.group(1).isEmpty();
                boolean isSelfClosed = !matcher.group(3).isEmpty();
                String tag = matcher.group();
                if (!isSelfClosed) {
                    isSelfClosed = NO_CLOSED_TAGS.stream().anyMatch(tag::startsWith);
                }
                stringBuilder.append(tag);
                if (isEnd) {
                    stack.removeFirst();
                } else if (!isSelfClosed) {
                    stack.addFirst(tag);
                }
            }
            index = matcher.end();
        }
        int len = html.length();
        while (index < len && count < length) {
            char ch = html.charAt(index);
            if (ch != ' ') {
                stringBuilder.append(ch);
                ++count;
            }
            ++index;
        }
        if (retainTags) {
            while (!stack.isEmpty()) {
                String tag = (String)stack.removeFirst();
                String temp = tag.substring(1, tag.length() - 1);
                int i = temp.indexOf(32);
                if (i >= 0) {
                    temp = temp.substring(0, i);
                }
                stringBuilder.append("</").append(temp).append(">");
            }
        }
        return stringBuilder.toString();
    }

    static {
        HashSet<String> set = new HashSet<String>();
        set.add("!");
        set.add("meta");
        set.add("link");
        set.add("input");
        set.add("img");
        set.add("hr");
        set.add("br");
        set.add("area");
        set.add("base");
        set.add("col");
        set.add("command");
        set.add("embed");
        set.add("keygen");
        set.add("param");
        set.add("source");
        set.add("track");
        set.add("wbr");
        NO_CLOSED_TAGS = set.stream().map(x -> "<" + x).collect(Collectors.toList());
        HTML_TAG_REGEX = Pattern.compile("<.+?>", 8);
        HTML_SCRIPT_REGEX = Pattern.compile("(<script.*?>)(.*?)(</script>)", 10);
        HTML_STYLE_REGEX = Pattern.compile("(<style.*?>)(.*?)(</style>)", 10);
    }
}

