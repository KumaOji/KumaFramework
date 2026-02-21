/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  com.kuma.boot.common.utils.servlet.RequestUtils
 *  jakarta.servlet.http.HttpServletRequest
 *  org.springframework.core.NamedInheritableThreadLocal
 *  org.springframework.core.NamedThreadLocal
 *  org.springframework.util.StringUtils
 */
package com.kuma.boot.ip2region.ip2region.support;

import com.kuma.boot.common.utils.servlet.RequestUtils;
import jakarta.servlet.http.HttpServletRequest;
import java.util.function.Supplier;
import org.springframework.core.NamedInheritableThreadLocal;
import org.springframework.core.NamedThreadLocal;
import org.springframework.util.StringUtils;

public class RequestIpContextHolder {
    private static final ThreadLocal<String> context = new NamedThreadLocal("request ip context");
    private static final ThreadLocal<String> inheritableContext = new NamedInheritableThreadLocal("inheritable request ip context");

    public static void set(String ip) {
        RequestIpContextHolder.set(ip, false);
    }

    public static void set(String ip, boolean inheritable) {
        if (StringUtils.hasText((String)ip)) {
            if (inheritable) {
                inheritableContext.set(ip);
                context.remove();
            } else {
                context.set(ip);
                inheritableContext.remove();
            }
        } else {
            RequestIpContextHolder.remove();
        }
    }

    public static void set(HttpServletRequest request) {
        String ip = RequestUtils.getHttpServletRequestIpAddress((HttpServletRequest)request);
        RequestIpContextHolder.set(ip);
    }

    public static synchronized String computeIfAbsent(Supplier<String> supplier) {
        String ip = RequestIpContextHolder.get();
        if (StringUtils.hasText((String)ip)) {
            return ip;
        }
        String supplierIp = supplier.get();
        RequestIpContextHolder.set(supplierIp);
        return supplierIp;
    }

    public static String get() {
        String ip = context.get();
        if (!StringUtils.hasText((String)ip)) {
            ip = inheritableContext.get();
        }
        return ip;
    }

    public static void remove() {
        context.remove();
        inheritableContext.remove();
    }
}

