/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.web.request.altas.context;

import java.util.UUID;

public class TraceIdHolder {
    private static final InheritableThreadLocal<String> TRACE_ID_HOLDER = new InheritableThreadLocal();

    public static void setTraceId(String traceId) {
        TRACE_ID_HOLDER.set(traceId);
    }

    public static String getTraceId() {
        String traceId = (String)TRACE_ID_HOLDER.get();
        if (traceId == null) {
            traceId = TraceIdHolder.generateTraceId();
            TraceIdHolder.setTraceId(traceId);
        }
        return traceId;
    }

    public static String getTraceIdIfPresent() {
        return (String)TRACE_ID_HOLDER.get();
    }

    public static void clear() {
        TRACE_ID_HOLDER.remove();
    }

    public static String generateTraceId() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    public static void runWithTraceId(String traceId, Runnable runnable) {
        String originalTraceId = TraceIdHolder.getTraceIdIfPresent();
        try {
            TraceIdHolder.setTraceId(traceId);
            runnable.run();
        }
        finally {
            if (originalTraceId != null) {
                TraceIdHolder.setTraceId(originalTraceId);
            } else {
                TraceIdHolder.clear();
            }
        }
    }
}

