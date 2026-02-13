/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cn.hutool.core.collection.CollUtil
 *  cn.hutool.core.util.StrUtil
 *  jakarta.servlet.http.HttpServletResponse
 *  org.reactivestreams.Publisher
 *  org.springframework.core.io.buffer.DataBuffer
 *  org.springframework.core.io.buffer.DataBufferFactory
 *  org.springframework.core.io.buffer.DataBufferUtils
 *  org.springframework.http.HttpHeaders
 *  org.springframework.http.HttpStatus
 *  org.springframework.http.HttpStatusCode
 *  org.springframework.http.MediaType
 *  org.springframework.http.server.ServerHttpResponse
 *  org.springframework.http.server.reactive.ServerHttpResponse
 *  org.springframework.web.server.ServerWebExchange
 *  reactor.core.publisher.Mono
 */
package com.kuma.boot.common.utils.servlet;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.kuma.boot.common.enums.ResultEnum;
import com.kuma.boot.common.holder.TenantContextHolder;
import com.kuma.boot.common.holder.TraceContextHolder;
import com.kuma.boot.common.holder.VersionContextHolder;
import com.kuma.boot.common.model.result.Result;
import com.kuma.boot.common.utils.json.JacksonUtils;
import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.boot.common.utils.servlet.TraceUtils;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.nio.charset.Charset;
import java.util.Collection;
import java.util.List;
import org.reactivestreams.Publisher;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

public class ResponseUtils {
    private ResponseUtils() {
    }

    public static void success(HttpServletResponse response, Object data) throws IOException {
        Result<Object> result = Result.success(data);
        ResponseUtils.writeResponse(response, result);
    }

    public static void fail(HttpServletResponse response, Object data) {
        Result<String> result = Result.fail(data.toString());
        try {
            ResponseUtils.writeResponse(response, result);
        }
        catch (IOException e) {
            LogUtils.error(e);
            LogUtils.error(e);
        }
    }

    public static void result(HttpServletResponse response, Result<?> result) throws IOException {
        ResponseUtils.writeResponse(response, result);
    }

    public static void fail(HttpServletResponse response, ResultEnum resultEnum) throws IOException {
        Result<ResultEnum> result = Result.fail(resultEnum);
        ResponseUtils.writeResponse(response, result);
    }

    private static void writeResponse(HttpServletResponse response, Result<?> result) throws IOException {
        response.setContentType("application/json");
        response.setStatus(HttpStatus.OK.value());
        response.setCharacterEncoding("UTF-8");
        response.setHeader("Content-Type", "application/json;charset=UTF-8");
        try (PrintWriter writer = response.getWriter();){
            ((Writer)writer).write(JacksonUtils.toJSONString(result));
            ((Writer)writer).flush();
        }
    }

    public static Mono<Void> success(ServerWebExchange exchange, Object data) {
        Result<Object> result = Result.success(data);
        return ResponseUtils.writeResponse(exchange, result);
    }

    public static Mono<Void> fail(ServerWebExchange exchange, Object data) {
        Result<String> result = Result.fail(data.toString());
        return ResponseUtils.writeResponse(exchange, result);
    }

    public static Mono<Void> result(ServerWebExchange exchange, Result<?> result) {
        return ResponseUtils.writeResponse(exchange, result);
    }

    public static Mono<Void> fail(ServerWebExchange exchange, ResultEnum resultEnum) {
        Result<ResultEnum> result = Result.fail(resultEnum);
        return ResponseUtils.writeResponse(exchange, result);
    }

    public static Mono<Void> writeResponse(ServerWebExchange exchange, Result<?> result) {
        org.springframework.http.server.reactive.ServerHttpResponse response = exchange.getResponse();
        response.getHeaders().setAccessControlAllowCredentials(true);
        response.getHeaders().setAccessControlAllowOrigin("*");
        response.setStatusCode((HttpStatusCode)HttpStatus.OK);
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
        DataBufferFactory dataBufferFactory = response.bufferFactory();
        DataBuffer buffer = dataBufferFactory.wrap(JacksonUtils.toJSONString(result).getBytes(Charset.defaultCharset()));
        return response.writeWith((Publisher)Mono.just((Object)buffer)).doOnSuccess(error -> DataBufferUtils.release((DataBuffer)buffer));
    }

    public static Mono<Void> writeResponseTextHtml(ServerWebExchange exchange, HttpStatus httpStatus, String result) {
        org.springframework.http.server.reactive.ServerHttpResponse response = exchange.getResponse();
        response.getHeaders().setAccessControlAllowCredentials(true);
        response.getHeaders().setAccessControlAllowOrigin("*");
        response.setStatusCode((HttpStatusCode)httpStatus);
        response.getHeaders().setContentType(MediaType.TEXT_HTML);
        DataBufferFactory dataBufferFactory = response.bufferFactory();
        DataBuffer buffer = dataBufferFactory.wrap(result.getBytes(Charset.defaultCharset()));
        return response.writeWith((Publisher)Mono.just((Object)buffer)).doOnSuccess(error -> DataBufferUtils.release((DataBuffer)buffer));
    }

    public static void addResponseHeader(ServerHttpResponse serverHttpResponse) {
        HttpHeaders headers = serverHttpResponse.getHeaders();
        ResponseUtils.addHeader(headers, "kmc-tenant-id", TenantContextHolder.getTenant());
        ResponseUtils.addHeader(headers, "kmc-trace-id", TraceContextHolder.getTraceId());
        ResponseUtils.addHeader(headers, "X-B3-TraceId", TraceUtils.getOtlpTraceId());
        ResponseUtils.addHeader(headers, "X-B3-SpanId", TraceUtils.getOtlpSpanId());
        ResponseUtils.addHeader(headers, "kmc-request-version", VersionContextHolder.getVersion());
    }

    public static void addResponseHeader(HttpServletResponse httpServletResponse, String key, String value) {
        if (StrUtil.isBlank((CharSequence)value)) {
            return;
        }
        Collection headerNames = httpServletResponse.getHeaderNames();
        if (CollUtil.isEmpty((Collection)headerNames)) {
            httpServletResponse.addHeader(key, value);
            return;
        }
        if (!headerNames.contains(key)) {
            httpServletResponse.addHeader(key, value);
            return;
        }
        Collection headersValues = httpServletResponse.getHeaders(key);
        if (CollUtil.isEmpty((Collection)headersValues)) {
            httpServletResponse.addHeader(key, value);
            return;
        }
        if (!headersValues.contains(value)) {
            httpServletResponse.addHeader(key, value);
        }
    }

    public static void addHeader(HttpHeaders headers, String key, String value) {
        if (StrUtil.isBlank((CharSequence)value)) {
            return;
        }
        if ("N/A".equals(value)) {
            return;
        }
        List headerValues = headers.get(key);
        if (CollUtil.isEmpty((Collection)headerValues)) {
            headers.add(key, value);
            return;
        }
        if (!headerValues.contains(value)) {
            headers.add(key, value);
        }
    }
}

