/*
 * Copyright (c) 2020-2030, kuma (2569277704@qq.com & https://blog.kumacloud.top/).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.kuma.boot.common.utils.servlet;

import cn.hutool.core.util.StrUtil;
import com.kuma.boot.common.constant.CommonConstants;
import jakarta.servlet.http.HttpServletRequest;

/**
 * 链路追踪工具类
 *
 * @author kuma
 * @version 2023.01
 * @since 2023-01-03 11:32:50
 */
public class TraceUtils {

    /**
     * 跟踪跑龙套
     * @return
     * @since 2023-01-03 11:32:50
     */
    private TraceUtils() {}

    /**
     * 从header和参数中获取traceId 从前端传入数据
     * @param request request
     * @return {@link String }
     * @since 2023-01-03 11:32:50
     */
    public static String getTtcTraceIdByRequest(HttpServletRequest request) {
        String traceId = request.getParameter(CommonConstants.KMC_TRACE_ID);
        if (StrUtil.isBlank(traceId)) {
            traceId = request.getHeader(CommonConstants.KMC_TRACE_ID);
        }
        return traceId;
    }

    /**
     * 获取traceId
     * @return {@link String }
     * @since 2023-01-03 11:32:50
     */
    public static String getTtcTraceId() {
        return MdcUtils.get(CommonConstants.KMC_TRACE_ID);
    }

    /**
     * 传递traceId至MDC
     * @param traceId 跟踪ID
     * @since 2023-01-03 11:32:50
     */
    public static void setTtcTraceId(String traceId) {
        MdcUtils.put(CommonConstants.KMC_TRACE_ID, traceId);
    }

    /**
     * 删除mdc跟踪id
     *
     * @since 2023-01-03 11:32:50
     */
    public static void removeTtcTraceId() {
        MdcUtils.remove(CommonConstants.KMC_TRACE_ID);
    }

    /**
     * 传递tenantId至MDC
     * @param tenantId 租户id
     * @since 2023-01-03 11:32:50
     */
    public static void setTtcTenantId(String tenantId) {
        MdcUtils.put(CommonConstants.KMC_TENANT_ID, tenantId);
    }

    /**
     * mdc删除租户id
     *
     * @since 2023-01-03 11:32:50
     */
    public static void removeTtcTenantId() {
        MdcUtils.remove(CommonConstants.KMC_TENANT_ID);
    }

    public static String getTtcTenantId() {
        return MdcUtils.get(CommonConstants.KMC_TENANT_ID);
    }

    /**
     * 传递version至MDC
     * @param version 租户id
     * @since 2023-01-03 11:32:50
     */
    public static void setTtcVersion(String version) {
        MdcUtils.put(CommonConstants.KMC_REQUEST_VERSION, version);
    }

    /**
     * mdc删除版本
     *
     * @since 2023-01-03 11:32:50
     */
    public static void removeTtcVersion() {
        MdcUtils.remove(CommonConstants.KMC_REQUEST_VERSION);
    }

    public static String getTtcVersion() {
        return MdcUtils.get(CommonConstants.KMC_REQUEST_VERSION);
    }

    /**
     * 从header和参数中获取OtlpTraceId从前端传入数据
     * @param request HttpServletRequest
     * @return {@link String }
     * @since 2023-01-03 11:32:50
     */
    public static String getOtlpTraceIdByRequest(HttpServletRequest request) {
        String otlpTraceId = request.getParameter(CommonConstants.OTLP_TRACE_ID);
        if (StrUtil.isBlank(otlpTraceId)) {
            otlpTraceId = request.getHeader(CommonConstants.OTLP_TRACE_ID);
        }
        return otlpTraceId;
    }

    /**
     * 从header和参数中获取otlpSpanId 从前端传入数据
     * @param request HttpServletRequest
     * @return {@link String }
     * @since 2023-01-03 11:32:50
     */
    public static String getOtlpSpanIdByRequest(HttpServletRequest request) {
        String otlpSpanId = request.getParameter(CommonConstants.OTLP_SPANE_ID);
        if (StrUtil.isBlank(otlpSpanId)) {
            otlpSpanId = request.getHeader(CommonConstants.OTLP_SPANE_ID);
        }
        return otlpSpanId;
    }

    /**
     * 传递otlpTraceId至MDC
     * @param request request
     * @since 2023-01-03 11:32:50
     */
    public static void setOtlpTraceId(HttpServletRequest request, String defaultTraceId) {
        String otlpTraceId = getOtlpTraceIdByRequest(request);
        if(otlpTraceId == null){
            otlpTraceId = defaultTraceId;
        }
        MdcUtils.put(CommonConstants.OTLP_TRACE_ID, otlpTraceId);
    }

    /**
     * 传递otlpTraceId至MDC
     *
     * @since 2023-01-03 11:32:50
     */
    public static void setOtlpTraceId(String otlpTraceId) {
        MdcUtils.put(CommonConstants.OTLP_TRACE_ID, otlpTraceId);
    }

    /**
     * mdc删除otlp跟踪id
     *
     * @since 2023-01-03 11:32:50
     */
    public static void removeOtlpTraceId() {
        MdcUtils.remove(CommonConstants.OTLP_TRACE_ID);
    }

    public static String getOtlpTraceId() {
        return MdcUtils.get(CommonConstants.OTLP_TRACE_ID);
    }

    /**
     * 传递otlpSpanId至MDC
     * @param request request
     * @since 2023-01-03 11:32:50
     */
    public static void setOtlpSpanId(HttpServletRequest request,String defaultSpanId) {
        String otlpSpanId = getOtlpSpanIdByRequest(request);
        if(otlpSpanId == null){
            otlpSpanId = defaultSpanId;
        }
        MdcUtils.put(CommonConstants.OTLP_SPANE_ID, otlpSpanId);
    }

    public static void setOtlpSpanId(String otlpSpanId) {
        MdcUtils.put(CommonConstants.OTLP_SPANE_ID, otlpSpanId);
    }

    /**
     * mdc删除otlp跨度id
     *
     * @since 2023-01-03 11:32:50
     */
    public static void removeOtlpSpanId() {
        MdcUtils.remove(CommonConstants.OTLP_SPANE_ID);
    }

    public static String getOtlpSpanId() {
        return MdcUtils.get(CommonConstants.OTLP_SPANE_ID);
    }

    /**
     * 从header和参数中获取traceId 从前端传入数据
     * @param request request
     * @return {@link String }
     * @since 2023-01-03 11:32:50
     */
    public static String getTraceIdByRequest(HttpServletRequest request) {
        String traceId = request.getParameter(CommonConstants.TRACE_ID);
        if (StrUtil.isBlank(traceId)) {
            traceId = request.getHeader(CommonConstants.TRACE_ID);
        }
        return traceId;
    }

    /**
     * 获取traceId
     * @return {@link String }
     * @since 2023-01-03 11:32:50
     */
    public static String getTraceId() {
        return MdcUtils.get(CommonConstants.TRACE_ID);
    }

    /**
     * 传递traceId至MDC
     * @param traceId 跟踪ID
     * @since 2023-01-03 11:32:50
     */
    public static void setTraceId(String traceId) {
        MdcUtils.put(CommonConstants.TRACE_ID, traceId);
    }

    /**
     * 删除mdc跟踪id
     *
     * @since 2023-01-03 11:32:50
     */
    public static void removeTraceId() {
        MdcUtils.remove(CommonConstants.TRACE_ID);
    }

    /**
     * 从header和参数中获取traceId 从前端传入数据
     * @param request request
     * @return {@link String }
     * @since 2023-01-03 11:32:50
     */
    public static String getSpanIdByRequest(HttpServletRequest request) {
        String spanId = request.getParameter(CommonConstants.SPANE_ID);
        if (StrUtil.isBlank(spanId)) {
            spanId = request.getHeader(CommonConstants.SPANE_ID);
        }
        return spanId;
    }

    /**
     * 获取traceId
     * @return {@link String }
     * @since 2023-01-03 11:32:50
     */
    public static String getSpanId() {
        return MdcUtils.get(CommonConstants.SPANE_ID);
    }

    /**
     * 传递traceId至MDC
     * @param spanId 跟踪ID
     * @since 2023-01-03 11:32:50
     */
    public static void setSpanId(String spanId) {
        MdcUtils.put(CommonConstants.SPANE_ID, spanId);
    }

    /**
     * 删除mdc跟踪id
     *
     * @since 2023-01-03 11:32:50
     */
    public static void removeSpanId() {
        MdcUtils.remove(CommonConstants.SPANE_ID);
    }
}
