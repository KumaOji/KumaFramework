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

package com.kuma.boot.web.error;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.AnnotationAwareOrderComparator;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;

import java.util.List;
import java.util.Locale;

/**
 * ErrorHandlingControllerAdvice
 *
 * @author kuma
 * @version 2021.10
 * @since 2022-01-12 08:57:54
 */
@ControllerAdvice(annotations = RestController.class)
public class ErrorHandlingControllerAdvice {

    private static final Logger LOGGER =
            LoggerFactory.getLogger(ErrorHandlingControllerAdvice.class);

    private final ErrorHandlingProperties properties;
    private final List<com.kuma.boot.web.error.ApiExceptionHandler> handlers;
    private final FallbackApiExceptionHandler fallbackHandler;

    public ErrorHandlingControllerAdvice(
            ErrorHandlingProperties properties,
            List<com.kuma.boot.web.error.ApiExceptionHandler> handlers,
            FallbackApiExceptionHandler fallbackHandler) {
        this.properties = properties;
        this.handlers = handlers;
        this.fallbackHandler = fallbackHandler;
        this.handlers.sort(AnnotationAwareOrderComparator.INSTANCE);

        LOGGER.info(
                "Error Handling Spring Boot Starter active with {} handlers", this.handlers.size());
        LOGGER.debug("Handlers: {}", this.handlers);
    }

    @ExceptionHandler
    public ResponseEntity<?> handleException(
            Throwable exception, WebRequest webRequest, Locale locale) {
        LOGGER.debug("webRequest: {}", webRequest);
        LOGGER.debug("locale: {}", locale);
        logException(exception);

        com.kuma.boot.web.error.ApiErrorResponse errorResponse = null;
        for (com.kuma.boot.web.error.ApiExceptionHandler handler : handlers) {
            if (handler.canHandle(exception)) {
                errorResponse = handler.handle(exception);
                break;
            }
        }

        if (errorResponse == null) {
            errorResponse = fallbackHandler.handle(exception);
        }

        if (!properties.getFullStacktraceHttpStatuses().isEmpty()) {
            logFullStacktraceIfNeeded(errorResponse.getHttpStatus(), exception);
        }

        return ResponseEntity.status(errorResponse.getHttpStatus()).body(errorResponse);
    }

    private void logException(Throwable exception) {
        if (properties.getFullStacktraceClasses().contains(exception.getClass())) {
            LOGGER.error(exception.getMessage(), exception);
        } else {
            switch (properties.getExceptionLogging()) {
                case WITH_STACKTRACE:
                    LOGGER.error(exception.getMessage(), exception);
                    break;
                case MESSAGE_ONLY:
                    LOGGER.error(exception.getMessage());
                    break;
            }
        }
    }

    private void logFullStacktraceIfNeeded(HttpStatus httpStatus, Throwable exception) {
        String httpStatusValue = String.valueOf(httpStatus.value());
        if (properties.getFullStacktraceHttpStatuses().contains(httpStatusValue)) {
            LOGGER.error(exception.getMessage(), exception);
        } else if (properties
                .getFullStacktraceHttpStatuses()
                .contains(httpStatusValue.replaceFirst("\\d$", "x"))) {
            LOGGER.error(exception.getMessage(), exception);
        } else if (properties
                .getFullStacktraceHttpStatuses()
                .contains(httpStatusValue.replaceFirst("\\d\\d$", "xx"))) {
            LOGGER.error(exception.getMessage(), exception);
        }
    }
}
