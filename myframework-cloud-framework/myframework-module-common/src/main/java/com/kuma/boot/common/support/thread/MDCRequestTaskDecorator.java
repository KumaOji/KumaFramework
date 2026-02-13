/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.web.context.request.RequestAttributes
 *  org.springframework.web.context.request.RequestContextHolder
 *  org.springframework.web.context.request.ServletRequestAttributes
 */
package com.kuma.boot.common.support.thread;

import com.kuma.boot.common.support.thread.AbstractContextDecorator;
import com.kuma.boot.common.utils.servlet.MdcUtils;
import java.util.Map;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

public class MDCRequestTaskDecorator
extends AbstractContextDecorator {
    public MDCRequestTaskDecorator(boolean enableServletAsyncContext, Long servletAsyncContextTimeoutMillis) {
        super(enableServletAsyncContext, servletAsyncContextTimeoutMillis);
    }

    public Runnable decorate(Runnable runnable) {
        ServletRequestAttributes context = (ServletRequestAttributes)RequestContextHolder.getRequestAttributes();
        Map<String, String> previous = MdcUtils.getCopyOfContextMap();
        this.enableServletAsyncContext(context);
        return () -> {
            try {
                RequestContextHolder.setRequestAttributes((RequestAttributes)context);
                if (previous != null) {
                    MdcUtils.setContextMap(previous);
                }
                runnable.run();
            }
            finally {
                RequestContextHolder.resetRequestAttributes();
                MdcUtils.clear();
                this.completeServletAsyncContext(context);
            }
        };
    }
}

