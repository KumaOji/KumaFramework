/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  org.springframework.beans.TypeMismatchException
 *  org.springframework.http.HttpStatus
 *  org.springframework.web.method.annotation.MethodArgumentTypeMismatchException
 */
package com.kuma.boot.web.error.handler;

import com.kuma.boot.web.error.ApiErrorResponse;
import com.kuma.boot.web.error.ErrorHandlingProperties;
import com.kuma.boot.web.error.mapper.ErrorCodeMapper;
import com.kuma.boot.web.error.mapper.ErrorMessageMapper;
import com.kuma.boot.web.error.mapper.HttpStatusMapper;
import org.springframework.beans.TypeMismatchException;
import org.springframework.http.HttpStatus;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

public class TypeMismatchApiExceptionHandler
extends AbstractApiExceptionHandler {
    public TypeMismatchApiExceptionHandler(ErrorHandlingProperties properties, HttpStatusMapper httpStatusMapper, ErrorCodeMapper errorCodeMapper, ErrorMessageMapper errorMessageMapper) {
        super(httpStatusMapper, errorCodeMapper, errorMessageMapper);
    }

    @Override
    public boolean canHandle(Throwable exception) {
        return exception instanceof TypeMismatchException;
    }

    @Override
    public ApiErrorResponse handle(Throwable exception) {
        ApiErrorResponse response = new ApiErrorResponse(this.getHttpStatus(exception, HttpStatus.BAD_REQUEST), this.getErrorCode(exception), this.getErrorMessage(exception));
        TypeMismatchException ex = (TypeMismatchException)exception;
        response.addErrorProperty("property", this.getPropertyName(ex));
        response.addErrorProperty("rejectedValue", ex.getValue());
        response.addErrorProperty("expectedType", ex.getRequiredType() != null ? ex.getRequiredType().getName() : null);
        return response;
    }

    private String getPropertyName(TypeMismatchException exception) {
        if (exception instanceof MethodArgumentTypeMismatchException) {
            return ((MethodArgumentTypeMismatchException)exception).getName();
        }
        return exception.getPropertyName();
    }
}

