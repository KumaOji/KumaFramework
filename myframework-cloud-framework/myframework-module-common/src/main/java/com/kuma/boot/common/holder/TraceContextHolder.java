/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.alibaba.ttl.TransmittableThreadLocal
 */
package com.kuma.boot.common.holder;

import com.alibaba.ttl.TransmittableThreadLocal;

public class TraceContextHolder {
    private static final ThreadLocal<String> TRACE_CONTEXT = new TransmittableThreadLocal();

    private TraceContextHolder() {
    }

    public static void setTraceId(String traceId) {
        TRACE_CONTEXT.set(traceId);
    }

    public static String getTraceId() {
        return TRACE_CONTEXT.get();
    }

    public static void clear() {
        TRACE_CONTEXT.remove();
    }
}

