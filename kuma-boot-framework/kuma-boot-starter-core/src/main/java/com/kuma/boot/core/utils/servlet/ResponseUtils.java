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

package com.kuma.boot.core.utils.servlet;

import com.kuma.boot.common.constant.CommonConstants;
import com.kuma.boot.common.enums.ResultEnum;
import com.kuma.boot.common.holder.TenantContextHolder;
import com.kuma.boot.common.holder.TraceContextHolder;
import com.kuma.boot.common.holder.VersionContextHolder;
import com.kuma.boot.common.model.result.Result;
import com.kuma.boot.common.utils.json.JacksonUtils;
import com.kuma.boot.common.utils.log.LogUtils;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Writer;
import java.nio.charset.Charset;
import java.util.Collection;
import java.util.List;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * ResponseUtil
 *
 * @author kuma
 * @version 2023.01
 * @since 2023-01-03 11:33:15
 */
public class ResponseUtils {

    private ResponseUtils() {}

    /**
     * 成功返回数据
     * @param response response
     * @param data 数据对象
     * @since 2023-01-03 11:33:15
     */
    public static void success(HttpServletResponse response, Object data) throws IOException {
        Result<?> result = Result.success(data);
        writeResponse(response, result);
    }

    /**
     * 失败返回数据
     * @param response response
     * @param data 数据对象
     * @since 2023-01-03 11:33:15
     */
    public static void fail(HttpServletResponse response, Object data) {
        Result<?> result = Result.fail(data.toString());

        try {
            writeResponse(response, result);
        } catch (IOException e) {
            LogUtils.error(e);
            LogUtils.error(e);
        }
    }

    /**
     * 成功返回数据
     * @param response response
     * @param result 数据对象
     * @since 2023-01-03 11:33:15
     */
    public static void result(HttpServletResponse response, Result<?> result) throws IOException {
        writeResponse(response, result);
    }

    /**
     * 失败返回数据
     * @param response response
     * @param resultEnum 数据对象
     * @since 2023-01-03 11:33:15
     */
    public static void fail(HttpServletResponse response, ResultEnum resultEnum)
            throws IOException {
        Result<String> result = Result.fail(resultEnum);
        writeResponse(response, result);
    }

    /**
     * 通过流返回数据
     * @param response response
     * @param result 数据
     * @since 2023-01-03 11:33:15
     */
    private static void writeResponse(HttpServletResponse response, Result<?> result)
            throws IOException {
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(HttpStatus.OK.value());
        response.setCharacterEncoding("UTF-8");
        response.setHeader("Content-Type", "application/json;charset=UTF-8");
        try (Writer writer = response.getWriter()) {
            writer.write(JacksonUtils.toJSONString(result));
            writer.flush();
        }
    }

    /**
     * webflux成功返回数据
     * @param exchange exchange
     * @param data 数据
     * @return {@link Mono }<{@link Void }>
     * @since 2023-01-03 11:33:15
     */
    public static Mono<Void> success(ServerWebExchange exchange, Object data) {
        Result<Object> result = Result.success(data);
        return writeResponse(exchange, result);
    }

    /**
     * webflux失败返回数据
     * @param exchange exchange
     * @param data 数据
     * @return {@link Mono }<{@link Void }>
     * @since 2023-01-03 11:33:15
     */
    public static Mono<Void> fail(ServerWebExchange exchange, Object data) {
        Result<Object> result = Result.fail(data.toString());
        return writeResponse(exchange, result);
    }

    /**
     * 失败返回数据
     * @param exchange exchange
     * @param result 数据
     * @return {@link Mono }<{@link Void }>
     * @since 2023-01-03 11:33:15
     */
    public static Mono<Void> result(ServerWebExchange exchange, Result<?> result) {
        return writeResponse(exchange, result);
    }

    /**
     * 失败返回数据
     * @param exchange exchange
     * @param resultEnum 状态码
     * @return {@link Mono }<{@link Void }>
     * @since 2023-01-03 11:33:15
     */
    public static Mono<Void> fail(ServerWebExchange exchange, ResultEnum resultEnum) {
        Result<String> result = Result.fail(resultEnum);
        return writeResponse(exchange, result);
    }

    /**
     * 通过流返回数据
     * @param exchange exchange
     * @param result 数据
     * @return {@link Mono }
     * @since 2021-09-02 15:01:48
     */

    /**
     * writeResponse
     * @param exchange exchange
     * @param result result
     * @return {@link Mono }<{@link Void }>
     * @since 2023-01-03 11:33:15
     */
    public static Mono<Void> writeResponse(ServerWebExchange exchange, Result<?> result) {
        ServerHttpResponse response = exchange.getResponse();
        response.getHeaders().setAccessControlAllowCredentials(true);
        response.getHeaders().setAccessControlAllowOrigin("*");
        response.setStatusCode(HttpStatus.OK);
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
        DataBufferFactory dataBufferFactory = response.bufferFactory();
        DataBuffer buffer =
                dataBufferFactory.wrap(
                        JacksonUtils.toJSONString(result).getBytes(Charset.defaultCharset()));
        return response.writeWith(Mono.just(buffer))
                .doOnSuccess((error) -> DataBufferUtils.release(buffer));
    }

    /**
     * 编写html响应文本
     * @param exchange 交换
     * @param httpStatus http状态
     * @param result 结果
     * @return {@link Mono }<{@link Void }>
     * @since 2023-01-03 11:33:15
     */
    public static Mono<Void> writeResponseTextHtml(
            ServerWebExchange exchange, HttpStatus httpStatus, String result) {
        ServerHttpResponse response = exchange.getResponse();
        response.getHeaders().setAccessControlAllowCredentials(true);
        response.getHeaders().setAccessControlAllowOrigin("*");
        response.setStatusCode(httpStatus);
        response.getHeaders().setContentType(MediaType.TEXT_HTML);

        DataBufferFactory dataBufferFactory = response.bufferFactory();
        DataBuffer buffer = dataBufferFactory.wrap(result.getBytes(Charset.defaultCharset()));

        return response.writeWith(Mono.just(buffer))
                .doOnSuccess((error) -> DataBufferUtils.release(buffer));
    }

    public static void addResponseHeader(
            org.springframework.http.server.ServerHttpResponse serverHttpResponse) {
        HttpHeaders headers = serverHttpResponse.getHeaders();
        addHeader(headers, CommonConstants.KMC_TENANT_ID, TenantContextHolder.getTenant());
        addHeader(headers, CommonConstants.KMC_TRACE_ID, TraceContextHolder.getTraceId());
        addHeader(headers, CommonConstants.OTLP_TRACE_ID, TraceUtils.getOtlpTraceId());
        addHeader(headers, CommonConstants.OTLP_SPANE_ID, TraceUtils.getOtlpSpanId());
        addHeader(headers, CommonConstants.KMC_REQUEST_VERSION, VersionContextHolder.getVersion());
    }

    public static void addResponseHeader(
            HttpServletResponse httpServletResponse, String key, String value) {
        if (StrUtil.isBlank(value)) {
            return;
        }

        Collection<String> headerNames = httpServletResponse.getHeaderNames();
        if (CollUtil.isEmpty(headerNames)) {
            httpServletResponse.addHeader(key, value);
            return;
        }
        if (!headerNames.contains(key)) {
            httpServletResponse.addHeader(key, value);
            return;
        }

        Collection<String> headersValues = httpServletResponse.getHeaders(key);
        if (CollUtil.isEmpty(headersValues)) {
            httpServletResponse.addHeader(key, value);
            return;
        }
        if (!headersValues.contains(value)) {
            httpServletResponse.addHeader(key, value);
        }
    }

    public static void addHeader(HttpHeaders headers, String key, String value) {
        if (StrUtil.isBlank(value)) {
            return;
        }
        if ("N/A".equals(value)) {
            return;
        }

        //if (!headers.containsKey(key)) {
        //    headers.add(key, value);
        //    return;
        //}
        List<String> headerValues = headers.get(key);
        if (CollUtil.isEmpty(headerValues)) {
            headers.add(key, value);
            return;
        }
        if (!headerValues.contains(value)) {
            headers.add(key, value);
        }
    }
}
