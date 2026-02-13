/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  jakarta.servlet.http.HttpServletRequest
 */
package com.kuma.boot.common.utils.network;

import com.kuma.boot.common.utils.lang.StringUtils;
import jakarta.servlet.http.HttpServletRequest;
import java.util.LinkedList;
import java.util.List;

public final class ClientIpUtils {
    private static final List<String> POSSIBLE_PROXY_HEADER_KEYS = new LinkedList<String>();

    private ClientIpUtils() {
    }

    public static String getClientIp(HttpServletRequest request) {
        int index;
        String realIp = request.getRemoteAddr();
        for (String key : POSSIBLE_PROXY_HEADER_KEYS) {
            String header = request.getHeader(key);
            if (!StringUtils.isNotEmpty((CharSequence)header) || "unknown".equalsIgnoreCase(header)) continue;
            realIp = header;
            break;
        }
        if ((index = realIp.indexOf(44)) >= 0) {
            realIp = realIp.substring(0, index);
        }
        return realIp;
    }

    static {
        POSSIBLE_PROXY_HEADER_KEYS.add("X-Forwarded-For");
        POSSIBLE_PROXY_HEADER_KEYS.add("Proxy-Client-IP");
        POSSIBLE_PROXY_HEADER_KEYS.add("WL-Proxy-Client-IP");
        POSSIBLE_PROXY_HEADER_KEYS.add("HTTP_X_FORWARDED_FOR");
        POSSIBLE_PROXY_HEADER_KEYS.add("HTTP_X_FORWARDED");
        POSSIBLE_PROXY_HEADER_KEYS.add("HTTP_X_CLUSTER_CLIENT_IP");
        POSSIBLE_PROXY_HEADER_KEYS.add("HTTP_CLIENT_IP");
        POSSIBLE_PROXY_HEADER_KEYS.add("HTTP_FORWARDED_FOR");
        POSSIBLE_PROXY_HEADER_KEYS.add("HTTP_FORWARDED");
        POSSIBLE_PROXY_HEADER_KEYS.add("HTTP_VIA");
        POSSIBLE_PROXY_HEADER_KEYS.add("REMOTE_ADDR");
    }
}

