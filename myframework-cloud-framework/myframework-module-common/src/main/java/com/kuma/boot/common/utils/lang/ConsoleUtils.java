/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 */
package com.kuma.boot.common.utils.lang;

import com.google.common.collect.Lists;
import com.kuma.boot.common.utils.collection.ArrayUtils;
import com.kuma.boot.common.utils.collection.CollectionUtils;
import com.kuma.boot.common.utils.date.DateUtils;
import com.kuma.boot.common.utils.log.LogUtils;
import java.util.ArrayList;

public final class ConsoleUtils {
    public static final String LINE = "--------------------------------------------------------";

    private ConsoleUtils() {
    }

    public static void info(String className, String methodName, String format, Object ... args) {
        String formatStr = ConsoleUtils.buildString(format, args);
        ConsoleUtils.log("INFO", className, methodName, formatStr, null);
    }

    public static void info(String format, Object ... args) {
        StackTraceElement callMethodElem = Thread.currentThread().getStackTrace()[3];
        String className = callMethodElem.getClassName();
        String methodNameName = callMethodElem.getMethodName();
        ConsoleUtils.info(className, methodNameName, format, args);
    }

    private static String buildString(String format, Object[] params) {
        String stringFormat = format;
        for (int i = 0; i < params.length; ++i) {
            stringFormat = stringFormat.replaceFirst("\\{}", "%s");
        }
        return String.format(stringFormat, params);
    }

    private static void log(String level, String className, String methodName, String content, Throwable throwable) {
        String prettyMethod = ConsoleUtils.buildPrettyMethodName(className, methodName);
        String dateStr = DateUtils.getCurrentDateTimeStr();
        String log = String.format("[%s] [%s] [%s] - %s", level, dateStr, prettyMethod, content);
        if ("ERROR".equalsIgnoreCase(level)) {
            System.err.println(log);
        } else {
            LogUtils.info(log, new Object[0]);
        }
        if (throwable != null) {
            throwable.printStackTrace(System.err);
        }
    }

    private static String buildPrettyMethodName(String className, String methodName) {
        Object[] classNames = className.split("\\.");
        if (ArrayUtils.isEmpty(classNames)) {
            return methodName;
        }
        int length = classNames.length;
        if (length == 1) {
            return className + "." + methodName;
        }
        ArrayList classFirstChars = Lists.newArrayList();
        for (int i = 0; i < length - 1; ++i) {
            Object name = classNames[i];
            classFirstChars.add(String.valueOf(((String)name).charAt(0)));
        }
        classFirstChars.add(classNames[length - 1]);
        String prettyClass = CollectionUtils.join(classFirstChars, ".");
        return prettyClass + "." + methodName;
    }
}

