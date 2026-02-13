/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.common.utils.common;

import com.kuma.boot.common.utils.lang.StringUtils;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class AbbreviationUtils {
    private static final Map<String, String> MAP = new ConcurrentHashMap<String, String>();

    private AbbreviationUtils() {
    }

    public static void set(String shortName, String fullName) {
        MAP.put(shortName, fullName);
    }

    public static String get(String shortName) {
        return MAP.get(shortName);
    }

    public static String getOrDefault(String shortName, String defaultValue) {
        String value = MAP.get(shortName);
        if (StringUtils.isEmpty(value)) {
            return defaultValue;
        }
        return value;
    }

    static {
        MAP.put("impl", "implements");
        MAP.put("msg", "message");
        MAP.put("err", "error");
        MAP.put("e", "exception");
        MAP.put("ex", "exception");
        MAP.put("doc", "document");
        MAP.put("val", "value");
        MAP.put("num", "number");
        MAP.put("vo", "value object");
        MAP.put("dto", "data transfer object");
        MAP.put("gen", "generate");
        MAP.put("dir", "directory");
        MAP.put("init", "initialize");
        MAP.put("cfg", "config");
        MAP.put("arg", "argument");
        MAP.put("args", "arguments");
    }
}

