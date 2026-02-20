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

/**
 * Class to handle {@link MethodArgumentNotValidException} exceptions. This is typically used when
 * `@Valid` is used on {@link org.springframework.web.bind.annotation.RestController} method
 * arguments.
 */
public class MethodArgumentNotValidApiExceptionHandler extends com.kuma.boot.web.error.handler.AbstractApiExceptionHandler {

    public MethodArgumentNotValidApiExceptionHandler(
            ErrorHandlingProperties properties,
            HttpStatusMapper httpStatusMapper,
            ErrorCodeMapper errorCodeMapper,
            ErrorMessageMapper errorMessageMapper) {
        super(httpStatusMapper, errorCodeMapper, errorMessageMapper);
    }

    @Override
    public boolean canHandle(Throwable exception) {
        return exception instanceof MethodArgumentNotValidException;
    }

    @Override
    public ApiErrorResponse handle(Throwable exception) {

        MethodArgumentNotValidException ex = (MethodArgumentNotValidException) exception;
        ApiErrorResponse response =
                new ApiErrorResponse(
                        getHttpStatus(exception, HttpStatus.BAD_REQUEST),
                        getErrorCode(exception),
                        getMessage(ex));
        BindingResult bindingResult = ex.getBindingResult();
        if (bindingResult.hasFieldErrors()) {
            bindingResult.getFieldErrors().stream()
                    .map(
                            fieldError ->
                                    new ApiFieldError(
                                            getCode(fieldError),
                                            fieldError.getField(),
                                            getMessage(fieldError),
                                            fieldError.getRejectedValue()))
                    .forEach(response::addFieldError);
        }

        if (bindingResult.hasGlobalErrors()) {
            bindingResult.getGlobalErrors().stream()
                    .map(
                            globalError ->
                                    new ApiGlobalError(
                                            errorCodeMapper.getErrorCode(globalError.getCode()),
                                            errorMessageMapper.getErrorMessage(
                                                    globalError.getCode(),
                                                    globalError.getDefaultMessage())))
                    .forEach(response::addGlobalError);
        }

        return response;
    }

    private String getCode(FieldError fieldError) {
        String code = fieldError.getCode();
        String fieldSpecificCode = fieldError.getField() + "." + code;
        return errorCodeMapper.getErrorCode(fieldSpecificCode, code);
    }

    private String getMessage(FieldError fieldError) {
        String code = fieldError.getCode();
        String fieldSpecificCode = fieldError.getField() + "." + code;
        return errorMessageMapper.getErrorMessage(
                fieldSpecificCode, code, fieldError.getDefaultMessage());
    }

    private String getMessage(MethodArgumentNotValidException exception) {
        return "Validation failed for object='"
                + exception.getBindingResult().getObjectName()
                + "'. Error count: "
                + exception.getBindingResult().getErrorCount();
    }
}
