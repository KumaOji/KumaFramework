/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  com.kuma.boot.common.utils.servlet.RequestUtils
 *  jakarta.servlet.http.HttpServletRequest
 *  org.jspecify.annotations.NonNull
 *  org.springframework.core.MethodParameter
 *  org.springframework.util.Assert
 *  org.springframework.web.bind.support.WebDataBinderFactory
 *  org.springframework.web.context.request.NativeWebRequest
 *  org.springframework.web.method.support.HandlerMethodArgumentResolver
 *  org.springframework.web.method.support.ModelAndViewContainer
 */
package com.kuma.boot.ip2region.ip2region.support;

import com.kuma.boot.common.utils.servlet.RequestUtils;
import com.kuma.boot.ip2region.ip2region.annotation.RequestIp;
import jakarta.servlet.http.HttpServletRequest;
import org.jspecify.annotations.NonNull;
import org.springframework.core.MethodParameter;
import org.springframework.util.Assert;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

public class RequestIPMethodArgumentResolver
implements HandlerMethodArgumentResolver {
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(RequestIp.class);
    }

    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, @NonNull NativeWebRequest webRequest, WebDataBinderFactory binderFactory) {
        if (parameter.getParameterType().isAssignableFrom(String.class)) {
            return RequestIpContextHolder.computeIfAbsent(() -> this.parseIp(webRequest));
        }
        throw new RuntimeException("param not support " + String.valueOf(parameter.getParameterType()));
    }

    private String parseIp(NativeWebRequest webRequest) {
        HttpServletRequest request = (HttpServletRequest)webRequest.getNativeRequest(HttpServletRequest.class);
        Assert.notNull((Object)request, (String)"request not be null");
        return RequestUtils.getHttpServletRequestIpAddress((HttpServletRequest)request);
    }
}

