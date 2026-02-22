/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  jakarta.servlet.ServletRequest
 *  jakarta.servlet.ServletResponse
 *  jakarta.servlet.http.Cookie
 *  jakarta.servlet.http.HttpServletRequest
 *  jakarta.servlet.http.HttpServletResponse
 *  jakarta.servlet.http.HttpSession
 *  org.apache.commons.lang3.ObjectUtils
 *  org.apache.commons.lang3.StringUtils
 *  org.dromara.hutool.extra.spring.SpringUtil
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.util.AntPathMatcher
 *  org.springframework.util.Assert
 *  org.springframework.util.PathMatcher
 *  org.springframework.web.context.request.RequestContextHolder
 *  org.springframework.web.context.request.ServletRequestAttributes
 *  org.springframework.web.servlet.resource.ResourceUrlProvider
 *  org.springframework.web.util.WebUtils
 */
package com.kuma.boot.security.spring.utils;

import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.dromara.hutool.extra.spring.SpringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.Assert;
import org.springframework.util.PathMatcher;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.resource.ResourceUrlProvider;

public class WebUtils
extends org.springframework.web.util.WebUtils {
    private static final Logger log = LoggerFactory.getLogger(WebUtils.class);
    public static final String XML_HTTP_REQUEST = "XMLHttpRequest";
    private static final PathMatcher pathMatcher = new AntPathMatcher();

    public static PathMatcher getPathMatcher() {
        return pathMatcher;
    }

    public static HttpSession getSession(HttpServletRequest request, boolean create) {
        return request.getSession(create);
    }

    public static HttpSession getSession(HttpServletRequest request) {
        return WebUtils.getSession(request, false);
    }

    public static String getSessionId(HttpServletRequest request, boolean create) {
        HttpSession httpSession = WebUtils.getSession(request, create);
        if (ObjectUtils.isNotEmpty((Object)httpSession)) {
            return httpSession.getId();
        }
        return null;
    }

    public static String getSessionId(HttpServletRequest request) {
        return WebUtils.getSessionId(request, false);
    }

    public static String getAuthorizationHeader(HttpServletRequest request) {
        return request.getHeader("Authorization");
    }

    public static String getBearerTokenValue(HttpServletRequest request) {
        String header = WebUtils.getAuthorizationHeader(request);
        if (StringUtils.isNotBlank((CharSequence)header) && StringUtils.startsWith((CharSequence)header, (CharSequence)"Bearer ")) {
            return StringUtils.remove((String)header, (String)"Bearer ");
        }
        return null;
    }

    public static boolean isStaticResources(String uri) {
        ResourceUrlProvider resourceUrlProvider = (ResourceUrlProvider)SpringUtil.getBean(ResourceUrlProvider.class, (Object[])new Object[0]);
        String staticUri = resourceUrlProvider.getForLookupPath(uri);
        return staticUri != null;
    }

    public static boolean isPathMatch(List<String> patterns, String path) {
        PathMatcher pathMatcher = WebUtils.getPathMatcher();
        for (String pattern : patterns) {
            if (!pathMatcher.match(pattern, path)) continue;
            return true;
        }
        return false;
    }

    public static boolean isRequestMatched(List<String> patterns, HttpServletRequest request) {
        String url = request.getRequestURI();
        return WebUtils.isPathMatch(patterns, url);
    }

    public static String getCookieValue(String name) {
        HttpServletRequest request = WebUtils.getRequest();
        Assert.notNull((Object)request, (String)"request from RequestContextHolder is null");
        return WebUtils.getCookieValue(request, name);
    }

    public static String getCookieValue(HttpServletRequest request, String name) {
        Cookie cookie = WebUtils.getCookie((HttpServletRequest)request, (String)name);
        return cookie != null ? cookie.getValue() : null;
    }

    public static void removeCookie(HttpServletResponse response, String key) {
        WebUtils.setCookie(response, key, null, 0);
    }

    public static void setCookie(HttpServletResponse response, String name, String value, int maxAgeInSeconds) {
        Cookie cookie = new Cookie(name, value);
        cookie.setPath("/");
        cookie.setMaxAge(maxAgeInSeconds);
        cookie.setHttpOnly(true);
        response.addCookie(cookie);
    }

    public static HttpServletRequest getRequest() {
        return ((ServletRequestAttributes)RequestContextHolder.getRequestAttributes()).getRequest();
    }

    public static HttpServletResponse getResponse() {
        return ((ServletRequestAttributes)RequestContextHolder.getRequestAttributes()).getResponse();
    }

    public static void renderJson(HttpServletResponse response, String string, String type) {
        try {
            response.setContentType(type);
            response.setCharacterEncoding(StandardCharsets.UTF_8.toString());
            response.getWriter().print(string);
            response.getWriter().flush();
            response.getWriter().close();
        }
        catch (IOException e) {
            log.error("Render response to Json error!");
        }
    }

    public static HttpServletRequest toHttp(ServletRequest request) {
        return (HttpServletRequest)request;
    }

    public static HttpServletResponse toHttp(ServletResponse response) {
        return (HttpServletResponse)response;
    }

    private static boolean isHeaderContainMediaType(ServletRequest request, String headerType, String mediaType) {
        String header = WebUtils.toHttp(request).getHeader(headerType);
        return StringUtils.isNotEmpty((CharSequence)header) && header.contains(mediaType);
    }

    private static boolean isHeaderContainJson(ServletRequest request, String headerType) {
        return WebUtils.isHeaderContainMediaType(request, headerType, "application/json");
    }

    private static boolean isContentTypeHeaderContainJson(ServletRequest request) {
        return WebUtils.isHeaderContainJson(request, "Content-Type");
    }

    private static boolean isAcceptHeaderContainJson(ServletRequest request) {
        return WebUtils.isHeaderContainJson(request, "Accept");
    }

    private static boolean isContainAjaxFlag(ServletRequest request) {
        String xRequestedWith = WebUtils.toHttp(request).getHeader("X-Requested-With");
        return XML_HTTP_REQUEST.equalsIgnoreCase(xRequestedWith);
    }

    public static boolean isAjaxRequest(ServletRequest request) {
        if (WebUtils.isContentTypeHeaderContainJson(request) || WebUtils.isAcceptHeaderContainJson(request) || WebUtils.isContainAjaxFlag(request)) {
            log.info("Is Ajax Request!!!!!");
            return true;
        }
        log.info("Not a Ajax Request!!!!!");
        return false;
    }

    public static boolean isStaticResourcesRequest(HttpServletRequest request) {
        String requestPath = request.getServletPath();
        if (StringUtils.endsWith((CharSequence)requestPath, (CharSequence)"html")) {
            return false;
        }
        return WebUtils.isStaticResources(requestPath);
    }

    public static <T> T getPathMatchedObject(String path, Map<String, T> map) {
        PathMatcher pathMatcher = WebUtils.getPathMatcher();
        for (String key : map.keySet()) {
            if (!pathMatcher.match(key, path)) continue;
            return map.get(key);
        }
        return null;
    }

    public static String getRequestPayload(ServletRequest request) {
        StringBuilder stringBuilder = new StringBuilder();
        try (BufferedReader reader = request.getReader();){
            int length;
            char[] buffer = new char[1024];
            while ((length = reader.read(buffer)) != -1) {
                stringBuilder.append(buffer, 0, length);
            }
        }
        catch (IOException e) {
            log.error("Get Request Payload Error!");
        }
        return stringBuilder.toString();
    }
}

