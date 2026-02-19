/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  com.kuma.boot.common.utils.lang.StringUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.core.annotation.AnnotationUtils
 *  org.springframework.http.HttpStatus
 */
package com.kuma.boot.web.error;

import com.kuma.boot.common.utils.lang.StringUtils;
import com.kuma.boot.web.error.mapper.ErrorCodeMapper;
import com.kuma.boot.web.error.mapper.ErrorMessageMapper;
import com.kuma.boot.web.error.mapper.HttpStatusMapper;
import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.http.HttpStatus;

public class DefaultFallbackApiExceptionHandler
implements FallbackApiExceptionHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultFallbackApiExceptionHandler.class);
    private final HttpStatusMapper httpStatusMapper;
    private final ErrorCodeMapper errorCodeMapper;
    private final ErrorMessageMapper errorMessageMapper;

    public DefaultFallbackApiExceptionHandler(HttpStatusMapper httpStatusMapper, ErrorCodeMapper errorCodeMapper, ErrorMessageMapper errorMessageMapper) {
        this.httpStatusMapper = httpStatusMapper;
        this.errorCodeMapper = errorCodeMapper;
        this.errorMessageMapper = errorMessageMapper;
    }

    @Override
    public ApiErrorResponse handle(Throwable exception) {
        HttpStatus statusCode = this.httpStatusMapper.getHttpStatus(exception);
        String errorCode = this.errorCodeMapper.getErrorCode(exception);
        String errorMessage = this.errorMessageMapper.getErrorMessage(exception);
        ApiErrorResponse response = new ApiErrorResponse(statusCode, errorCode, errorMessage);
        response.addErrorProperties(this.getMethodResponseErrorProperties(exception));
        response.addErrorProperties(this.getFieldResponseErrorProperties(exception));
        return response;
    }

    private Map<String, Object> getFieldResponseErrorProperties(Throwable exception) {
        HashMap<String, Object> result = new HashMap<String, Object>();
        for (Field field : exception.getClass().getDeclaredFields()) {
            if (!field.isAnnotationPresent(ResponseErrorProperty.class)) continue;
            try {
                field.setAccessible(true);
                Object value = field.get(exception);
                if (value == null && !field.getAnnotation(ResponseErrorProperty.class).includeIfNull()) continue;
                result.put(this.getPropertyName(field), value);
            }
            catch (IllegalAccessException e) {
                LOGGER.error(String.format("Unable to use field result of field %s.%s", exception.getClass().getName(), field.getName()));
            }
        }
        return result;
    }

    private Map<String, Object> getMethodResponseErrorProperties(Throwable exception) {
        HashMap<String, Object> result = new HashMap<String, Object>();
        Class<?> exceptionClass = exception.getClass();
        for (Method method : exceptionClass.getMethods()) {
            if (!method.isAnnotationPresent(ResponseErrorProperty.class) || method.getReturnType() == Void.TYPE || method.getParameterCount() != 0) continue;
            try {
                method.setAccessible(true);
                Object value = method.invoke((Object)exception, new Object[0]);
                if (value == null && !method.getAnnotation(ResponseErrorProperty.class).includeIfNull()) continue;
                result.put(this.getPropertyName(exceptionClass, method), value);
            }
            catch (IllegalAccessException | InvocationTargetException e) {
                LOGGER.error(String.format("Unable to use method result of method %s.%s", exceptionClass.getName(), method.getName()));
            }
        }
        return result;
    }

    private String getPropertyName(Field field) {
        ResponseErrorProperty annotation = (ResponseErrorProperty)AnnotationUtils.getAnnotation((AnnotatedElement)field, ResponseErrorProperty.class);
        assert (annotation != null);
        if (!StringUtils.isEmpty((String)annotation.value())) {
            return annotation.value();
        }
        return field.getName();
    }

    private String getPropertyName(Class<? extends Throwable> exceptionClass, Method method) {
        ResponseErrorProperty annotation = (ResponseErrorProperty)AnnotationUtils.getAnnotation((Method)method, ResponseErrorProperty.class);
        assert (annotation != null);
        if (!StringUtils.isEmpty((String)annotation.value())) {
            return annotation.value();
        }
        try {
            PropertyDescriptor[] propertyDescriptors;
            BeanInfo beanInfo = Introspector.getBeanInfo(exceptionClass);
            for (PropertyDescriptor propertyDescriptor : propertyDescriptors = beanInfo.getPropertyDescriptors()) {
                if (!propertyDescriptor.getReadMethod().equals(method)) continue;
                return propertyDescriptor.getName();
            }
        }
        catch (IntrospectionException introspectionException) {
            // empty catch block
        }
        return method.getName();
    }
}

