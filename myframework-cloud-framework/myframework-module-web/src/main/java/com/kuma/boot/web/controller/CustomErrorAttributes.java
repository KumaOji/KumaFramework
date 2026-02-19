/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  com.kuma.boot.common.utils.lang.StringUtils
 *  com.kuma.boot.common.utils.log.LogUtils
 *  org.jspecify.annotations.Nullable
 *  org.springframework.boot.web.error.ErrorAttributeOptions
 *  org.springframework.boot.webmvc.error.DefaultErrorAttributes
 *  org.springframework.web.context.request.WebRequest
 */
package com.kuma.boot.web.controller;

import com.kuma.boot.common.utils.lang.StringUtils;
import com.kuma.boot.common.utils.log.LogUtils;
import java.util.Map;
import org.jspecify.annotations.Nullable;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.webmvc.error.DefaultErrorAttributes;
import org.springframework.web.context.request.WebRequest;

public class CustomErrorAttributes
extends DefaultErrorAttributes {
    public Map<String, Object> getErrorAttributes(WebRequest webRequest, ErrorAttributeOptions options) {
        String requestUrl = (String)this.getAttr(webRequest, "jakarta.servlet.error.request_uri");
        if (StringUtils.isBlank((String)requestUrl)) {
            requestUrl = (String)this.getAttr(webRequest, "jakarta.servlet.forward.request_uri");
        }
        Integer status = (Integer)this.getAttr(webRequest, "jakarta.servlet.error.status_code");
        Throwable error = this.getError(webRequest);
        LogUtils.error((String)"URL:{} error status:{}", (Object[])new Object[]{requestUrl, status, error});
        return super.getErrorAttributes(webRequest, options);
    }

    private <T> @Nullable T getAttr(WebRequest webRequest, String name) {
        return (T)webRequest.getAttribute(name, 0);
    }
}

