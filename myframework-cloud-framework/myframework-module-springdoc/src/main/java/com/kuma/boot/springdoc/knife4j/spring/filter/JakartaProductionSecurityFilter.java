/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  com.github.xiaoymin.knife4j.extend.filter.BasicFilter
 *  jakarta.servlet.Filter
 *  jakarta.servlet.FilterChain
 *  jakarta.servlet.FilterConfig
 *  jakarta.servlet.ServletException
 *  jakarta.servlet.ServletRequest
 *  jakarta.servlet.ServletResponse
 *  jakarta.servlet.http.HttpServletRequest
 */
package com.kuma.boot.springdoc.knife4j.spring.filter;

import com.github.xiaoymin.knife4j.extend.filter.BasicFilter;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Enumeration;

public class JakartaProductionSecurityFilter
extends BasicFilter
implements Filter {
    private boolean production = false;

    public void init(FilterConfig filterConfig) throws ServletException {
        Enumeration enumeration = filterConfig.getInitParameterNames();
        if (enumeration.hasMoreElements()) {
            this.setProduction(Boolean.valueOf(filterConfig.getInitParameter("production")));
        }
    }

    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpServletRequest = (HttpServletRequest)request;
        if (this.production) {
            String uri = httpServletRequest.getRequestURI();
            if (!this.match(uri)) {
                chain.doFilter(request, response);
            } else {
                response.setContentType("text/palin;charset=UTF-8");
                PrintWriter pw = response.getWriter();
                pw.write("You do not have permission to access this page");
                pw.flush();
            }
        } else {
            chain.doFilter(request, response);
        }
    }

    public void destroy() {
    }

    public JakartaProductionSecurityFilter(boolean production) {
        this.production = production;
    }

    public JakartaProductionSecurityFilter() {
    }

    public boolean isProduction() {
        return this.production;
    }

    public void setProduction(boolean production) {
        this.production = production;
    }
}

