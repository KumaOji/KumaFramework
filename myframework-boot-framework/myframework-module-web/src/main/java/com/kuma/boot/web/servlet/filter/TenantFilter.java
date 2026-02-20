/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  cn.hutool.core.util.StrUtil
 *  com.kuma.boot.common.holder.TenantContextHolder
 *  com.kuma.boot.common.utils.lang.StringUtils
 *  com.kuma.boot.common.utils.servlet.RequestUtils
 *  com.kuma.boot.common.utils.servlet.ResponseUtils
 *  com.kuma.boot.common.utils.servlet.TraceUtils
 *  jakarta.servlet.FilterChain
 *  jakarta.servlet.ServletException
 *  jakarta.servlet.ServletRequest
 *  jakarta.servlet.ServletResponse
 *  jakarta.servlet.http.HttpServletRequest
 *  jakarta.servlet.http.HttpServletResponse
 *  org.springframework.web.filter.OncePerRequestFilter
 */
package com.kuma.boot.web.servlet.filter;

import cn.hutool.core.util.StrUtil;
import com.kuma.boot.common.holder.TenantContextHolder;
import com.kuma.boot.common.utils.lang.StringUtils;
import com.kuma.boot.common.utils.servlet.RequestUtils;
import com.kuma.boot.common.utils.servlet.ResponseUtils;
import com.kuma.boot.common.utils.servlet.TraceUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.web.filter.OncePerRequestFilter;

public class TenantFilter
extends OncePerRequestFilter {
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        return RequestUtils.excludeActuator((HttpServletRequest)request);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws IOException, ServletException {
        try {
            String tenantId = request.getParameter("kmc-tenant-id");
            if (StrUtil.isEmpty((CharSequence)tenantId)) {
                tenantId = request.getHeader("kmc-tenant-id");
            }
            if (StringUtils.isNotBlank((String)tenantId)) {
                TenantContextHolder.setTenant((String)tenantId);
                TraceUtils.setKmcTenantId((String)tenantId);
            } else if (StringUtils.isBlank((String)TenantContextHolder.getTenant())) {
                TenantContextHolder.setTenant((String)"1");
                TraceUtils.setKmcTenantId((String)"1");
            }
            ResponseUtils.addResponseHeader((HttpServletResponse)response, (String)"kmc-tenant-id", (String)TenantContextHolder.getTenant());
            filterChain.doFilter((ServletRequest)request, (ServletResponse)response);
        }
        finally {
            TenantContextHolder.clear();
            TraceUtils.removeKmcTenantId();
        }
    }
}

