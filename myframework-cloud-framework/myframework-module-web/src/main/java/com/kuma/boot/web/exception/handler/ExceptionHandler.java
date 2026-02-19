/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  cn.hutool.core.util.StrUtil
 *  jakarta.servlet.http.HttpServletRequest
 *  org.springframework.web.context.request.NativeWebRequest
 */
package com.kuma.boot.web.exception.handler;

import cn.hutool.core.util.StrUtil;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Objects;
import org.springframework.web.context.request.NativeWebRequest;

public interface ExceptionHandler {
    public void handle(NativeWebRequest var1, Throwable var2, String var3);

    default public String uri(NativeWebRequest request) {
        HttpServletRequest nativeRequest = (HttpServletRequest)request.getNativeRequest(HttpServletRequest.class);
        if (Objects.nonNull(nativeRequest)) {
            return nativeRequest.getRequestURI();
        }
        return "--";
    }

    default public String query(NativeWebRequest request) {
        String queryString;
        HttpServletRequest nativeRequest = (HttpServletRequest)request.getNativeRequest(HttpServletRequest.class);
        if (Objects.nonNull(nativeRequest) && StrUtil.isNotBlank((CharSequence)(queryString = nativeRequest.getQueryString()))) {
            return queryString;
        }
        return "--";
    }
}

