/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cn.hutool.core.map.MapUtil
 *  cn.hutool.core.util.StrUtil
 *  org.slf4j.MDC
 */
package com.kuma.boot.common.utils.servlet;

import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.StrUtil;
import com.kuma.boot.common.utils.servlet.LoggerContextUtils;
import java.util.Deque;
import java.util.Map;
import org.slf4j.MDC;

public class MdcUtils {
    public static void put(String key, String val) {
        if (StrUtil.isNotBlank((CharSequence)val)) {
            MDC.put((String)key, (String)val);
            LoggerContextUtils.put(key, val);
        }
    }

    public static String get(String key) {
        String s = MDC.get((String)key);
        if (StrUtil.isBlank((CharSequence)s)) {
            s = LoggerContextUtils.get(key);
        }
        return s;
    }

    public static void remove(String key) {
        MDC.remove((String)key);
        LoggerContextUtils.remove(key);
    }

    public static void clear() {
        MDC.clear();
        LoggerContextUtils.clear();
    }

    public static Map<String, String> getCopyOfContextMap() {
        Map<String, String> copyOfContextMap = MDC.getCopyOfContextMap();
        if (MapUtil.isEmpty((Map)copyOfContextMap)) {
            copyOfContextMap = LoggerContextUtils.getCopyOfContextMap();
        }
        return copyOfContextMap;
    }

    public static void setContextMap(Map<String, String> contextMap) {
        if (MapUtil.isNotEmpty(contextMap)) {
            MDC.setContextMap(contextMap);
            LoggerContextUtils.setContextMap(contextMap);
        }
    }

    public static void pushByKey(String key, String value) {
        if (StrUtil.isNotBlank((CharSequence)value)) {
            MDC.pushByKey((String)key, (String)value);
            LoggerContextUtils.pushByKey(key, value);
        }
    }

    public static String popByKey(String key) {
        String s = MDC.popByKey((String)key);
        if (StrUtil.isBlank((CharSequence)s)) {
            s = LoggerContextUtils.popByKey(key);
        }
        return s;
    }

    public static Deque<String> getCopyOfDequeByKey(String key) {
        return LoggerContextUtils.getCopyOfDequeByKey(key);
    }

    public static void clearDequeByKey(String key) {
        LoggerContextUtils.clearDequeByKey(key);
    }
}

