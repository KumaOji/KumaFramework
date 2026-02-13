/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cn.hutool.core.util.StrUtil
 *  jakarta.servlet.http.HttpServletRequest
 */
package com.kuma.boot.common.utils.servlet;

import cn.hutool.core.util.StrUtil;
import com.kuma.boot.common.utils.servlet.MdcUtils;
import jakarta.servlet.http.HttpServletRequest;

public class TraceUtils {
    private TraceUtils() {
    }

    public static String getKmcTraceIdByRequest(HttpServletRequest request) {
        String traceId = request.getParameter("kmc-trace-id");
        if (StrUtil.isBlank((CharSequence)traceId)) {
            traceId = request.getHeader("kmc-trace-id");
        }
        return traceId;
    }

    public static String getKmcTraceId() {
        return MdcUtils.get("kmc-trace-id");
    }

    public static void setKmcTraceId(String traceId) {
        MdcUtils.put("kmc-trace-id", traceId);
    }

    public static void removeKmcTraceId() {
        MdcUtils.remove("kmc-trace-id");
    }

    public static void setKmcTenantId(String tenantId) {
        MdcUtils.put("kmc-tenant-id", tenantId);
    }

    public static void removeKmcTenantId() {
        MdcUtils.remove("kmc-tenant-id");
    }

    public static String getKmcTenantId() {
        return MdcUtils.get("kmc-tenant-id");
    }

    public static void setKmcVersion(String version) {
        MdcUtils.put("kmc-request-version", version);
    }

    public static void removeKmcVersion() {
        MdcUtils.remove("kmc-request-version");
    }

    public static String getKmcVersion() {
        return MdcUtils.get("kmc-request-version");
    }

    public static String getOtlpTraceIdByRequest(HttpServletRequest request) {
        String otlpTraceId = request.getParameter("X-B3-TraceId");
        if (StrUtil.isBlank((CharSequence)otlpTraceId)) {
            otlpTraceId = request.getHeader("X-B3-TraceId");
        }
        return otlpTraceId;
    }

    public static String getOtlpSpanIdByRequest(HttpServletRequest request) {
        String otlpSpanId = request.getParameter("X-B3-SpanId");
        if (StrUtil.isBlank((CharSequence)otlpSpanId)) {
            otlpSpanId = request.getHeader("X-B3-SpanId");
        }
        return otlpSpanId;
    }

    public static void setOtlpTraceId(HttpServletRequest request, String defaultTraceId) {
        String otlpTraceId = TraceUtils.getOtlpTraceIdByRequest(request);
        if (otlpTraceId == null) {
            otlpTraceId = defaultTraceId;
        }
        MdcUtils.put("X-B3-TraceId", otlpTraceId);
    }

    public static void setOtlpTraceId(String otlpTraceId) {
        MdcUtils.put("X-B3-TraceId", otlpTraceId);
    }

    public static void removeOtlpTraceId() {
        MdcUtils.remove("X-B3-TraceId");
    }

    public static String getOtlpTraceId() {
        return MdcUtils.get("X-B3-TraceId");
    }

    public static void setOtlpSpanId(HttpServletRequest request, String defaultSpanId) {
        String otlpSpanId = TraceUtils.getOtlpSpanIdByRequest(request);
        if (otlpSpanId == null) {
            otlpSpanId = defaultSpanId;
        }
        MdcUtils.put("X-B3-SpanId", otlpSpanId);
    }

    public static void setOtlpSpanId(String otlpSpanId) {
        MdcUtils.put("X-B3-SpanId", otlpSpanId);
    }

    public static void removeOtlpSpanId() {
        MdcUtils.remove("X-B3-SpanId");
    }

    public static String getOtlpSpanId() {
        return MdcUtils.get("X-B3-SpanId");
    }

    public static String getTraceIdByRequest(HttpServletRequest request) {
        String traceId = request.getParameter("traceId");
        if (StrUtil.isBlank((CharSequence)traceId)) {
            traceId = request.getHeader("traceId");
        }
        return traceId;
    }

    public static String getTraceId() {
        return MdcUtils.get("traceId");
    }

    public static void setTraceId(String traceId) {
        MdcUtils.put("traceId", traceId);
    }

    public static void removeTraceId() {
        MdcUtils.remove("traceId");
    }

    public static String getSpanIdByRequest(HttpServletRequest request) {
        String spanId = request.getParameter("spanId");
        if (StrUtil.isBlank((CharSequence)spanId)) {
            spanId = request.getHeader("spanId");
        }
        return spanId;
    }

    public static String getSpanId() {
        return MdcUtils.get("spanId");
    }

    public static void setSpanId(String spanId) {
        MdcUtils.put("spanId", spanId);
    }

    public static void removeSpanId() {
        MdcUtils.remove("spanId");
    }
}

