/*
 * Copyright (c) 2020-2030, Kuma (2569277704@qq.com & https://blog.kumacloud.top/).
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

package com.kuma.boot.web.servlet.filter;

import com.kuma.boot.common.constant.CommonConstants;
import com.kuma.boot.common.holder.TraceContextHolder;
import com.kuma.boot.common.utils.id.IdGeneratorUtils;
import com.kuma.boot.common.utils.lang.StringUtils;
import com.kuma.boot.core.utils.servlet.RequestUtils;
import com.kuma.boot.core.utils.servlet.ResponseUtils;
import com.kuma.boot.common.utils.servlet.TraceUtils;
import com.kuma.boot.web.autoconfigure.properties.WebMvcFilterProperties;
import io.micrometer.tracing.Span;
import io.micrometer.tracing.Tracer;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.skywalking.apm.toolkit.trace.TraceContext;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * 日志链路追踪过滤器
 *
 * @author kuma
 * @version 2021.9
 * @since 2021-09-02 22:16:19
 */
// @WebFilter(filterName = "TraceFilter", urlPatterns = "/*", asyncSupported = true)
public class TraceFilter extends OncePerRequestFilter {

    private final WebMvcFilterProperties filterProperties;
    private final Tracer tracer;
    public TraceFilter(WebMvcFilterProperties filterProperties, Tracer tracer) {
        this.filterProperties = filterProperties;
        this.tracer = tracer;
    }

    // @Override
    // protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
    //	if (request.getRequestURI().startsWith("/actuator")) {
    //		return true;
    //	}
    //	return !filterProperties.getTrace();
    // }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        return RequestUtils.excludeActuator(request);
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        try {
            String traceId = TraceUtils.getKmcTraceIdByRequest(request);
            if (StringUtils.isBlank(traceId)) {
                traceId = IdGeneratorUtils.getIdStr();
            }
            TraceContextHolder.setTraceId(traceId);

            TraceUtils.setKmcTraceId(traceId);

            String otlpTraceId=null;
            String otlpSpanId=null;
            if(tracer != null){
                Span currentSpan = tracer.currentSpan();
                if (currentSpan != null) {
                    otlpTraceId = currentSpan.context().traceId();
                    otlpSpanId = currentSpan.context().spanId();
                }
            }

            TraceUtils.setOtlpTraceId(request, otlpTraceId);
            TraceUtils.setOtlpSpanId(request, otlpSpanId);

            ResponseUtils.addResponseHeader(
                    response, CommonConstants.KMC_TRACE_ID, TraceContextHolder.getTraceId());
            ResponseUtils.addResponseHeader(
                    response, CommonConstants.OTLP_TRACE_ID, TraceUtils.getOtlpTraceId());
            ResponseUtils.addResponseHeader(
                    response, CommonConstants.OTLP_SPANE_ID, TraceUtils.getOtlpSpanId());

            ResponseUtils.addResponseHeader(response, "tid", TraceContext.traceId());

            filterChain.doFilter(request, response);
        } finally {
            TraceContextHolder.clear();

            TraceUtils.removeKmcTraceId();
            TraceUtils.removeOtlpTraceId();
            TraceUtils.removeOtlpSpanId();
        }
    }

    public WebMvcFilterProperties getFilterProperties() {
        return filterProperties;
    }
}
