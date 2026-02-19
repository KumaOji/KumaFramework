/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  cn.hutool.core.util.StrUtil
 *  com.kuma.boot.common.utils.servlet.TraceUtils
 *  io.micrometer.tracing.Span
 *  io.micrometer.tracing.Tracer
 *  jakarta.servlet.http.HttpServletRequest
 *  jakarta.servlet.http.HttpServletResponse
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.web.servlet.HandlerInterceptor
 */
package com.kuma.boot.web.mvc.interceptor;

import cn.hutool.core.util.StrUtil;
import com.kuma.boot.common.utils.servlet.TraceUtils;
import io.micrometer.tracing.Span;
import io.micrometer.tracing.Tracer;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.HandlerInterceptor;

public class TraceMdcInterceptor
implements HandlerInterceptor {
    @Autowired(required=false)
    private Tracer tracer;

    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        Span currentSpan;
        Span currentSpan2;
        String traceIdByRequest = TraceUtils.getTraceIdByRequest((HttpServletRequest)request);
        if (StrUtil.isBlank((CharSequence)traceIdByRequest) && this.tracer != null && (currentSpan2 = this.tracer.currentSpan()) != null) {
            traceIdByRequest = currentSpan2.context().traceId();
        }
        TraceUtils.setTraceId((String)traceIdByRequest);
        String spanIdByRequest = TraceUtils.getSpanIdByRequest((HttpServletRequest)request);
        if (StrUtil.isBlank((CharSequence)spanIdByRequest) && this.tracer != null && (currentSpan = this.tracer.currentSpan()) != null) {
            spanIdByRequest = currentSpan.context().spanId();
        }
        TraceUtils.setSpanId((String)spanIdByRequest);
        return true;
    }

    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        TraceUtils.removeTraceId();
        TraceUtils.removeSpanId();
    }
}

