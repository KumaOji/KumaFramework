/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  com.kuma.boot.common.utils.log.LogUtils
 *  jakarta.servlet.Filter
 *  jakarta.servlet.FilterChain
 *  jakarta.servlet.FilterConfig
 *  jakarta.servlet.ServletException
 *  jakarta.servlet.ServletRequest
 *  jakarta.servlet.ServletResponse
 *  jakarta.servlet.http.HttpServletRequest
 *  jakarta.servlet.http.HttpServletResponse
 *  org.springframework.util.StringUtils
 */
package com.kuma.boot.web.request.altas.web;

import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.boot.web.request.altas.config.LogConfigProperties;
import com.kuma.boot.web.request.altas.context.TraceIdHolder;
import com.kuma.boot.web.request.altas.serializer.ArgumentFormatConfig;
import com.kuma.boot.web.request.altas.serializer.ArgumentFormatType;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Map;
import org.springframework.util.StringUtils;

public class LoggingFilter
implements Filter {
    private final LogConfigProperties properties;
    private final ArgumentFormatConfig argumentFormatConfig;

    public LoggingFilter(LogConfigProperties properties, ArgumentFormatConfig argumentFormatConfig) {
        this.properties = properties;
        this.argumentFormatConfig = argumentFormatConfig != null ? argumentFormatConfig : new ArgumentFormatConfig();
        LogUtils.debug((String)"=== LoggingFilter Constructor Debug ===", (Object[])new Object[0]);
        LogUtils.debug((String)"Received LogConfigProperties: {}", (Object[])new Object[]{properties});
        if (properties != null && properties.getHttpLog() != null) {
            LogUtils.debug((String)"HTTP Log urlFormat: '{}'", (Object[])new Object[]{properties.getHttpLog().getUrlFormat()});
            LogUtils.debug((String)"HTTP Log includeQueryString: {}", (Object[])new Object[]{properties.getHttpLog().isIncludeQueryString()});
        } else {
            LogUtils.debug((String)"No HTTP log configuration found in properties", (Object[])new Object[0]);
        }
        LogUtils.debug((String)"===========================================", (Object[])new Object[0]);
    }

    public LoggingFilter(LogConfigProperties properties) {
        this.properties = properties;
        this.argumentFormatConfig = new ArgumentFormatConfig();
    }

    public void init(FilterConfig filterConfig) throws ServletException {
        LogUtils.info((String)"Atlas Log filter initialized successfully", (Object[])new Object[0]);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        String recursionKey = "ATLAS_LOG_FILTER_PROCESSED";
        if (request.getAttribute(recursionKey) != null) {
            chain.doFilter(request, response);
            return;
        }
        request.setAttribute(recursionKey, (Object)true);
        if (!(request instanceof HttpServletRequest) || !(response instanceof HttpServletResponse)) {
            chain.doFilter(request, response);
            return;
        }
        HttpServletRequest httpRequest = (HttpServletRequest)request;
        HttpServletResponse httpResponse = (HttpServletResponse)response;
        String traceId = TraceIdHolder.getTraceIdIfPresent();
        if (traceId == null) {
            traceId = httpRequest.getHeader("X-Trace-Id");
            if (traceId == null || traceId.trim().isEmpty()) {
                traceId = TraceIdHolder.generateTraceId();
                LogUtils.debug((String)"LoggingFilter generated new TraceId: {}", (Object[])new Object[]{traceId});
            } else {
                LogUtils.debug((String)"LoggingFilter obtained TraceId from request header: {}", (Object[])new Object[]{traceId});
            }
            TraceIdHolder.setTraceId(traceId);
            httpResponse.setHeader("X-Trace-Id", traceId);
        }
        LogUtils.debug((String)"LoggingFilter started - TraceId: {}", (Object[])new Object[]{traceId});
        long startTime = System.currentTimeMillis();
        try {
            this.logRequestStart(httpRequest, traceId);
            chain.doFilter(request, response);
        }
        catch (Throwable throwable) {
            String currentTraceId = TraceIdHolder.getTraceIdIfPresent();
            LogUtils.debug((String)"LoggingFilter finished - saved TraceId: {}, current TraceId: {}", (Object[])new Object[]{traceId, currentTraceId});
            long executionTime = System.currentTimeMillis() - startTime;
            this.logRequestEnd(httpRequest, httpResponse, executionTime, traceId);
            throw throwable;
        }
        String currentTraceId = TraceIdHolder.getTraceIdIfPresent();
        LogUtils.debug((String)"LoggingFilter finished - saved TraceId: {}, current TraceId: {}", (Object[])new Object[]{traceId, currentTraceId});
        long executionTime = System.currentTimeMillis() - startTime;
        this.logRequestEnd(httpRequest, httpResponse, executionTime, traceId);
    }

    private void logRequestStart(HttpServletRequest request, String traceId) {
        if (LogUtils.isDebugEnabled()) {
            String method = request.getMethod();
            String uri = request.getRequestURI();
            String queryString = request.getQueryString();
            String remoteAddr = this.getClientIpAddress(request);
            StringBuilder logMsg = new StringBuilder();
            logMsg.append("TraceId: ").append(traceId).append(" | ");
            String formattedUrl = this.formatUrl(method, uri, queryString, remoteAddr);
            logMsg.append("URL: ").append(formattedUrl);
            logMsg.append(" | HTTP\u8bf7\u6c42\u5f00\u59cb");
            if (this.properties.getHttpLog().isLogFullParameters()) {
                String headers;
                String formattedParams;
                if (StringUtils.hasText((String)queryString) && this.properties.getHttpLog().isIncludeQueryString() && StringUtils.hasText((String)(formattedParams = this.formatRequestParameters(request)))) {
                    logMsg.append(" | Parameters: ").append(formattedParams);
                }
                if (this.properties.getHttpLog().isIncludeHeaders() && StringUtils.hasText((String)(headers = this.getFilteredHeaders(request)))) {
                    logMsg.append(" | Headers: ").append(headers);
                }
            }
            logMsg.append(" | RemoteAddr: ").append(remoteAddr);
            LogUtils.debug((String)logMsg.toString(), (Object[])new Object[0]);
        }
    }

    private void logRequestEnd(HttpServletRequest request, HttpServletResponse response, long executionTime, String traceId) {
        String method = request.getMethod();
        String uri = request.getRequestURI();
        String queryString = request.getQueryString();
        String remoteAddr = this.getClientIpAddress(request);
        int status = response.getStatus();
        StringBuilder logMsg = new StringBuilder();
        logMsg.append("TraceId: ").append(traceId).append(" | ");
        String formattedUrl = this.formatUrl(method, uri, queryString, remoteAddr);
        logMsg.append("URL: ").append(formattedUrl).append(" | ");
        logMsg.append("HTTP\u8bf7\u6c42\u5b8c\u6210 | ");
        logMsg.append("Status: ").append(status).append(" | ");
        logMsg.append("ExecutionTime: ").append(executionTime).append("ms");
        if (status >= 500) {
            LogUtils.error((String)logMsg.toString(), (Object[])new Object[0]);
        } else if (status >= 400) {
            LogUtils.warn((String)logMsg.toString(), (Object[])new Object[0]);
        } else if (executionTime > this.properties.getPerformance().getSlowThreshold()) {
            LogUtils.warn((String)(logMsg.toString() + " | SlowRequest: true"), (Object[])new Object[0]);
        } else if (LogUtils.isInfoEnabled()) {
            LogUtils.info((String)logMsg.toString(), (Object[])new Object[0]);
        }
    }

    private String formatUrl(String method, String uri, String queryString, String remoteAddr) {
        String format = this.properties.getHttpLog().getUrlFormat();
        String result = format.replace("{method}", method != null ? method : "").replace("{uri}", uri != null ? uri : "").replace("{remoteAddr}", remoteAddr != null ? remoteAddr : "");
        result = this.properties.getHttpLog().isIncludeQueryString() && StringUtils.hasText((String)queryString) ? result.replace("{queryString}", "?" + queryString) : result.replace("{queryString}", "");
        return result.trim();
    }

    private String getFilteredHeaders(HttpServletRequest request) {
        StringBuilder headers = new StringBuilder();
        Enumeration headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String headerName = (String)headerNames.nextElement();
            boolean shouldExclude = this.properties.getHttpLog().getExcludeHeaders().stream().anyMatch(excludeHeader -> headerName.toLowerCase().contains(excludeHeader.toLowerCase()));
            if (shouldExclude) continue;
            if (headers.length() > 0) {
                headers.append(", ");
            }
            headers.append(headerName).append("=").append(request.getHeader(headerName));
        }
        return headers.toString();
    }

    private String formatRequestParameters(HttpServletRequest request) {
        Map parameterMap = request.getParameterMap();
        if (parameterMap == null || parameterMap.isEmpty()) {
            return "";
        }
        if (this.argumentFormatConfig.getType() == ArgumentFormatType.KEY_VALUE) {
            return this.formatParametersAsKeyValue(parameterMap);
        }
        return this.formatParametersAsJson(parameterMap);
    }

    private String formatParametersAsKeyValue(Map<String, String[]> parameterMap) {
        StringBuilder sb = new StringBuilder();
        boolean first = true;
        for (Map.Entry<String, String[]> entry : parameterMap.entrySet()) {
            String key = entry.getKey();
            String[] values = entry.getValue();
            if (!first) {
                sb.append(this.argumentFormatConfig.getSeparator());
            }
            first = false;
            sb.append(key).append(this.argumentFormatConfig.getKeyValueSeparator());
            if (values.length == 1) {
                sb.append(values[0]);
                continue;
            }
            sb.append("[");
            for (int i = 0; i < values.length; ++i) {
                if (i > 0) {
                    sb.append(",");
                }
                sb.append(values[i]);
            }
            sb.append("]");
        }
        return sb.toString();
    }

    private String formatParametersAsJson(Map<String, String[]> parameterMap) {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        boolean first = true;
        for (Map.Entry<String, String[]> entry : parameterMap.entrySet()) {
            if (!first) {
                sb.append(",");
            }
            first = false;
            sb.append("\"").append(entry.getKey()).append("\":");
            String[] values = entry.getValue();
            if (values.length == 1) {
                sb.append("\"").append(values[0]).append("\"");
                continue;
            }
            sb.append("[");
            for (int i = 0; i < values.length; ++i) {
                if (i > 0) {
                    sb.append(",");
                }
                sb.append("\"").append(values[i]).append("\"");
            }
            sb.append("]");
        }
        sb.append("}");
        return sb.toString();
    }

    private String getClientIpAddress(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (StringUtils.hasText((String)xForwardedFor) && !"unknown".equalsIgnoreCase(xForwardedFor)) {
            return xForwardedFor.split(",")[0].trim();
        }
        String xRealIp = request.getHeader("X-Real-IP");
        if (StringUtils.hasText((String)xRealIp) && !"unknown".equalsIgnoreCase(xRealIp)) {
            return xRealIp;
        }
        return request.getRemoteAddr();
    }

    public void destroy() {
        LogUtils.info((String)"Atlas Log filter destroyed", (Object[])new Object[0]);
    }
}

