/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  jakarta.servlet.http.Cookie
 *  jakarta.servlet.http.HttpServletRequest
 *  jakarta.servlet.http.HttpServletResponse
 */
package com.kuma.boot.common.utils.servlet;

import com.kuma.boot.common.utils.log.LogUtils;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class CookieUtils {
    public static void addCookie(String key, String value, Integer maxAge, HttpServletResponse response) {
        try {
            Cookie c = new Cookie(key, value);
            c.setMaxAge(maxAge.intValue());
            c.setPath("/");
            response.addCookie(c);
        }
        catch (Exception e) {
            LogUtils.error("\u65b0\u589ecookie\u9519\u8bef", e);
        }
    }

    public static void delCookie(String key, HttpServletResponse response) {
        try {
            Cookie c = new Cookie(key, "");
            c.setMaxAge(0);
            response.addCookie(c);
        }
        catch (Exception e) {
            LogUtils.error("\u5220\u9664cookie\u9519\u8bef", e);
        }
    }

    public static String getCookie(String key, HttpServletRequest request) {
        try {
            if (request.getCookies() == null) {
                return null;
            }
            for (int i = 0; i < request.getCookies().length; ++i) {
                if (!request.getCookies()[i].getName().equals(key)) continue;
                return request.getCookies()[i].getValue();
            }
        }
        catch (Exception e) {
            LogUtils.error("\u83b7\u53d6cookie\u9519\u8bef", e);
        }
        return null;
    }
}

