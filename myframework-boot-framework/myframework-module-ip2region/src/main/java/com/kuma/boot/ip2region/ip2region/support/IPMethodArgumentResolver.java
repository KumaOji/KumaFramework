/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  com.kuma.boot.common.utils.log.LogUtils
 *  jakarta.servlet.http.HttpServletRequest
 *  org.springframework.core.MethodParameter
 *  org.springframework.util.Assert
 *  org.springframework.web.bind.support.WebDataBinderFactory
 *  org.springframework.web.context.request.NativeWebRequest
 *  org.springframework.web.method.support.HandlerMethodArgumentResolver
 *  org.springframework.web.method.support.ModelAndViewContainer
 *  org.xbill.DNS.Address
 */
package com.kuma.boot.ip2region.ip2region.support;

import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.boot.ip2region.ip2region.annotation.IP;
import jakarta.servlet.http.HttpServletRequest;
import java.net.UnknownHostException;
import org.springframework.core.MethodParameter;
import org.springframework.util.Assert;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.xbill.DNS.Address;

public class IPMethodArgumentResolver
implements HandlerMethodArgumentResolver {
    public boolean supportsParameter(MethodParameter parameter) {
        IP ip = (IP)parameter.getParameterAnnotation(IP.class);
        return ip != null && ip.dns();
    }

    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        HttpServletRequest request = (HttpServletRequest)webRequest.getNativeRequest(HttpServletRequest.class);
        Assert.notNull((Object)request, (String)"request not be null");
        String param = request.getParameter(parameter.getParameterName());
        return this.DNSForIp(param);
    }

    private String DNSForIp(String ip) {
        try {
            return Address.getByName((String)ip).getHostAddress();
        }
        catch (UnknownHostException e) {
            LogUtils.error((String)"\u57df\u540d\u89e3\u6790\u5931\u8d25:{} msg:{}", (Object[])new Object[]{ip, e.getMessage(), e});
            return ip;
        }
    }
}

