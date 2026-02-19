/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  com.kuma.boot.common.utils.reflect.AnnotationUtils
 *  org.jspecify.annotations.NonNull
 *  org.reactivestreams.Publisher
 *  org.springframework.core.MethodParameter
 *  org.springframework.core.Ordered
 *  org.springframework.core.ReactiveAdapter
 *  org.springframework.core.ReactiveAdapterRegistry
 *  org.springframework.core.ResolvableType
 *  org.springframework.http.ContentDisposition
 *  org.springframework.http.HttpHeaders
 *  org.springframework.http.MediaType
 *  org.springframework.http.MediaTypeFactory
 *  org.springframework.http.ReactiveHttpOutputMessage
 *  org.springframework.http.server.reactive.ServerHttpResponse
 *  org.springframework.web.reactive.HandlerResult
 *  org.springframework.web.reactive.HandlerResultHandler
 *  org.springframework.web.server.ServerWebExchange
 *  reactor.core.publisher.Mono
 */
package com.kuma.boot.office.fastexcel.resolver;

import com.kuma.boot.common.utils.reflect.AnnotationUtils;
import com.kuma.boot.office.fastexcel.FastExcelSupport;
import com.kuma.boot.office.fastexcel.annotation.ResponseExcel;
import com.kuma.boot.office.fastexcel.converter.ExcelHttpMessageWriter;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import org.jspecify.annotations.NonNull;
import org.reactivestreams.Publisher;
import org.springframework.core.MethodParameter;
import org.springframework.core.Ordered;
import org.springframework.core.ReactiveAdapter;
import org.springframework.core.ReactiveAdapterRegistry;
import org.springframework.core.ResolvableType;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.MediaTypeFactory;
import org.springframework.http.ReactiveHttpOutputMessage;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.reactive.HandlerResult;
import org.springframework.web.reactive.HandlerResultHandler;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

public class ReactiveExcelMethodReturnValueHandler
implements HandlerResultHandler,
Ordered {
    public static final MediaType EXCEL_MEDIA_TYPE = new MediaType("application", "vnd.ms-excel");
    private final ExcelHttpMessageWriter writer = new ExcelHttpMessageWriter();
    private final ReactiveAdapterRegistry adapterRegistry = ReactiveAdapterRegistry.getSharedInstance();

    public boolean supports(@NonNull HandlerResult result) {
        return AnnotationUtils.hasAnnotationElement((MethodParameter)result.getReturnTypeSource(), ResponseExcel.class);
    }

    public @NonNull Mono<Void> handleResult(@NonNull ServerWebExchange exchange, HandlerResult result) {
        Object returnValue = result.getReturnValue();
        if (returnValue != null) {
            ResponseExcel responseExcel = (ResponseExcel)AnnotationUtils.getAnnotationElement((MethodParameter)result.getReturnTypeSource(), ResponseExcel.class);
            ServerHttpResponse response = exchange.getResponse();
            this.setResponse(responseExcel, response);
            ResolvableType returnType = result.getReturnType();
            ReactiveAdapter adapter = this.adapterRegistry.getAdapter(returnType.resolve(), returnValue);
            if (this.writer.canWrite(returnType, MediaType.ALL)) {
                Publisher inputStream = adapter != null ? (Publisher)returnValue : Mono.just((Object)returnValue);
                HashMap<String, Object> hints = new HashMap<String, Object>();
                hints.put("responseExcel", responseExcel);
                return this.writer.write(inputStream, returnType, EXCEL_MEDIA_TYPE, (ReactiveHttpOutputMessage)exchange.getResponse(), hints);
            }
        }
        return Mono.empty();
    }

    private void setResponse(ResponseExcel responseExcel, ServerHttpResponse response) {
        String fileName = FastExcelSupport.fileName(responseExcel);
        MediaType mediaType = MediaTypeFactory.getMediaType((String)fileName).orElse(EXCEL_MEDIA_TYPE);
        HttpHeaders headers = response.getHeaders();
        headers.setContentType(mediaType);
        headers.setAcceptCharset(List.of(StandardCharsets.UTF_8));
        ContentDisposition contentDisposition = ContentDisposition.parse((String)("attachment;filename=" + fileName));
        headers.setContentDisposition(contentDisposition);
    }

    public int getOrder() {
        return Integer.MIN_VALUE;
    }
}

