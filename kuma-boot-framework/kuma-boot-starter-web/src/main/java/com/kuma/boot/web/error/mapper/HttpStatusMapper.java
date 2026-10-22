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

package com.kuma.boot.web.error.mapper;

import com.kuma.boot.common.enums.ResultEnum;
import com.kuma.boot.common.exception.BaseException;
import com.kuma.boot.web.error.ErrorHandlingProperties;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * This class contains the logic for getting the matching HTTP Status for the given {@link
 * Throwable}.
 */
public class HttpStatusMapper {

    // Business codes are opaque strings, never parsed as HTTP status numbers.
    private static final java.util.Map<String, HttpStatus> BUSINESS_STATUSES = java.util.Map.ofEntries(
            java.util.Map.entry(ResultEnum.BAD_REQUEST.codeDesc(), HttpStatus.BAD_REQUEST),
            java.util.Map.entry(ResultEnum.DATA_NOT_EXIST_ERROR.codeDesc(), HttpStatus.NOT_FOUND),
            java.util.Map.entry(ResultEnum.REQUEST_NOT_FOUND.codeDesc(), HttpStatus.NOT_FOUND),
            java.util.Map.entry(ResultEnum.REQUEST_OTHER_OPERATION.codeDesc(), HttpStatus.CONFLICT),
            java.util.Map.entry(ResultEnum.UNAUTHORIZED.codeDesc(), HttpStatus.UNAUTHORIZED),
            java.util.Map.entry(ResultEnum.FORBIDDEN.codeDesc(), HttpStatus.FORBIDDEN),
            java.util.Map.entry(ResultEnum.REQUEST_MORE_ERROR.codeDesc(), HttpStatus.TOO_MANY_REQUESTS));

    private final ErrorHandlingProperties properties;

    public HttpStatusMapper( ErrorHandlingProperties properties) {
        this.properties = properties;
    }

    public HttpStatus getHttpStatus(Throwable exception) {
        return getHttpStatus(exception, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    public HttpStatus getHttpStatus(Throwable exception, HttpStatus defaultHttpStatus) {
        HttpStatus status = getHttpStatusFromPropertiesOrAnnotation(exception.getClass());
        if (status != null) {
            return status;
        }

        if (exception instanceof BaseException business && business.getCode() != null) {
            return BUSINESS_STATUSES.getOrDefault(business.getCode().getCode(), defaultHttpStatus);
        }
        if (exception instanceof org.springframework.web.ErrorResponse error) {
            HttpStatus resolved = HttpStatus.resolve(error.getStatusCode().value());
            return resolved == null ? defaultHttpStatus : resolved;
        }
        if (exception instanceof org.springframework.security.access.AccessDeniedException) return HttpStatus.FORBIDDEN;
        if (exception instanceof org.springframework.security.core.AuthenticationException) return HttpStatus.UNAUTHORIZED;
        if (exception instanceof IllegalArgumentException || exception instanceof ArithmeticException
                || exception instanceof org.springframework.beans.TypeMismatchException
                || exception instanceof org.springframework.http.converter.HttpMessageNotReadableException
                || exception instanceof jakarta.validation.ConstraintViolationException) {
            return HttpStatus.BAD_REQUEST;
        }

        return defaultHttpStatus;
    }

    private HttpStatus getHttpStatusFromPropertiesOrAnnotation(Class<?> exceptionClass) {
        if (exceptionClass == null) {
            return null;
        }
        String exceptionClassName = exceptionClass.getName();
        if (properties.getHttpStatuses().containsKey(exceptionClassName)) {
            return properties.getHttpStatuses().get(exceptionClassName);
        }

        ResponseStatus responseStatus =
                AnnotationUtils.getAnnotation(exceptionClass, ResponseStatus.class);
        if (responseStatus != null) {
            return responseStatus.value();
        }

        if (properties.isSearchSuperClassHierarchy()) {
            return getHttpStatusFromPropertiesOrAnnotation(exceptionClass.getSuperclass());
        } else {
            return null;
        }
    }
}
