/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  jakarta.servlet.http.HttpServletRequest
 *  org.springframework.beans.factory.annotation.Value
 */
package com.kuma.boot.common.utils.ip;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;

public class IpHelper {
    @Value(value="${lili.lbs.key}")
    private String key;
    @Value(value="${lili.lbs.sk}")
    private String sk;
    private static final String API = "https://apis.map.qq.com";

    public String getIpCity(HttpServletRequest request) {
        String result = "\u672a\u77e5";
        return result;
    }
}

