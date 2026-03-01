/*
 * Copyright (c) 2020-2030, Kuma (2569277704@qq.com & https://blog.kumacloud.top/).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.kuma.boot.security.spring.utils;

import cn.hutool.extra.spring.SpringUtil;
import com.google.common.net.HttpHeaders;
import com.kuma.boot.security.spring.constants.BaseConstants;
import com.kuma.boot.security.spring.constants.Settings;
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
import com.kuma.boot.common.utils.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.Assert;
import org.springframework.util.PathMatcher;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.resource.ResourceUrlProvider;

/**
 * <p> Http与Servlet工具类.
 *
 * @author kuma
 * @version 2023.07
 * @since 2023-07-04 10:09:27
 */
public class WebUtils extends org.springframework.web.util.WebUtils {

    /**
     * 日志
     */
    private static final Logger log = LoggerFactory.getLogger(WebUtils.class);

    /**
     * xml http请求
     */
    public static final String XML_HTTP_REQUEST = "XMLHttpRequest";

    /**
     * 路径匹配器
     */
    private static final PathMatcher pathMatcher = new AntPathMatcher();

    /**
     * 得到路径匹配器
     *
     * @return {@link PathMatcher }
     * @since 2023-07-04 10:09:27
     */
    public static PathMatcher getPathMatcher() {
        return pathMatcher;
    }

    /**
     * 将 getSession 统一封装为一个方法，方便统一修改
     *
     * @param request request
     * @param create  是否创建新的 Session
     * @return {@link HttpSession }
     * @since 2023-07-04 10:09:27
     */
    public static HttpSession getSession(HttpServletRequest request, boolean create) {
        return request.getSession(create);
    }

    /**
     * 将 getSession 统一封装为一个方法，方便统一修改
     * <p>
     * 该方法默认不创建新的 getSession
     *
     * @param request {@link HttpServletRequest}
     * @return {@link HttpSession }
     * @since 2023-07-04 10:09:27
     */
    public static HttpSession getSession(HttpServletRequest request) {
        return getSession(request, Settings.CREATE_NEW_SESSION);
    }

    /**
     * 获取 Session Id。
     *
     * @param request {@link HttpServletRequest}
     * @param create  create 是否创建新的 Session
     * @return {@link String }
     * @since 2023-07-04 10:09:27
     */
    public static String getSessionId(HttpServletRequest request, boolean create) {
        HttpSession httpSession = getSession(request, create);
        if (ObjectUtils.isNotEmpty(httpSession)) {
            return httpSession.getId();
        } else {
            return null;
        }
    }

    /**
     * 获取 Session Id。
     *
     * @param request {@link HttpServletRequest}
     * @return {@link String }
     * @since 2023-07-04 10:09:27
     */
    public static String getSessionId(HttpServletRequest request) {
        return getSessionId(request, Settings.CREATE_NEW_SESSION);
    }

    /**
     * 获取 AUTHORIZATION 请求头内容
     *
     * @param request {@link HttpServletRequest}
     * @return {@link String }
     * @since 2023-07-04 10:09:27
     */
    public static String getAuthorizationHeader(HttpServletRequest request) {
        return request.getHeader(HttpHeaders.AUTHORIZATION);
    }

    /**
     * 获取 Bearer Token 的值
     *
     * @param request {@link HttpServletRequest}
     * @return {@link String }
     * @since 2023-07-04 10:09:27
     */
    public static String getBearerTokenValue(HttpServletRequest request) {
        String header = getAuthorizationHeader(request);
        if (StringUtils.isNotBlank(header)
                && StringUtils.startWith(header, BaseConstants.BEARER_TOKEN)) {
            return StringUtils.removePrefix(header, BaseConstants.BEARER_TOKEN);
        } else {
            return null;
        }
    }

    /**
     * 判断是否为静态资源
     *
     * @param uri 请求 URL
     * @return boolean
     * @since 2023-07-04 10:09:27
     */
    public static boolean isStaticResources(String uri) {
        ResourceUrlProvider resourceUrlProvider = SpringUtil.getBean(ResourceUrlProvider.class);
        String staticUri = resourceUrlProvider.getForLookupPath(uri);
        return staticUri != null;
    }

    /**
     * 判断路径是否与路径模式匹配
     *
     * @param patterns 路径模式字符串List
     * @param path     url
     * @return boolean
     * @since 2023-07-04 10:09:27
     */
    public static boolean isPathMatch(List<String> patterns, String path) {
        PathMatcher pathMatcher = getPathMatcher();
        for (String pattern : patterns) {
            if (pathMatcher.match(pattern, path)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 判断请求是否与设定的模式匹配
     *
     * @param patterns 路径匹配模式
     * @param request  请求
     * @return boolean
     * @since 2023-07-04 10:09:27
     */
    public static boolean isRequestMatched(List<String> patterns, HttpServletRequest request) {
        String url = request.getRequestURI();
        return isPathMatch(patterns, url);
    }

    /**
     * 读取cookie
     *
     * @param name cookie name
     * @return {@link String }
     * @since 2023-07-04 10:09:27
     */
    public static String getCookieValue(String name) {
        HttpServletRequest request = WebUtils.getRequest();
        Assert.notNull(request, "request from RequestContextHolder is null");
        return getCookieValue(request, name);
    }

    /**
     * 读取cookie
     *
     * @param request HttpServletRequest
     * @param name    cookie name
     * @return {@link String }
     * @since 2023-07-04 10:09:27
     */
    public static String getCookieValue(HttpServletRequest request, String name) {
        Cookie cookie = getCookie(request, name);
        return cookie != null ? cookie.getValue() : null;
    }

    /**
     * 清除 某个指定的cookie
     *
     * @param response HttpServletResponse
     * @param key      cookie key
     * @since 2023-07-04 10:09:27
     */
    public static void removeCookie(HttpServletResponse response, String key) {
        setCookie(response, key, null, 0);
    }

    /**
     * 设置cookie
     *
     * @param response        HttpServletResponse
     * @param name            cookie name
     * @param value           cookie value
     * @param maxAgeInSeconds maxage
     * @since 2023-07-04 10:09:27
     */
    public static void setCookie(
            HttpServletResponse response, String name, String value, int maxAgeInSeconds) {
        Cookie cookie = new Cookie(name, value);
        cookie.setPath("/");
        cookie.setMaxAge(maxAgeInSeconds);
        cookie.setHttpOnly(true);
        response.addCookie(cookie);
    }

    /**
     * 获取 HttpServletRequest
     *
     * @return {@link HttpServletRequest }
     * @since 2023-07-04 10:09:27
     */
    public static HttpServletRequest getRequest() {
        return ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes())
                .getRequest();
    }

    /**
     * 获取 HttpServletResponse
     *
     * @return {@link HttpServletResponse }
     * @since 2023-07-04 10:09:27
     */
    public static HttpServletResponse getResponse() {
        return ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes())
                .getResponse();
    }

    /**
     * 客户端返回JSON字符串
     *
     * @param response HttpServletResponse
     * @param object   需要转换的对象
     */
    //    public static void renderJson(HttpServletResponse response, Object object) {
    //        renderJson(response, Jackson2Utils.toJson(object),
    // MediaType.APPLICATION_JSON.toString());
    //    }

    /**
     * 客户端返回字符串
     *
     * @param response HttpServletResponse
     * @param string   需要绘制的信息
     * @param type     类型
     * @since 2023-07-04 10:09:27
     */
    public static void renderJson(HttpServletResponse response, String string, String type) {
        try {
            response.setContentType(type);
            response.setCharacterEncoding(StandardCharsets.UTF_8.toString());
            response.getWriter().print(string);
            response.getWriter().flush();
            response.getWriter().close();
        } catch (IOException e) {
            log.error("Render response to Json error!");
        }
    }

    /**
     * 到http
     *
     * @param request 请求
     * @return {@link HttpServletRequest }
     * @since 2023-07-04 10:09:28
     */
    public static HttpServletRequest toHttp(ServletRequest request) {
        return (HttpServletRequest) request;
    }

    /**
     * 到http
     *
     * @param response 响应
     * @return {@link HttpServletResponse }
     * @since 2023-07-04 10:09:28
     */
    public static HttpServletResponse toHttp(ServletResponse response) {
        return (HttpServletResponse) response;
    }

    /**
     * 头包含媒体类型吗
     *
     * @param request    请求
     * @param headerType 头类型
     * @param mediaType  媒体类型
     * @return boolean
     * @since 2023-07-04 10:09:28
     */
    private static boolean isHeaderContainMediaType(
            ServletRequest request, String headerType, String mediaType) {
        String header = toHttp(request).getHeader(headerType);
        return StringUtils.isNotEmpty(header) && header.contains(mediaType);
    }

    /**
     * 头包含json
     *
     * @param request    请求
     * @param headerType 头类型
     * @return boolean
     * @since 2023-07-04 10:09:28
     */
    private static boolean isHeaderContainJson(ServletRequest request, String headerType) {
        return isHeaderContainMediaType(request, headerType, MediaType.APPLICATION_JSON_VALUE);
    }

    /**
     * 内容类型是头包含json
     *
     * @param request 请求
     * @return boolean
     * @since 2023-07-04 10:09:28
     */
    private static boolean isContentTypeHeaderContainJson(ServletRequest request) {
        return isHeaderContainJson(request, HttpHeaders.CONTENT_TYPE);
    }

    /**
     * accept标头包含json吗
     *
     * @param request 请求
     * @return boolean
     * @since 2023-07-04 10:09:28
     */
    private static boolean isAcceptHeaderContainJson(ServletRequest request) {
        return isHeaderContainJson(request, HttpHeaders.ACCEPT);
    }

    /**
     * 是包含ajax标记
     *
     * @param request 请求
     * @return boolean
     * @since 2023-07-04 10:09:28
     */
    private static boolean isContainAjaxFlag(ServletRequest request) {
        String xRequestedWith = WebUtils.toHttp(request).getHeader(HttpHeaders.X_REQUESTED_WITH);
        return XML_HTTP_REQUEST.equalsIgnoreCase(xRequestedWith);
    }

    /**
     * ajax请求
     *
     * @param request 请求
     * @return boolean
     * @since 2023-07-04 10:09:29
     */
    public static boolean isAjaxRequest(ServletRequest request) {

        // 使用HttpServletRequest中的header检测请求是否为ajax, 如果是ajax则返回json, 如果为非ajax则返回view(即ModelAndView)
        if (isContentTypeHeaderContainJson(request)
                || isAcceptHeaderContainJson(request)
                || isContainAjaxFlag(request)) {
            log.info("Is Ajax Request!!!!!");
            return true;
        }

        log.info("Not a Ajax Request!!!!!");
        return false;
    }

    /**
     * 是静态资源请求
     *
     * @param request 请求
     * @return boolean
     * @since 2023-07-04 10:09:29
     */
    public static boolean isStaticResourcesRequest(HttpServletRequest request) {
        String requestPath = request.getServletPath();
        if (StringUtils.endWith(requestPath, "html")) {
            return false;
        } else {
            return isStaticResources(requestPath);
        }
    }

    /**
     * 从Map中获取url匹配的对象。
     *
     * @param path 传入的url
     * @param map  以路径匹配pattern为key的map
     * @return {@link T }
     * @since 2023-07-04 10:09:29
     */
    public static <T> T getPathMatchedObject(String path, Map<String, T> map) {
        PathMatcher pathMatcher = getPathMatcher();
        for (String key : map.keySet()) {
            if (pathMatcher.match(key, path)) {
                return map.get(key);
            }
        }

        return null;
    }

    /**
     * get请求负载
     *
     * @param request 请求
     * @return {@link String }
     * @since 2023-07-04 10:09:29
     */
    public static String getRequestPayload(ServletRequest request) {
        StringBuilder stringBuilder = new StringBuilder();
        try (BufferedReader reader = request.getReader(); ) {
            char[] buffer = new char[1024];
            int length;
            while ((length = reader.read(buffer)) != -1) {
                stringBuilder.append(buffer, 0, length);
            }
        } catch (IOException e) {
            log.error("Get Request Payload Error!");
        }

        return stringBuilder.toString();
    }
}
