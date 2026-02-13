/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cn.hutool.core.convert.Convert
 *  jakarta.servlet.http.HttpServletRequest
 *  org.springframework.core.task.TaskDecorator
 *  org.springframework.web.context.request.ServletRequestAttributes
 */
package com.kuma.boot.common.support.thread;

import cn.hutool.core.convert.Convert;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.core.task.TaskDecorator;
import org.springframework.web.context.request.ServletRequestAttributes;

public abstract class AbstractContextDecorator
implements TaskDecorator {
    public static final String SERVLET_ASYNC_CONTEXT_TIMEOUT_MILLIS = "servletAsyncContextTimeoutMillis";
    private boolean enableServletAsyncContext = false;
    private Long servletAsyncContextTimeoutMillis = 600L;

    public AbstractContextDecorator(boolean enableServletAsyncContext, Long servletAsyncContextTimeoutMillis) {
        this.enableServletAsyncContext = enableServletAsyncContext;
        this.servletAsyncContextTimeoutMillis = servletAsyncContextTimeoutMillis;
    }

    protected void enableServletAsyncContext(ServletRequestAttributes context) {
        if (!this.enableServletAsyncContext) {
            return;
        }
        HttpServletRequest request = context.getRequest();
        request.startAsync();
        Object servletAsyncContextTimeoutMillis = request.getAttribute(SERVLET_ASYNC_CONTEXT_TIMEOUT_MILLIS);
        if (servletAsyncContextTimeoutMillis == null) {
            servletAsyncContextTimeoutMillis = this.servletAsyncContextTimeoutMillis;
        }
        request.getAsyncContext().setTimeout(Convert.toLong((Object)servletAsyncContextTimeoutMillis).longValue());
    }

    protected void completeServletAsyncContext(ServletRequestAttributes context) {
        if (this.enableServletAsyncContext) {
            context.getRequest().getAsyncContext().complete();
        }
    }
}

