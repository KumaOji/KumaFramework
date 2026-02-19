/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  com.kuma.boot.common.utils.log.LogUtils
 *  jakarta.servlet.http.HttpServletRequest
 *  org.springframework.core.MethodParameter
 *  org.springframework.web.context.request.NativeWebRequest
 *  org.springframework.web.method.HandlerMethod
 *  org.springframework.web.servlet.HandlerExecutionChain
 *  org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping
 */
package com.kuma.boot.web.exception.handler;

import com.kuma.boot.common.utils.log.LogUtils;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.core.MethodParameter;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerExecutionChain;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

public class LoggerExceptionHandler
implements ExceptionHandler {
    private RequestMappingHandlerMapping mapping;

    public LoggerExceptionHandler(RequestMappingHandlerMapping mapping) {
        this.mapping = mapping;
    }

    @Override
    public void handle(NativeWebRequest req, Throwable throwable, String traceId) {
        this.printLog(req, throwable);
    }

    private void printLog(NativeWebRequest req, Throwable e) {
        try {
            HandlerExecutionChain chain = this.mapping.getHandler((HttpServletRequest)req.getNativeRequest());
            Object handler = chain.getHandler();
            if (handler instanceof HandlerMethod) {
                HandlerMethod handlerMethod = (HandlerMethod)handler;
                MethodParameter[] methodParameters = handlerMethod.getMethodParameters();
                Object object = handlerMethod.getBean();
            }
        }
        catch (Exception ex) {
            LogUtils.error((Throwable)e);
        }
        LogUtils.error((Throwable)e, (String)"\u3010\u5168\u5c40\u5f02\u5e38\u62e6\u622a\u3011{}: \u8bf7\u6c42\u8def\u5f84: {}, \u8bf7\u6c42\u53c2\u6570: {}, \u5f02\u5e38\u4fe1\u606f {} ", (Object[])new Object[]{e.getClass().getName(), this.uri(req), this.query(req), e.getMessage()});
    }
}

