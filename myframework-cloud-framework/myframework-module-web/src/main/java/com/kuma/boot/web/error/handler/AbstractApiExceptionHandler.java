/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  org.springframework.http.HttpStatus
 */
package com.kuma.boot.web.error.handler;

import com.kuma.boot.web.error.ApiExceptionHandler;
import com.kuma.boot.web.error.mapper.ErrorCodeMapper;
import com.kuma.boot.web.error.mapper.ErrorMessageMapper;
import com.kuma.boot.web.error.mapper.HttpStatusMapper;
import org.springframework.http.HttpStatus;

public abstract class AbstractApiExceptionHandler
implements ApiExceptionHandler {
    protected final HttpStatusMapper httpStatusMapper;
    protected final ErrorCodeMapper errorCodeMapper;
    protected final ErrorMessageMapper errorMessageMapper;

    public AbstractApiExceptionHandler(HttpStatusMapper httpStatusMapper, ErrorCodeMapper errorCodeMapper, ErrorMessageMapper errorMessageMapper) {
        this.httpStatusMapper = httpStatusMapper;
        this.errorCodeMapper = errorCodeMapper;
        this.errorMessageMapper = errorMessageMapper;
    }

    protected HttpStatus getHttpStatus(Throwable exception, HttpStatus defaultHttpStatus) {
        return this.httpStatusMapper.getHttpStatus(exception, defaultHttpStatus);
    }

    protected String getErrorCode(Throwable exception) {
        return this.errorCodeMapper.getErrorCode(exception);
    }

    protected String getErrorMessage(Throwable exception) {
        return this.errorMessageMapper.getErrorMessage(exception);
    }
}

