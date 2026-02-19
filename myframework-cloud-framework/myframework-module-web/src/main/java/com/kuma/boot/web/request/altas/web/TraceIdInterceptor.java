/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  com.kuma.boot.common.utils.log.LogUtils
 *  jakarta.servlet.http.HttpServletRequest
 *  jakarta.servlet.http.HttpServletResponse
 *  org.springframework.util.StringUtils
 *  org.springframework.web.servlet.HandlerInterceptor
 */
package com.kuma.boot.web.request.altas.web;

import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.boot.web.request.altas.context.TraceIdHolder;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;

public class TraceIdInterceptor
implements HandlerInterceptor {
    private final String headerName;

    public TraceIdInterceptor(String headerName) {
        this.headerName = headerName;
    }

    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        LogUtils.debug((String)"TraceIdInterceptor.preHandle called - URI: {}", (Object[])new Object[]{request.getRequestURI()});
        String existingTraceId = TraceIdHolder.getTraceIdIfPresent();
        if (existingTraceId != null) {
            response.setHeader(this.headerName, existingTraceId);
            LogUtils.debug((String)"Reusing existing TraceId: {}", (Object[])new Object[]{existingTraceId});
            return true;
        }
        String traceId = request.getHeader(this.headerName);
        LogUtils.debug((String)"Obtained TraceId from request header: {} (header: {})", (Object[])new Object[]{traceId, this.headerName});
        if (!StringUtils.hasText((String)traceId)) {
            traceId = TraceIdHolder.generateTraceId();
            LogUtils.debug((String)"Generated new TraceId: {}", (Object[])new Object[]{traceId});
        }
        TraceIdHolder.setTraceId(traceId);
        response.setHeader(this.headerName, traceId);
        LogUtils.debug((String)"TraceId setup completed: {}", (Object[])new Object[]{traceId});
        return true;
    }

    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        String currentTraceId = TraceIdHolder.getTraceIdIfPresent();
        LogUtils.debug((String)"TraceIdInterceptor completed - current TraceId: {}", (Object[])new Object[]{currentTraceId});
    }
}

