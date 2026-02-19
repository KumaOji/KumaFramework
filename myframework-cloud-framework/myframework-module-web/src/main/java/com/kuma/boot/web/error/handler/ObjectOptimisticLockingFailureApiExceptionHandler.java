/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  org.springframework.http.HttpStatus
 */
package com.kuma.boot.web.error.handler;

import com.kuma.boot.web.error.ApiErrorResponse;
import com.kuma.boot.web.error.ErrorHandlingProperties;
import com.kuma.boot.web.error.mapper.ErrorCodeMapper;
import com.kuma.boot.web.error.mapper.ErrorMessageMapper;
import com.kuma.boot.web.error.mapper.HttpStatusMapper;
import org.springframework.http.HttpStatus;

public class ObjectOptimisticLockingFailureApiExceptionHandler
extends AbstractApiExceptionHandler {
    public ObjectOptimisticLockingFailureApiExceptionHandler(ErrorHandlingProperties properties, HttpStatusMapper httpStatusMapper, ErrorCodeMapper errorCodeMapper, ErrorMessageMapper errorMessageMapper) {
        super(httpStatusMapper, errorCodeMapper, errorMessageMapper);
    }

    @Override
    public boolean canHandle(Throwable exception) {
        return false;
    }

    @Override
    public ApiErrorResponse handle(Throwable exception) {
        ApiErrorResponse response = new ApiErrorResponse(this.getHttpStatus(exception, HttpStatus.CONFLICT), this.getErrorCode(exception), this.getErrorMessage(exception));
        return response;
    }
}

