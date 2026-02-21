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
import com.kuma.boot.common.holder.TenantContextHolder;
import com.kuma.boot.common.utils.lang.StringUtils;
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
 * 租户过滤器
 *
 * @author kuma
 * @version 2021.9
 * @since 2021-09-02 22:15:01
 */
// @WebFilter(filterName = "TenantFilter", urlPatterns = "/*", asyncSupported = true)
public class TenantFilter extends OncePerRequestFilter {

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        return RequestUtils.excludeActuator(request);
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws IOException, ServletException {
        try {
            // 优先获取请求参数中的tenantId值
            String tenantId = request.getParameter(CommonConstants.KMC_TENANT_ID);
            if (StrUtil.isEmpty(tenantId)) {
                tenantId = request.getHeader(CommonConstants.KMC_TENANT_ID);
            }

            // 保存租户id
            if (StringUtils.isNotBlank(tenantId)) {
                TenantContextHolder.setTenant(tenantId);
                TraceUtils.setKmcTenantId(tenantId);
            } else {
                if (StringUtils.isBlank(TenantContextHolder.getTenant())) {
                    TenantContextHolder.setTenant(CommonConstants.KMC_TENANT_ID_DEFAULT);
                    TraceUtils.setKmcTenantId(CommonConstants.KMC_TENANT_ID_DEFAULT);
                }
            }

            ResponseUtils.addResponseHeader(
                    response, CommonConstants.KMC_TENANT_ID, TenantContextHolder.getTenant());

            filterChain.doFilter(request, response);
        } finally {
            TenantContextHolder.clear();
            TraceUtils.removeKmcTenantId();
        }
    }
}
