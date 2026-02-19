/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  jakarta.annotation.Resource
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.core.MethodParameter
 *  org.springframework.core.annotation.Order
 *  org.springframework.http.MediaType
 *  org.springframework.http.converter.HttpMessageConverter
 *  org.springframework.http.converter.json.MappingJackson2HttpMessageConverter
 *  org.springframework.http.server.ServerHttpRequest
 *  org.springframework.http.server.ServerHttpResponse
 *  org.springframework.util.AntPathMatcher
 *  org.springframework.util.CollectionUtils
 *  org.springframework.web.bind.annotation.ControllerAdvice
 *  org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice
 */
package com.kuma.boot.web.gracefulresponse.advice;

import com.kuma.boot.web.gracefulresponse.GracefulResponseProperties;
import com.kuma.boot.web.gracefulresponse.api.ExcludeFromGracefulResponse;
import com.kuma.boot.web.gracefulresponse.api.ResponseFactory;
import com.kuma.boot.web.gracefulresponse.data.Response;
import jakarta.annotation.Resource;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.MethodParameter;
import org.springframework.core.annotation.Order;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

@ControllerAdvice
@Order(value=1000)
public class NotVoidResponseBodyAdvice
implements ResponseBodyAdvice<Object> {
    private final Logger logger = LoggerFactory.getLogger(NotVoidResponseBodyAdvice.class);
    @Resource
    private ResponseFactory responseFactory;
    @Resource
    private GracefulResponseProperties properties;
    private static final AntPathMatcher ANT_PATH_MATCHER = new AntPathMatcher();

    public boolean supports(MethodParameter methodParameter, Class<? extends HttpMessageConverter<?>> clazz) {
        Method method = methodParameter.getMethod();
        if (Objects.isNull(method) || method.getReturnType().equals(Void.TYPE) || !MappingJackson2HttpMessageConverter.class.isAssignableFrom(clazz)) {
            this.logger.debug("Graceful Response:method\u4e3a\u7a7a\u3001\u8fd4\u56de\u503c\u4e3avoid\u3001\u975eJSON\uff0c\u8df3\u8fc7");
            return false;
        }
        if (method.isAnnotationPresent(ExcludeFromGracefulResponse.class)) {
            if (this.logger.isDebugEnabled()) {
                this.logger.debug("Graceful Response:\u65b9\u6cd5\u88ab@ExcludeFromGracefulResponse\u6ce8\u89e3\u4fee\u9970\uff0c\u8df3\u8fc7:methodName={}", (Object)method.getName());
            }
            return false;
        }
        List<String> excludePackages = this.properties.getExcludePackages();
        if (!CollectionUtils.isEmpty(excludePackages)) {
            String packageName = method.getDeclaringClass().getPackage().getName();
            if (excludePackages.stream().anyMatch(item -> ANT_PATH_MATCHER.match(item, packageName))) {
                this.logger.debug("Graceful Response:\u5339\u914d\u5230excludePackages\u4f8b\u5916\u914d\u7f6e\uff0c\u8df3\u8fc7:packageName={},", (Object)packageName);
                return false;
            }
        }
        this.logger.debug("Graceful Response:\u975e\u7a7a\u8fd4\u56de\u503c\uff0c\u9700\u8981\u8fdb\u884c\u5c01\u88c5");
        return true;
    }

    public Object beforeBodyWrite(Object body, MethodParameter methodParameter, MediaType mediaType, Class<? extends HttpMessageConverter<?>> clazz, ServerHttpRequest serverHttpRequest, ServerHttpResponse serverHttpResponse) {
        if (body == null) {
            return this.responseFactory.newSuccessInstance();
        }
        if (body instanceof Response) {
            return body;
        }
        return this.responseFactory.newSuccessInstance(body);
    }
}

