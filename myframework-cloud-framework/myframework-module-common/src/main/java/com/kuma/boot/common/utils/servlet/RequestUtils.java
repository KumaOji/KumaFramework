/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cn.hutool.core.util.StrUtil
 *  com.alibaba.ttl.TransmittableThreadLocal
 *  jakarta.servlet.ServletInputStream
 *  jakarta.servlet.http.HttpServletRequest
 *  jakarta.servlet.http.HttpServletResponse
 *  org.springframework.core.ResolvableType
 *  org.springframework.core.codec.ByteArrayDecoder
 *  org.springframework.core.codec.Decoder
 *  org.springframework.http.HttpHeaders
 *  org.springframework.http.ReactiveHttpInputMessage
 *  org.springframework.http.codec.DecoderHttpMessageReader
 *  org.springframework.http.server.reactive.ServerHttpRequest
 *  org.springframework.util.CollectionUtils
 *  org.springframework.util.LinkedCaseInsensitiveMap
 *  org.springframework.util.StringUtils
 *  org.springframework.web.context.request.RequestAttributes
 *  org.springframework.web.context.request.RequestContextHolder
 *  org.springframework.web.context.request.ServletRequestAttributes
 *  reactor.core.publisher.Mono
 */
package com.kuma.boot.common.utils.servlet;

import cn.hutool.core.util.StrUtil;
import com.alibaba.ttl.TransmittableThreadLocal;
import com.kuma.boot.common.utils.common.PropertyUtils;
import com.kuma.boot.common.utils.log.LogUtils;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Pattern;
import org.springframework.core.ResolvableType;
import org.springframework.core.codec.ByteArrayDecoder;
import org.springframework.core.codec.Decoder;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ReactiveHttpInputMessage;
import org.springframework.http.codec.DecoderHttpMessageReader;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.util.CollectionUtils;
import org.springframework.util.LinkedCaseInsensitiveMap;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import reactor.core.publisher.Mono;

public class RequestUtils {
    private static final String UNKNOWN = "unknown";
    public static final String IP_INCLUDE_REGEX_KEY = "kuma.boot.core.ip.include.regex";
    public static final String IP_EXCLUDE_REGEX_KEY = "kuma.boot.core.ip.exclude.regex";
    public static final String UNKNOWN_STR = "unknown";
    public static final ThreadLocal<WebContext> WEB_CONTEXT = new TransmittableThreadLocal();

    public static WebContext getContext() {
        return WEB_CONTEXT.get();
    }

    public static void bindContext(HttpServletRequest request, HttpServletResponse response) {
        WEB_CONTEXT.set(new WebContext(request, response));
    }

    public static void clearContext() {
        WEB_CONTEXT.remove();
    }

    public static HttpServletRequest getRequest() {
        WebContext webContext = RequestUtils.getContext();
        if (webContext == null) {
            RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
            if (requestAttributes != null) {
                ServletRequestAttributes attributes = (ServletRequestAttributes)requestAttributes;
                return attributes.getRequest();
            }
        } else {
            return webContext.request;
        }
        return null;
    }

    public static HttpServletResponse getResponse() {
        WebContext webContext = RequestUtils.getContext();
        if (webContext == null) {
            RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
            if (requestAttributes != null) {
                ServletRequestAttributes attributes = (ServletRequestAttributes)requestAttributes;
                return attributes.getResponse();
            }
        } else {
            return webContext.response;
        }
        return null;
    }

    public static HttpHeaders headers(HttpServletRequest request) {
        LinkedCaseInsensitiveMap insensitiveMap = new LinkedCaseInsensitiveMap();
        Enumeration headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String headerName = (String)headerNames.nextElement();
            Enumeration headers = request.getHeaders(headerName);
            insensitiveMap.put(headerName, Collections.list(headers));
        }
        return new HttpHeaders(CollectionUtils.toMultiValueMap((Map)insensitiveMap));
    }

    public static Map<String, String> getAllRequestParam(HttpServletRequest request) {
        HashMap<String, String> res = new HashMap<String, String>();
        Enumeration temp = request.getParameterNames();
        if (null != temp) {
            while (temp.hasMoreElements()) {
                String en = (String)temp.nextElement();
                String value = request.getParameter(en);
                res.put(en, value);
            }
        }
        return res;
    }

    public static Map<String, String> getAllRequestHeaders(HttpServletRequest request) {
        HashMap<String, String> res = new HashMap<String, String>();
        Enumeration temp = request.getHeaderNames();
        if (null != temp) {
            while (temp.hasMoreElements()) {
                String en = (String)temp.nextElement();
                String value = request.getHeader(en);
                res.put(en, value);
            }
        }
        return res;
    }

    public static String getHeader(String headerName) {
        HttpServletRequest request = RequestUtils.getRequest();
        return request.getHeader(headerName);
    }

    public static String getBodyString(ServerHttpRequest serverHttpRequest) {
        DecoderHttpMessageReader httpMessageReader = new DecoderHttpMessageReader((Decoder)new ByteArrayDecoder());
        ResolvableType resolvableType = ResolvableType.forClass(byte[].class);
        Mono mono = httpMessageReader.readMono(resolvableType, (ReactiveHttpInputMessage)serverHttpRequest, Collections.emptyMap());
        return (String)mono.map(String::new).block();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static String getBodyString(HttpServletRequest request) throws IOException {
        StringBuilder sb = new StringBuilder();
        ServletInputStream inputStream = null;
        BufferedReader reader = null;
        try {
            inputStream = request.getInputStream();
            reader = new BufferedReader(new InputStreamReader((InputStream)inputStream, StandardCharsets.UTF_8));
            String line = "";
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
        }
        catch (IOException e) {
            LogUtils.error(e);
        }
        finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                }
                catch (IOException e) {
                    LogUtils.error(e);
                }
            }
            if (reader != null) {
                try {
                    reader.close();
                }
                catch (IOException e) {
                    LogUtils.error(e);
                }
            }
        }
        return sb.toString().trim();
    }

    public static HttpServletRequest getHttpServletRequest() {
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        return requestAttributes == null ? null : ((ServletRequestAttributes)requestAttributes).getRequest();
    }

    public static HttpServletResponse getHttpServletResponse() {
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        return requestAttributes == null ? null : ((ServletRequestAttributes)requestAttributes).getResponse();
    }

    public static String getHttpServletRequestIpAddress() {
        HttpServletRequest request = RequestUtils.getHttpServletRequest();
        assert (request != null);
        return RequestUtils.getHttpServletRequestIpAddress(request);
    }

    public static String getHttpServletRequestIpAddress(HttpServletRequest request) {
        String ip = request.getHeader("x-forwarded-for");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        if (ip.contains(",")) {
            ip = ip.split(",")[0];
        }
        return "0:0:0:0:0:0:0:1".equals(ip) ? "127.0.0.1" : ip;
    }

    public static String getServerHttpRequestIpAddress(ServerHttpRequest request) {
        HttpHeaders headers = request.getHeaders();
        String ip = headers.getFirst("x-forwarded-for");
        if (ip != null && ip.length() != 0 && !"unknown".equalsIgnoreCase(ip) && ip.contains(",")) {
            ip = ip.split(",")[0];
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = headers.getFirst("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = headers.getFirst("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = headers.getFirst("HTTP_CLIENT_IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = headers.getFirst("HTTP_X_FORWARDED_FOR");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = headers.getFirst("X-Real-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = Objects.requireNonNull(request.getRemoteAddress()).getAddress().getHostAddress();
        }
        return "0:0:0:0:0:0:0:1".equals(ip) ? "127.0.0.1" : ip;
    }

    public static String getRemoteAddr(HttpServletRequest request) {
        String ip;
        block3: {
            block2: {
                ip = request.getHeader("X-Forwarded-For");
                if (!RequestUtils.isEmptyIp(ip)) break block2;
                ip = request.getHeader("Proxy-Client-IP");
                if (!RequestUtils.isEmptyIp(ip) || !RequestUtils.isEmptyIp(ip = request.getHeader("WL-Proxy-Client-IP")) || !RequestUtils.isEmptyIp(ip = request.getHeader("HTTP_CLIENT_IP")) || !RequestUtils.isEmptyIp(ip = request.getHeader("HTTP_X_FORWARDED_FOR")) || !"127.0.0.1".equals(ip = request.getRemoteAddr()) && !"0:0:0:0:0:0:0:1".equals(ip)) break block3;
                ip = RequestUtils.getLocalAddr();
                break block3;
            }
            if (ip.length() > 15) {
                String[] ips;
                for (String strIp : ips = ip.split(",")) {
                    if (RequestUtils.isEmptyIp(ip)) continue;
                    ip = strIp;
                    break;
                }
            }
        }
        return ip;
    }

    public static boolean isEmptyIp(String ip) {
        return StrUtil.isEmpty((CharSequence)ip) || "unknown".equalsIgnoreCase(ip);
    }

    public static String getLocalAddr() {
        try {
            return InetAddress.getLocalHost().getHostAddress();
        }
        catch (UnknownHostException e) {
            LogUtils.error("InetAddress.getLocalHost()--error", e);
            return "";
        }
    }

    public static String getIpAddress() {
        String ipExclude = PropertyUtils.getPropertyCache(IP_EXCLUDE_REGEX_KEY, "");
        if (StringUtils.hasText((String)ipExclude)) {
            String regex = RequestUtils.buildRegex(ipExclude);
            return RequestUtils.getIpAddressExMatched(regex);
        }
        String ipInclude = PropertyUtils.getPropertyCache(IP_INCLUDE_REGEX_KEY, "");
        if (StringUtils.hasText((String)ipInclude)) {
            String regex = RequestUtils.buildRegex(ipInclude);
            return RequestUtils.getIpAddressMatched(regex);
        }
        return RequestUtils.getIpAddress0();
    }

    public static String getIpAddress0() {
        try {
            Enumeration<NetworkInterface> allNetInterfaces = NetworkInterface.getNetworkInterfaces();
            while (allNetInterfaces.hasMoreElements()) {
                NetworkInterface netInterface = allNetInterfaces.nextElement();
                if (netInterface.isLoopback() || netInterface.isVirtual() || !netInterface.isUp() || netInterface.isPointToPoint()) continue;
                Enumeration<InetAddress> addresses = netInterface.getInetAddresses();
                while (addresses.hasMoreElements()) {
                    InetAddress ip = addresses.nextElement();
                    if (!(ip instanceof Inet4Address)) continue;
                    return ip.getHostAddress();
                }
            }
        }
        catch (Exception e) {
            LogUtils.error(e);
        }
        return "";
    }

    public static String getIpAddressMatched(String regex) {
        try {
            Enumeration<NetworkInterface> allNetInterfaces = NetworkInterface.getNetworkInterfaces();
            while (allNetInterfaces.hasMoreElements()) {
                NetworkInterface netInterface = allNetInterfaces.nextElement();
                if (netInterface.isLoopback() || netInterface.isVirtual() || !netInterface.isUp()) continue;
                Enumeration<InetAddress> addresses = netInterface.getInetAddresses();
                while (addresses.hasMoreElements()) {
                    String strIp;
                    InetAddress ip = addresses.nextElement();
                    if (!(ip instanceof Inet4Address) || !Pattern.matches(regex, strIp = ip.getHostAddress())) continue;
                    return strIp;
                }
            }
        }
        catch (Exception e) {
            LogUtils.error(e);
        }
        return "";
    }

    public static String getIpAddressExMatched(String regex) {
        try {
            Enumeration<NetworkInterface> allNetInterfaces = NetworkInterface.getNetworkInterfaces();
            while (allNetInterfaces.hasMoreElements()) {
                NetworkInterface netInterface = allNetInterfaces.nextElement();
                if (netInterface.isLoopback() || netInterface.isVirtual() || !netInterface.isUp()) continue;
                Enumeration<InetAddress> addresses = netInterface.getInetAddresses();
                while (addresses.hasMoreElements()) {
                    String strIp;
                    InetAddress ip = addresses.nextElement();
                    if (!(ip instanceof Inet4Address) || Pattern.matches(regex, strIp = ip.getHostAddress())) continue;
                    return strIp;
                }
            }
        }
        catch (Exception e) {
            LogUtils.error(e);
        }
        return "";
    }

    private static String buildRegex(String source) {
        String[] strSource;
        StringBuilder sb = new StringBuilder();
        for (String s : strSource = source.split(",")) {
            sb.append("|(^").append(s).append(".*)");
        }
        String regex = sb.toString();
        if (!com.kuma.boot.common.utils.lang.StringUtils.isEmpty(regex)) {
            return regex.substring(1);
        }
        return "";
    }

    public static String getRemoteAddr(ServerHttpRequest request) {
        String ip;
        block3: {
            block2: {
                Map headers = request.getHeaders().toSingleValueMap();
                ip = (String)headers.get("X-Forwarded-For");
                if (!RequestUtils.isEmptyIp(ip)) break block2;
                ip = (String)headers.get("Proxy-Client-IP");
                if (!RequestUtils.isEmptyIp(ip) || !RequestUtils.isEmptyIp(ip = (String)headers.get("WL-Proxy-Client-IP")) || !RequestUtils.isEmptyIp(ip = (String)headers.get("HTTP_CLIENT_IP")) || !RequestUtils.isEmptyIp(ip = (String)headers.get("HTTP_X_FORWARDED_FOR")) || !"127.0.0.1".equals(ip = request.getRemoteAddress().getAddress().getHostAddress()) && !"0:0:0:0:0:0:0:1".equals(ip)) break block3;
                ip = RequestUtils.getLocalAddr();
                break block3;
            }
            if (ip.length() > 15) {
                String[] ips;
                for (String strIp : ips = ip.split(",")) {
                    if (RequestUtils.isEmptyIp(ip)) continue;
                    ip = strIp;
                    break;
                }
            }
        }
        return ip;
    }

    public static boolean excludeActuator(HttpServletRequest request) {
        return request.getRequestURI().startsWith("/actuator");
    }

    public static class WebContext {
        private final HttpServletRequest request;
        private final HttpServletResponse response;

        public WebContext(HttpServletRequest request, HttpServletResponse response) {
            this.request = request;
            this.response = response;
        }
    }
}

