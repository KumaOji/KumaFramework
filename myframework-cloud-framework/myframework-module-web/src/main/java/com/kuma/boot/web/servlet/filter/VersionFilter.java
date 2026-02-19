/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  cn.hutool.core.util.StrUtil
 *  com.kuma.boot.common.holder.VersionContextHolder
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
import com.kuma.boot.common.holder.VersionContextHolder;
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

public class VersionFilter
extends OncePerRequestFilter {
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        return RequestUtils.excludeActuator((HttpServletRequest)request);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws IOException, ServletException {
        try {
            String version = request.getHeader("kmc-request-version");
            if (StrUtil.isNotEmpty((CharSequence)version)) {
                VersionContextHolder.setVersion((String)version);
                TraceUtils.setKmcVersion((String)version);
            }
            ResponseUtils.addResponseHeader((HttpServletResponse)response, (String)"kmc-request-version", (String)VersionContextHolder.getVersion());
            filterChain.doFilter((ServletRequest)request, (ServletResponse)response);
        }
        finally {
            VersionContextHolder.clear();
            TraceUtils.removeKmcVersion();
        }
    }
}

