/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  org.springframework.core.task.TaskDecorator
 */
package com.kuma.boot.web.request.altas.async;

import com.kuma.boot.web.request.altas.context.TraceIdHolder;
import org.springframework.core.task.TaskDecorator;

public class TraceIdTaskDecorator
implements TaskDecorator {
    public Runnable decorate(Runnable runnable) {
        String traceId = TraceIdHolder.getTraceIdIfPresent();
        return () -> {
            String originalTraceId = null;
            try {
                originalTraceId = TraceIdHolder.getTraceIdIfPresent();
                if (traceId != null) {
                    TraceIdHolder.setTraceId(traceId);
                }
                runnable.run();
            }
            finally {
                if (originalTraceId != null) {
                    TraceIdHolder.setTraceId(originalTraceId);
                } else {
                    TraceIdHolder.clear();
                }
            }
        };
    }
}

