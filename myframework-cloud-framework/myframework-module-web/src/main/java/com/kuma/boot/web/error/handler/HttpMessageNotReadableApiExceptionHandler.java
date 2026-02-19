/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  org.springframework.http.HttpStatus
 *  org.springframework.http.converter.HttpMessageNotReadableException
 */
package com.kuma.boot.web.error.handler;

import com.kuma.boot.web.error.ApiErrorResponse;
import com.kuma.boot.web.error.ErrorHandlingProperties;
import com.kuma.boot.web.error.mapper.ErrorCodeMapper;
import com.kuma.boot.web.error.mapper.ErrorMessageMapper;
import com.kuma.boot.web.error.mapper.HttpStatusMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;

public class HttpMessageNotReadableApiExceptionHandler
extends AbstractApiExceptionHandler {
    public HttpMessageNotReadableApiExceptionHandler(ErrorHandlingProperties properties, HttpStatusMapper httpStatusMapper, ErrorCodeMapper errorCodeMapper, ErrorMessageMapper errorMessageMapper) {
        super(httpStatusMapper, errorCodeMapper, errorMessageMapper);
    }

    @Override
    public boolean canHandle(Throwable exception) {
        return exception instanceof HttpMessageNotReadableException;
    }

    @Override
    public ApiErrorResponse handle(Throwable exception) {
        return new ApiErrorResponse(this.getHttpStatus(exception, HttpStatus.BAD_REQUEST), this.getErrorCode(exception), this.getErrorMessage(exception));
    }
}

