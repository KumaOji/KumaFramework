/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  ch.qos.logback.classic.LoggerContext
 *  org.slf4j.ILoggerFactory
 *  org.slf4j.LoggerFactory
 */
package com.kuma.boot.common.utils.servlet;

import ch.qos.logback.classic.LoggerContext;
import java.util.Deque;
import java.util.Map;
import org.slf4j.ILoggerFactory;
import org.slf4j.LoggerFactory;

public class LoggerContextUtils {
    public static LoggerContext loggerContext = null;

    public static void put(String key, String val) {
        if (null != loggerContext) {
            loggerContext.getMDCAdapter().put(key, val);
        }
    }

    public static String get(String key) {
        if (null != loggerContext) {
            return loggerContext.getMDCAdapter().get(key);
        }
        return null;
    }

    public static void remove(String key) {
        if (null != loggerContext) {
            loggerContext.getMDCAdapter().remove(key);
        }
    }

    public static void clear() {
        if (null != loggerContext) {
            loggerContext.getMDCAdapter().clear();
        }
    }

    public static Map<String, String> getCopyOfContextMap() {
        if (null != loggerContext) {
            return loggerContext.getMDCAdapter().getCopyOfContextMap();
        }
        return null;
    }

    public static void setContextMap(Map<String, String> contextMap) {
        if (null != loggerContext) {
            loggerContext.getMDCAdapter().setContextMap(contextMap);
        }
    }

    public static void pushByKey(String key, String value) {
        if (null != loggerContext) {
            loggerContext.getMDCAdapter().pushByKey(key, value);
        }
    }

    public static String popByKey(String key) {
        if (null != loggerContext) {
            return loggerContext.getMDCAdapter().popByKey(key);
        }
        return null;
    }

    public static Deque<String> getCopyOfDequeByKey(String key) {
        if (null != loggerContext) {
            return loggerContext.getMDCAdapter().getCopyOfDequeByKey(key);
        }
        return null;
    }

    public static void clearDequeByKey(String key) {
        if (null != loggerContext) {
            loggerContext.getMDCAdapter().clearDequeByKey(key);
        }
    }

    static {
        ILoggerFactory iLoggerFactory = LoggerFactory.getILoggerFactory();
        if (iLoggerFactory instanceof LoggerContext) {
            LoggerContext loggerContextInfo;
            loggerContext = loggerContextInfo = (LoggerContext)iLoggerFactory;
        }
    }
}

