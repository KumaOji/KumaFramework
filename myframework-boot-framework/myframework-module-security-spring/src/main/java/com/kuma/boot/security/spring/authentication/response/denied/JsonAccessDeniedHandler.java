/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.kuma.boot.common.enums.ResultEnum
 *  com.kuma.boot.common.utils.log.LogUtils
 *  com.kuma.boot.common.utils.servlet.ResponseUtils
 *  jakarta.servlet.ServletException
 *  jakarta.servlet.http.HttpServletRequest
 *  jakarta.servlet.http.HttpServletResponse
 *  org.springframework.security.access.AccessDeniedException
 *  org.springframework.security.web.access.AccessDeniedHandler
 */
package com.kuma.boot.security.spring.authentication.response.denied;

import com.kuma.boot.common.enums.ResultEnum;
import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.boot.common.utils.servlet.ResponseUtils;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;

public class JsonAccessDeniedHandler
implements AccessDeniedHandler {
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
        LogUtils.error((String)"\u7528\u6237\u6743\u9650\u4e0d\u8db3", (Object[])new Object[]{accessDeniedException});
        ResponseUtils.fail((HttpServletResponse)response, (ResultEnum)ResultEnum.FORBIDDEN);
    }
}

