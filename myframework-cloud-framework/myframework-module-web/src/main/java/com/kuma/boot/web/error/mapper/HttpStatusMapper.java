/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  org.springframework.core.annotation.AnnotationUtils
 *  org.springframework.http.HttpStatus
 *  org.springframework.web.bind.annotation.ResponseStatus
 */
package com.kuma.boot.web.error.mapper;

import com.kuma.boot.web.error.ErrorHandlingProperties;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

public class HttpStatusMapper {
    private final ErrorHandlingProperties properties;

    public HttpStatusMapper(ErrorHandlingProperties properties) {
        this.properties = properties;
    }

    public HttpStatus getHttpStatus(Throwable exception) {
        return this.getHttpStatus(exception, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    public HttpStatus getHttpStatus(Throwable exception, HttpStatus defaultHttpStatus) {
        HttpStatus status = this.getHttpStatusFromPropertiesOrAnnotation(exception.getClass());
        if (status != null) {
            return status;
        }
        return defaultHttpStatus;
    }

    private HttpStatus getHttpStatusFromPropertiesOrAnnotation(Class<?> exceptionClass) {
        if (exceptionClass == null) {
            return null;
        }
        String exceptionClassName = exceptionClass.getName();
        if (this.properties.getHttpStatuses().containsKey(exceptionClassName)) {
            return this.properties.getHttpStatuses().get(exceptionClassName);
        }
        ResponseStatus responseStatus = (ResponseStatus)AnnotationUtils.getAnnotation(exceptionClass, ResponseStatus.class);
        if (responseStatus != null) {
            return responseStatus.value();
        }
        if (this.properties.isSearchSuperClassHierarchy()) {
            return this.getHttpStatusFromPropertiesOrAnnotation(exceptionClass.getSuperclass());
        }
        return null;
    }
}

