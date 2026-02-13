/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.LoggerFactory
 *  org.slf4j.spi.LocationAwareLogger
 */
package com.kuma.boot.common.utils.log;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Arrays;
import org.slf4j.LoggerFactory;
import org.slf4j.spi.LocationAwareLogger;

public class LogUtils {
    private static final Object[] EMPTY_ARRAY = new Object[0];
    private static final String FQDN = LogUtils.class.getName();

    private LogUtils() {
    }

    public static LocationAwareLogger getLocationAwareLogger() {
        StackTraceElement[] stackTraceElement = Thread.currentThread().getStackTrace();
        StackTraceElement frame = stackTraceElement[stackTraceElement.length - 1];
        return (LocationAwareLogger)LoggerFactory.getLogger((String)(frame.getClassName() + "-" + frame.getMethodName().split("\\$")[0] + "-" + frame.getLineNumber()));
    }

    public static void debug(String msg, Object ... arguments) {
        if (LogUtils.isDebugEnabled()) {
            LogUtils.getLocationAwareLogger().log(null, FQDN, 10, msg, arguments, null);
        }
    }

    public static void info(String msg, Object ... arguments) {
        if (LogUtils.isInfoEnabled()) {
            LogUtils.getLocationAwareLogger().log(null, FQDN, 20, msg, arguments, null);
        }
    }

    public static void started(Class<?> cls, String project, String ... message) {
        StringBuilder sb = new StringBuilder();
        sb.append("[").append(project).append("] ");
        sb.append("[").append(cls.getName()).append("] ");
        if (message.length > 0) {
            sb.append(Arrays.toString(message)).append(" ");
        }
        sb.append("started");
        LogUtils.info(sb.toString(), new Object[0]);
    }

    public static void warn(String msg, Object ... arguments) {
        if (LogUtils.isWarnEnabled()) {
            LogUtils.getLocationAwareLogger().log(null, FQDN, 30, msg, arguments, null);
        }
    }

    public static void trace(String msg, Object ... arguments) {
        if (LogUtils.isTraceEnabled()) {
            LogUtils.getLocationAwareLogger().log(null, FQDN, 0, msg, arguments, null);
        }
    }

    public static void error(Throwable error, String msg, Object ... arguments) {
        if (LogUtils.isErrorEnabled()) {
            LogUtils.getLocationAwareLogger().log(null, FQDN, 40, msg, arguments, error);
        }
    }

    public static void error(Throwable error) {
        if (LogUtils.isErrorEnabled()) {
            LogUtils.getLocationAwareLogger().log(null, FQDN, 40, null, EMPTY_ARRAY, error);
        }
    }

    public static void error(String msg, Object ... arguments) {
        if (LogUtils.isErrorEnabled()) {
            LogUtils.getLocationAwareLogger().log(null, FQDN, 40, msg, arguments, null);
        }
    }

    /*
     * Enabled aggressive exception aggregation
     */
    public static String exceptionToString(Exception e) {
        if (e == null) {
            return "\u65e0\u5177\u4f53\u5f02\u5e38\u4fe1\u606f";
        }
        try (StringWriter sw = new StringWriter();){
            PrintWriter pw = new PrintWriter(sw);
            try {
                e.printStackTrace(pw);
                String string = sw.toString();
                pw.close();
                return string;
            }
            catch (Throwable throwable) {
                try {
                    pw.close();
                }
                catch (Throwable throwable2) {
                    throwable.addSuppressed(throwable2);
                }
                throw throwable;
            }
        }
        catch (Exception ex) {
            return "";
        }
    }

    public static String getStackTrace(Throwable throwable) {
        StringWriter sw = new StringWriter();
        try (PrintWriter pw = new PrintWriter(sw);){
            throwable.printStackTrace(pw);
            String string = sw.toString();
            return string;
        }
    }

    public static int getRequestType(String methodName) {
        if (methodName.startsWith("get")) {
            return 1;
        }
        if (methodName.startsWith("query")) {
            return 1;
        }
        if (methodName.startsWith("find")) {
            return 1;
        }
        if (methodName.startsWith("select")) {
            return 1;
        }
        if (methodName.startsWith("add")) {
            return 2;
        }
        if (methodName.startsWith("save")) {
            return 2;
        }
        if (methodName.startsWith("update")) {
            return 3;
        }
        if (methodName.startsWith("delete")) {
            return 4;
        }
        return 1;
    }

    public static boolean isDebugEnabled() {
        return LogUtils.getLocationAwareLogger().isDebugEnabled();
    }

    public static boolean isInfoEnabled() {
        return LogUtils.getLocationAwareLogger().isInfoEnabled();
    }

    public static boolean isTraceEnabled() {
        return LogUtils.getLocationAwareLogger().isTraceEnabled();
    }

    public static boolean isErrorEnabled() {
        return LogUtils.getLocationAwareLogger().isErrorEnabled();
    }

    public static boolean isWarnEnabled() {
        return LogUtils.getLocationAwareLogger().isWarnEnabled();
    }
}

