/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  com.kuma.boot.common.holder.TraceContextHolder
 *  com.kuma.boot.common.utils.id.IdGeneratorUtils
 *  com.kuma.boot.common.utils.lang.StringUtils
 *  com.kuma.boot.common.utils.log.LogUtils
 *  com.kuma.boot.common.utils.servlet.RequestUtils
 *  com.kuma.boot.common.utils.servlet.TraceUtils
 *  jakarta.servlet.http.HttpServletRequest
 *  jakarta.servlet.http.HttpServletResponse
 *  org.springframework.beans.factory.InitializingBean
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.beans.factory.annotation.Qualifier
 *  org.springframework.boot.autoconfigure.AutoConfiguration
 *  org.springframework.context.ApplicationEventPublisher
 *  org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor
 *  org.springframework.web.bind.annotation.RestControllerAdvice
 *  org.springframework.web.context.request.NativeWebRequest
 */
package com.kuma.boot.web.exception.advice;

import com.kuma.boot.common.holder.TraceContextHolder;
import com.kuma.boot.common.utils.id.IdGeneratorUtils;
import com.kuma.boot.common.utils.lang.StringUtils;
import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.boot.common.utils.servlet.RequestUtils;
import com.kuma.boot.common.utils.servlet.TraceUtils;
import com.kuma.boot.web.annotation.BusinessApi;
import com.kuma.boot.web.exception.event.ErrorEvent;
import com.kuma.boot.web.exception.handler.ExceptionHandler;
import com.kuma.boot.web.utils.ErrorUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.List;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.NativeWebRequest;

@AutoConfiguration
@RestControllerAdvice(annotations={BusinessApi.class})
public class DataBusinessHandlerExceptionAdvice
implements InitializingBean {
    private final List<ExceptionHandler> exceptionHandler;
    @Autowired
    @Qualifier(value="asyncThreadPoolTaskExecutor")
    private ThreadPoolTaskExecutor asyncThreadPoolTaskExecutor;
    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;

    public DataBusinessHandlerExceptionAdvice(List<ExceptionHandler> exceptionHandler) {
        this.exceptionHandler = exceptionHandler;
    }

    public void afterPropertiesSet() throws Exception {
        LogUtils.started(DataBusinessHandlerExceptionAdvice.class, (String)"kuma-boot-starter-web", (String[])new String[0]);
    }

    public void handleExceptions(NativeWebRequest req, Exception e) {
        String traceId = this.getTraceId();
        try {
            HttpServletRequest nativeRequest = (HttpServletRequest)req.getNativeRequest(HttpServletRequest.class);
            HttpServletResponse nativeResponse = (HttpServletResponse)req.getNativeResponse(HttpServletResponse.class);
            LogUtils.error((Throwable)e);
            if (nativeRequest != null) {
                LogUtils.error((String)"\u8bf7\u6c42\u5931\u8d25\u8fd4\u56de request:{} traceId:{} url:{} \u5f02\u5e38\u6d88\u606f:{}", (Object[])new Object[]{req.getDescription(true), traceId, nativeRequest.getRequestURL(), e.getMessage()});
            }
        }
        catch (Exception exception) {
            // empty catch block
        }
        this.publishEvent(e);
        this.asyncThreadPoolTaskExecutor.submit(() -> this.exceptionHandler.forEach(handler -> handler.handle(req, e, traceId)));
    }

    private void publishEvent(Throwable error) {
        ErrorEvent event = new ErrorEvent();
        event.setRequestId(this.getTraceId());
        event.setRequestType("api");
        HttpServletRequest request = RequestUtils.getRequest();
        event.setRequestMethod(request.getMethod());
        Object requestUrl = request.getRequestURI();
        String queryString = request.getQueryString();
        if (StringUtils.isNotBlank((String)queryString)) {
            requestUrl = (String)requestUrl + "?" + queryString;
        }
        event.setRequestIp(RequestUtils.getHttpServletRequestIpAddress((HttpServletRequest)request));
        event.setRequestUrl((String)requestUrl);
        ErrorUtils.initErrorInfo(error, event);
        this.applicationEventPublisher.publishEvent((Object)event);
    }

    private String getTraceId() {
        String traceId = TraceContextHolder.getTraceId();
        if (traceId == null) {
            traceId = TraceUtils.getKmcTraceId();
        }
        if (traceId == null) {
            traceId = IdGeneratorUtils.getIdStr();
        }
        return traceId;
    }
}

