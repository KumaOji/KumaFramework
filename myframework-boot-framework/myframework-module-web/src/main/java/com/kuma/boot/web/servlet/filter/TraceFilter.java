/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  com.kuma.boot.common.holder.TraceContextHolder
 *  com.kuma.boot.common.utils.id.IdGeneratorUtils
 *  com.kuma.boot.common.utils.lang.StringUtils
 *  com.kuma.boot.common.utils.servlet.RequestUtils
 *  com.kuma.boot.common.utils.servlet.ResponseUtils
 *  com.kuma.boot.common.utils.servlet.TraceUtils
 *  io.micrometer.tracing.Span
 *  io.micrometer.tracing.Tracer
 *  jakarta.servlet.FilterChain
 *  jakarta.servlet.ServletException
 *  jakarta.servlet.ServletRequest
 *  jakarta.servlet.ServletResponse
 *  jakarta.servlet.http.HttpServletRequest
 *  jakarta.servlet.http.HttpServletResponse
 *  org.apache.skywalking.apm.toolkit.trace.TraceContext
 *  org.springframework.web.filter.OncePerRequestFilter
 */
package com.kuma.boot.web.servlet.filter;

import com.kuma.boot.common.holder.TraceContextHolder;
import com.kuma.boot.common.utils.id.IdGeneratorUtils;
import com.kuma.boot.common.utils.lang.StringUtils;
import com.kuma.boot.common.utils.servlet.RequestUtils;
import com.kuma.boot.common.utils.servlet.ResponseUtils;
import com.kuma.boot.common.utils.servlet.TraceUtils;
import com.kuma.boot.web.autoconfigure.properties.WebMvcFilterProperties;
import io.micrometer.tracing.Span;
import io.micrometer.tracing.Tracer;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.apache.skywalking.apm.toolkit.trace.TraceContext;
import org.springframework.web.filter.OncePerRequestFilter;

public class TraceFilter
extends OncePerRequestFilter {
    private final WebMvcFilterProperties filterProperties;
    private final Tracer tracer;

    public TraceFilter(WebMvcFilterProperties filterProperties, Tracer tracer) {
        this.filterProperties = filterProperties;
        this.tracer = tracer;
    }

    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        return RequestUtils.excludeActuator((HttpServletRequest)request);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            Span currentSpan;
            String traceId = TraceUtils.getKmcTraceIdByRequest((HttpServletRequest)request);
            if (StringUtils.isBlank((String)traceId)) {
                traceId = IdGeneratorUtils.getIdStr();
            }
            TraceContextHolder.setTraceId((String)traceId);
            TraceUtils.setKmcTraceId((String)traceId);
            String otlpTraceId = null;
            String otlpSpanId = null;
            if (this.tracer != null && (currentSpan = this.tracer.currentSpan()) != null) {
                otlpTraceId = currentSpan.context().traceId();
                otlpSpanId = currentSpan.context().spanId();
            }
            TraceUtils.setOtlpTraceId((HttpServletRequest)request, otlpTraceId);
            TraceUtils.setOtlpSpanId((HttpServletRequest)request, otlpSpanId);
            ResponseUtils.addResponseHeader((HttpServletResponse)response, (String)"kmc-trace-id", (String)TraceContextHolder.getTraceId());
            ResponseUtils.addResponseHeader((HttpServletResponse)response, (String)"X-B3-TraceId", (String)TraceUtils.getOtlpTraceId());
            ResponseUtils.addResponseHeader((HttpServletResponse)response, (String)"X-B3-SpanId", (String)TraceUtils.getOtlpSpanId());
            ResponseUtils.addResponseHeader((HttpServletResponse)response, (String)"tid", (String)TraceContext.traceId());
            filterChain.doFilter((ServletRequest)request, (ServletResponse)response);
        }
        finally {
            TraceContextHolder.clear();
            TraceUtils.removeKmcTraceId();
            TraceUtils.removeOtlpTraceId();
            TraceUtils.removeOtlpSpanId();
        }
    }

    public WebMvcFilterProperties getFilterProperties() {
        return this.filterProperties;
    }
}

