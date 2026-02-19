/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  org.springframework.http.HttpStatus
 *  org.springframework.validation.BindingResult
 *  org.springframework.validation.FieldError
 *  org.springframework.web.bind.MethodArgumentNotValidException
 */
package com.kuma.boot.web.error.handler;

import com.kuma.boot.web.error.ApiErrorResponse;
import com.kuma.boot.web.error.ApiFieldError;
import com.kuma.boot.web.error.ApiGlobalError;
import com.kuma.boot.web.error.ErrorHandlingProperties;
import com.kuma.boot.web.error.mapper.ErrorCodeMapper;
import com.kuma.boot.web.error.mapper.ErrorMessageMapper;
import com.kuma.boot.web.error.mapper.HttpStatusMapper;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

public class MethodArgumentNotValidApiExceptionHandler
extends AbstractApiExceptionHandler {
    public MethodArgumentNotValidApiExceptionHandler(ErrorHandlingProperties properties, HttpStatusMapper httpStatusMapper, ErrorCodeMapper errorCodeMapper, ErrorMessageMapper errorMessageMapper) {
        super(httpStatusMapper, errorCodeMapper, errorMessageMapper);
    }

    @Override
    public boolean canHandle(Throwable exception) {
        return exception instanceof MethodArgumentNotValidException;
    }

    @Override
    public ApiErrorResponse handle(Throwable exception) {
        MethodArgumentNotValidException ex = (MethodArgumentNotValidException)exception;
        ApiErrorResponse response = new ApiErrorResponse(this.getHttpStatus(exception, HttpStatus.BAD_REQUEST), this.getErrorCode(exception), this.getMessage(ex));
        BindingResult bindingResult = ex.getBindingResult();
        if (bindingResult.hasFieldErrors()) {
            bindingResult.getFieldErrors().stream().map(fieldError -> new ApiFieldError(this.getCode((FieldError)fieldError), fieldError.getField(), this.getMessage((FieldError)fieldError), fieldError.getRejectedValue())).forEach(response::addFieldError);
        }
        if (bindingResult.hasGlobalErrors()) {
            bindingResult.getGlobalErrors().stream().map(globalError -> new ApiGlobalError(this.errorCodeMapper.getErrorCode(globalError.getCode()), this.errorMessageMapper.getErrorMessage(globalError.getCode(), globalError.getDefaultMessage()))).forEach(response::addGlobalError);
        }
        return response;
    }

    private String getCode(FieldError fieldError) {
        String code = fieldError.getCode();
        String fieldSpecificCode = fieldError.getField() + "." + code;
        return this.errorCodeMapper.getErrorCode(fieldSpecificCode, code);
    }

    private String getMessage(FieldError fieldError) {
        String code = fieldError.getCode();
        String fieldSpecificCode = fieldError.getField() + "." + code;
        return this.errorMessageMapper.getErrorMessage(fieldSpecificCode, code, fieldError.getDefaultMessage());
    }

    private String getMessage(MethodArgumentNotValidException exception) {
        return "Validation failed for object='" + exception.getBindingResult().getObjectName() + "'. Error count: " + exception.getBindingResult().getErrorCount();
    }
}

