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

package com.kuma.cloud.project4.web.trace;

import com.kuma.boot.common.constant.CommonConstants;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.MDC;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * TraceId 过滤器，利用 Slf4J 的 MDC 功能将 traceId 放入 MDC 中，方便日志打印
 *
 * @author kuma
 */
@RequiredArgsConstructor
public class TraceIdFilter extends OncePerRequestFilter {

    private final String traceIdHeaderName;
    private final TraceIdGenerator traceIdGenerator;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String traceId = request.getHeader(this.traceIdHeaderName);
        if (traceId == null || traceId.isEmpty()) {
            traceId = this.traceIdGenerator.generate();
        }

        MDC.put(CommonConstants.TRACE_ID, traceId);
        try {
            response.setHeader(this.traceIdHeaderName, traceId);
            filterChain.doFilter(request, response);
        } finally {
            MDC.remove(CommonConstants.TRACE_ID);
        }
    }
}
