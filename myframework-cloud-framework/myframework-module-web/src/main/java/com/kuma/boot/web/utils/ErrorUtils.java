/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  cn.hutool.core.util.ObjUtil
 *  com.kuma.boot.common.utils.exception.FastStringPrintWriter
 */
package com.kuma.boot.web.utils;

import cn.hutool.core.util.ObjUtil;
import com.kuma.boot.common.utils.exception.FastStringPrintWriter;
import com.kuma.boot.web.exception.event.ErrorEvent;
import java.io.PrintWriter;
import java.time.LocalDateTime;

public class ErrorUtils {
    public static void initErrorInfo(Throwable error, ErrorEvent event) {
        event.setStackTrace(ErrorUtils.getStackTraceAsString(error));
        event.setExceptionName(error.getClass().getName());
        event.setMessage(error.getMessage());
        event.setCreatedAt(LocalDateTime.now());
        StackTraceElement[] elements = error.getStackTrace();
        if (ObjUtil.isNotEmpty((Object)elements)) {
            StackTraceElement element = elements[0];
            event.setClassName(element.getClassName());
            event.setFileName(element.getFileName());
            event.setMethodName(element.getMethodName());
            event.setLineNumber(element.getLineNumber());
        }
    }

    public static String getStackTraceAsString(Throwable ex) {
        FastStringPrintWriter printWriter = new FastStringPrintWriter(512);
        ex.printStackTrace((PrintWriter)printWriter);
        return printWriter.toString();
    }
}

