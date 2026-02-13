/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cn.hutool.core.convert.Convert
 *  cn.hutool.core.util.BooleanUtil
 *  cn.hutool.extra.servlet.JakartaServletUtil
 *  jakarta.servlet.http.HttpServletRequest
 *  jakarta.servlet.http.HttpServletResponse
 *  jakarta.servlet.http.HttpSession
 *  org.springframework.util.LinkedCaseInsensitiveMap
 *  org.springframework.web.context.request.RequestAttributes
 *  org.springframework.web.context.request.RequestContextHolder
 *  org.springframework.web.context.request.ServletRequestAttributes
 */
package com.kuma.boot.common.utils.servlet;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.BooleanUtil;
import cn.hutool.extra.servlet.JakartaServletUtil;
import com.kuma.boot.common.utils.lang.StringUtils;
import com.kuma.boot.common.utils.log.LogUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Enumeration;
import java.util.Map;
import org.springframework.util.LinkedCaseInsensitiveMap;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

public class ServletUtils
extends JakartaServletUtil {
    public static String getParameter(String name) {
        return ServletUtils.getRequest().getParameter(name);
    }

    public static String getParameter(String name, String defaultValue) {
        return Convert.toStr((Object)ServletUtils.getRequest().getParameter(name), (String)defaultValue);
    }

    public static Integer getParameterToInt(String name) {
        return Convert.toInt((Object)ServletUtils.getRequest().getParameter(name));
    }

    public static Integer getParameterToInt(String name, Integer defaultValue) {
        return Convert.toInt((Object)ServletUtils.getRequest().getParameter(name), (Integer)defaultValue);
    }

    public static Boolean getParameterToBool(String name) {
        return BooleanUtil.toBoolean((String)ServletUtils.getRequest().getParameter(name));
    }

    public static Boolean getParameterToBool(String name, Boolean defaultValue) {
        return BooleanUtil.toBoolean((String)ServletUtils.getRequest().getParameter(name));
    }

    public static HttpServletRequest getRequest() {
        try {
            return ServletUtils.getRequestAttributes().getRequest();
        }
        catch (Exception e) {
            return null;
        }
    }

    public static HttpServletResponse getResponse() {
        try {
            return ServletUtils.getRequestAttributes().getResponse();
        }
        catch (Exception e) {
            return null;
        }
    }

    public static HttpSession getSession() {
        return ServletUtils.getRequest().getSession();
    }

    public static ServletRequestAttributes getRequestAttributes() {
        try {
            RequestAttributes attributes = RequestContextHolder.getRequestAttributes();
            return (ServletRequestAttributes)attributes;
        }
        catch (Exception e) {
            return null;
        }
    }

    public static String getHeader(HttpServletRequest request, String name) {
        String value = request.getHeader(name);
        if (StringUtils.isEmpty(value)) {
            return "";
        }
        return ServletUtils.urlDecode(value);
    }

    public static Map<String, String> getHeaders(HttpServletRequest request) {
        LinkedCaseInsensitiveMap map = new LinkedCaseInsensitiveMap();
        Enumeration enumeration = request.getHeaderNames();
        if (enumeration != null) {
            while (enumeration.hasMoreElements()) {
                String key = (String)enumeration.nextElement();
                String value = request.getHeader(key);
                map.put(key, value);
            }
        }
        return map;
    }

    public static void renderString(HttpServletResponse response, String string) {
        try {
            response.setStatus(200);
            response.setContentType("application/json");
            response.setCharacterEncoding(StandardCharsets.UTF_8.toString());
            response.getWriter().print(string);
        }
        catch (IOException e) {
            LogUtils.error(e);
        }
    }

    public static boolean isAjaxRequest(HttpServletRequest request) {
        String accept = request.getHeader("accept");
        if (accept != null && accept.contains("application/json")) {
            return true;
        }
        String xRequestedWith = request.getHeader("X-Requested-With");
        if (xRequestedWith != null && xRequestedWith.contains("XMLHttpRequest")) {
            return true;
        }
        String uri = request.getRequestURI();
        if (StringUtils.equalsAnyIgnoreCase((CharSequence)uri, (CharSequence[])new CharSequence[]{".json", ".xml"})) {
            return true;
        }
        String ajax = request.getParameter("__ajax");
        return StringUtils.equalsAnyIgnoreCase((CharSequence)ajax, (CharSequence[])new CharSequence[]{"json", "xml"});
    }

    public static String urlEncode(String str) {
        try {
            return URLEncoder.encode(str, "UTF-8");
        }
        catch (UnsupportedEncodingException e) {
            return "";
        }
    }

    public static String urlDecode(String str) {
        try {
            return URLDecoder.decode(str, "UTF-8");
        }
        catch (UnsupportedEncodingException e) {
            return "";
        }
    }
}

