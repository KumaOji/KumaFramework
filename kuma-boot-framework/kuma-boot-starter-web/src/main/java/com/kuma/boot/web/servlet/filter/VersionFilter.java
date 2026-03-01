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

import cn.hutool.core.util.StrUtil;
import com.kuma.boot.common.constant.CommonConstants;
import com.kuma.boot.common.holder.VersionContextHolder;
import com.kuma.boot.common.utils.servlet.RequestUtils;
import com.kuma.boot.common.utils.servlet.ResponseUtils;
import com.kuma.boot.common.utils.servlet.TraceUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * 负载均衡隔离规则过滤器
 *
 * @author kuma
 * @version 2021.9
 * @since 2021-09-02 22:16:36
 */
// @WebFilter(filterName = "VersionFilter", urlPatterns = "/*", asyncSupported = true)
public class VersionFilter extends OncePerRequestFilter {

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        return RequestUtils.excludeActuator(request);
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws IOException, ServletException {
        try {
            // ServletRequestAttributes attributes = (ServletRequestAttributes)
            // Objects.requireNonNull(RequestContextHolder.getRequestAttributes());
            // RequestContextHolder.setRequestAttributes(attributes, true);

            String version = request.getHeader(CommonConstants.KMC_REQUEST_VERSION);
            if (StrUtil.isNotEmpty(version)) {
                VersionContextHolder.setVersion(version);
                TraceUtils.setKmcVersion(version);
            }

            ResponseUtils.addResponseHeader(
                    response,
                    CommonConstants.KMC_REQUEST_VERSION,
                    VersionContextHolder.getVersion());

            filterChain.doFilter(request, response);
        } finally {
            VersionContextHolder.clear();
            TraceUtils.removeKmcVersion();
        }
    }
}
