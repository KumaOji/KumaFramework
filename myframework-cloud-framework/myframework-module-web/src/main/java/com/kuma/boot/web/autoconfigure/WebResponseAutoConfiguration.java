/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  com.kuma.boot.common.model.result.Result
 *  com.kuma.boot.common.utils.log.LogUtils
 *  com.kuma.boot.common.utils.servlet.ResponseUtils
 *  jakarta.servlet.Servlet
 *  org.springframework.beans.factory.InitializingBean
 *  org.springframework.boot.autoconfigure.AutoConfiguration
 *  org.springframework.boot.autoconfigure.condition.ConditionalOnClass
 *  org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication
 *  org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication$Type
 *  org.springframework.core.MethodParameter
 *  org.springframework.http.MediaType
 *  org.springframework.http.server.ServerHttpRequest
 *  org.springframework.http.server.ServerHttpResponse
 *  org.springframework.web.bind.annotation.RestControllerAdvice
 *  org.springframework.web.servlet.DispatcherServlet
 *  org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice
 */
package com.kuma.boot.web.autoconfigure;

import com.kuma.boot.common.model.result.Result;
import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.boot.common.utils.servlet.ResponseUtils;
import com.kuma.boot.web.annotation.BusinessApi;
import com.kuma.boot.web.annotation.IgnoreResponseBodyAdvice;
import jakarta.servlet.Servlet;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

@AutoConfiguration
@ConditionalOnClass(value={Servlet.class, DispatcherServlet.class})
@ConditionalOnWebApplication(type=ConditionalOnWebApplication.Type.SERVLET)
@RestControllerAdvice(annotations={BusinessApi.class})
public class WebResponseAutoConfiguration
implements ResponseBodyAdvice<Object>,
InitializingBean {
    public void afterPropertiesSet() throws Exception {
        LogUtils.started(WebResponseAutoConfiguration.class, (String)"kuma-boot-starter-web", (String[])new String[0]);
    }

    public boolean supports(MethodParameter methodParameter, Class aClass) {
        if (methodParameter.getDeclaringClass().isAnnotationPresent(IgnoreResponseBodyAdvice.class)) {
            return false;
        }
        return !methodParameter.getMethod().isAnnotationPresent(IgnoreResponseBodyAdvice.class);
    }

    public Object beforeBodyWrite(Object o, MethodParameter methodParameter, MediaType mediaType, Class aClass, ServerHttpRequest serverHttpRequest, ServerHttpResponse serverHttpResponse) {
        ResponseUtils.addResponseHeader((ServerHttpResponse)serverHttpResponse);
        if (o == null) {
            return null;
        }
        if (o instanceof Result) {
            return o;
        }
        return Result.success((Object)o);
    }
}

