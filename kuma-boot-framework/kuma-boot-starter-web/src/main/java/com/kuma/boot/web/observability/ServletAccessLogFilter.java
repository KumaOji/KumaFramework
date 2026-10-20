package com.kuma.boot.web.observability;

import com.kuma.boot.common.constant.CommonConstants;
import io.micrometer.tracing.Span;
import io.micrometer.tracing.Tracer;
import jakarta.servlet.AsyncEvent;
import jakarta.servlet.AsyncListener;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;
import java.util.Enumeration;
import java.util.UUID;
import java.util.Map;
import java.util.HashMap;
import com.kuma.boot.common.utils.servlet.MdcUtils;
import org.apache.skywalking.apm.toolkit.trace.TraceContext;
import java.util.concurrent.atomic.AtomicBoolean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.web.filter.OncePerRequestFilter;

/** 在服务器 observation 内、Security 前记录完成的请求，包括拒绝请求和异步完成。 */
public class ServletAccessLogFilter extends OncePerRequestFilter {
    private static final Logger log = LoggerFactory.getLogger(ServletAccessLogFilter.class);
    private final ObjectProvider<Tracer> tracers;

    public ServletAccessLogFilter(ObjectProvider<Tracer> tracers) {
        this.tracers = tracers;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return request.getRequestURI().startsWith(request.getContextPath() + "/actuator/");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        Tracer tracer = tracers.getIfAvailable();
        Span span = tracer != null ? tracer.currentSpan() : null;
        String supplied = request.getHeader(CommonConstants.KMC_TRACE_ID);
        String traceId = supplied != null && supplied.matches("[A-Za-z0-9_-]{1,128}") ? supplied
                : span != null ? span.context().traceId()
                : UUID.randomUUID().toString().replace("-", "");
        String spanId = span != null ? span.context().spanId() : "";
        String otelTraceId = span != null ? span.context().traceId() : "";
        String skywalkingId = TraceContext.traceId();
        String path = request.getRequestURI();
        String method = request.getMethod();
        long start = System.nanoTime();
        AtomicBoolean logged = new AtomicBoolean();
        HttpServletRequestWrapper correlated = new HttpServletRequestWrapper(request) {
            @Override public String getHeader(String name) {
                return CommonConstants.KMC_TRACE_ID.equalsIgnoreCase(name) ? traceId : super.getHeader(name);
            }
            @Override public Enumeration<String> getHeaders(String name) {
                return CommonConstants.KMC_TRACE_ID.equalsIgnoreCase(name)
                        ? Collections.enumeration(Collections.singleton(traceId)) : super.getHeaders(name);
            }
        };
        response.setHeader(CommonConstants.KMC_TRACE_ID, traceId);
        java.util.function.IntConsumer complete = status -> {
            if (!logged.compareAndSet(false, true)) return;
            Map<String, String> previousMdc = MdcUtils.getCopyOfContextMap();
            Map<String, String> requestMdc = new HashMap<>();
            requestMdc.put(CommonConstants.KMC_TRACE_ID, traceId);
            String tenant = response.getHeader(CommonConstants.KMC_TENANT_ID);
            String version = response.getHeader(CommonConstants.KMC_REQUEST_VERSION);
            if (tenant != null) requestMdc.put(CommonConstants.KMC_TENANT_ID, tenant);
            if (version != null) requestMdc.put(CommonConstants.KMC_REQUEST_VERSION, version);
            if (!otelTraceId.isEmpty()) requestMdc.put("traceId", otelTraceId);
            if (!spanId.isEmpty()) requestMdc.put("spanId", spanId);
            if (skywalkingId != null && !skywalkingId.equals("N/A") && !skywalkingId.equals("Ignored_Trace")) {
                requestMdc.put("tid", skywalkingId);
            }
            try {
                MdcUtils.clear();
                MdcUtils.setContextMap(requestMdc);
                var event = status >= 500 ? log.atError() : status >= 400 ? log.atWarn() : log.atInfo();
                event.addKeyValue("event", "http_access").addKeyValue("method", method)
                        .addKeyValue("path", path).addKeyValue("status", status)
                        .addKeyValue("duration_ms", (System.nanoTime() - start) / 1_000_000)
                        .addKeyValue("traceId", traceId).addKeyValue("spanId", spanId)
                        .log("HTTP request completed");
            } finally {
                MdcUtils.clear();
                if (previousMdc != null) MdcUtils.setContextMap(previousMdc);
            }
        };
        try {
            chain.doFilter(correlated, response);
        } catch (IOException | ServletException | RuntimeException error) {
            complete.accept(response.getStatus() >= 400 ? response.getStatus() : 500);
            throw error;
        } finally {
            if (request.isAsyncStarted()) {
                try {
                    request.getAsyncContext().addListener(new AsyncListener() {
                        @Override public void onComplete(AsyncEvent event) { complete.accept(response.getStatus()); }
                        @Override public void onTimeout(AsyncEvent event) { complete.accept(504); }
                        @Override public void onError(AsyncEvent event) { complete.accept(500); }
                        @Override public void onStartAsync(AsyncEvent event) { event.getAsyncContext().addListener(this); }
                    });
                } catch (IllegalStateException completed) {
                    complete.accept(response.getStatus());
                }
            } else {
                complete.accept(response.getStatus());
            }
        }
    }
}
