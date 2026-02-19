/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.core.annotation.AnnotationAwareOrderComparator
 *  org.springframework.http.HttpStatus
 *  org.springframework.http.HttpStatusCode
 *  org.springframework.http.ResponseEntity
 *  org.springframework.web.bind.annotation.ControllerAdvice
 *  org.springframework.web.bind.annotation.ExceptionHandler
 *  org.springframework.web.bind.annotation.RestController
 *  org.springframework.web.context.request.WebRequest
 */
package com.kuma.boot.web.error;

import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.AnnotationAwareOrderComparator;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;

@ControllerAdvice(annotations={RestController.class})
public class ErrorHandlingControllerAdvice {
    private static final Logger LOGGER = LoggerFactory.getLogger(ErrorHandlingControllerAdvice.class);
    private final ErrorHandlingProperties properties;
    private final List<ApiExceptionHandler> handlers;
    private final FallbackApiExceptionHandler fallbackHandler;

    public ErrorHandlingControllerAdvice(ErrorHandlingProperties properties, List<ApiExceptionHandler> handlers, FallbackApiExceptionHandler fallbackHandler) {
        this.properties = properties;
        this.handlers = handlers;
        this.fallbackHandler = fallbackHandler;
        this.handlers.sort((Comparator<ApiExceptionHandler>)AnnotationAwareOrderComparator.INSTANCE);
        LOGGER.info("Error Handling Spring Boot Starter active with {} handlers", (Object)this.handlers.size());
        LOGGER.debug("Handlers: {}", this.handlers);
    }

    @ExceptionHandler
    public ResponseEntity<?> handleException(Throwable exception, WebRequest webRequest, Locale locale) {
        LOGGER.debug("webRequest: {}", (Object)webRequest);
        LOGGER.debug("locale: {}", (Object)locale);
        this.logException(exception);
        ApiErrorResponse errorResponse = null;
        for (ApiExceptionHandler handler : this.handlers) {
            if (!handler.canHandle(exception)) continue;
            errorResponse = handler.handle(exception);
            break;
        }
        if (errorResponse == null) {
            errorResponse = this.fallbackHandler.handle(exception);
        }
        if (!this.properties.getFullStacktraceHttpStatuses().isEmpty()) {
            this.logFullStacktraceIfNeeded(errorResponse.getHttpStatus(), exception);
        }
        return ResponseEntity.status((HttpStatusCode)errorResponse.getHttpStatus()).body((Object)errorResponse);
    }

    private void logException(Throwable exception) {
        if (this.properties.getFullStacktraceClasses().contains(exception.getClass())) {
            LOGGER.error(exception.getMessage(), exception);
        } else {
            switch (this.properties.getExceptionLogging()) {
                case WITH_STACKTRACE: {
                    LOGGER.error(exception.getMessage(), exception);
                    break;
                }
                case MESSAGE_ONLY: {
                    LOGGER.error(exception.getMessage());
                }
            }
        }
    }

    private void logFullStacktraceIfNeeded(HttpStatus httpStatus, Throwable exception) {
        String httpStatusValue = String.valueOf(httpStatus.value());
        if (this.properties.getFullStacktraceHttpStatuses().contains(httpStatusValue)) {
            LOGGER.error(exception.getMessage(), exception);
        } else if (this.properties.getFullStacktraceHttpStatuses().contains(httpStatusValue.replaceFirst("\\d$", "x"))) {
            LOGGER.error(exception.getMessage(), exception);
        } else if (this.properties.getFullStacktraceHttpStatuses().contains(httpStatusValue.replaceFirst("\\d\\d$", "xx"))) {
            LOGGER.error(exception.getMessage(), exception);
        }
    }
}

