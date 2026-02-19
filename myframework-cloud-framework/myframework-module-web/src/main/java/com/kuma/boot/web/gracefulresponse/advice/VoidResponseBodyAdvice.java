/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  jakarta.annotation.Resource
 *  org.springframework.core.MethodParameter
 *  org.springframework.core.annotation.Order
 *  org.springframework.http.MediaType
 *  org.springframework.http.converter.HttpMessageConverter
 *  org.springframework.http.converter.json.MappingJackson2HttpMessageConverter
 *  org.springframework.http.server.ServerHttpRequest
 *  org.springframework.http.server.ServerHttpResponse
 *  org.springframework.web.bind.annotation.ControllerAdvice
 *  org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice
 */
package com.kuma.boot.web.gracefulresponse.advice;

import com.kuma.boot.web.gracefulresponse.api.ResponseFactory;
import jakarta.annotation.Resource;
import java.util.Objects;
import org.springframework.core.MethodParameter;
import org.springframework.core.annotation.Order;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

@ControllerAdvice
@Order(value=1000)
public class VoidResponseBodyAdvice
implements ResponseBodyAdvice<Object> {
    @Resource
    private ResponseFactory responseFactory;

    public boolean supports(MethodParameter methodParameter, Class<? extends HttpMessageConverter<?>> clazz) {
        return Objects.requireNonNull(methodParameter.getMethod()).getReturnType().equals(Void.TYPE) && MappingJackson2HttpMessageConverter.class.isAssignableFrom(clazz);
    }

    public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType, Class<? extends HttpMessageConverter<?>> selectedConverterType, ServerHttpRequest request, ServerHttpResponse response) {
        return this.responseFactory.newSuccessInstance();
    }
}

