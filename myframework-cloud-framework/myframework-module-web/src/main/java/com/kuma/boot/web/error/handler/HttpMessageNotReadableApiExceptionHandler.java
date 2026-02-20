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
import com.kuma.boot.web.error.ApiExceptionHandler;
import com.kuma.boot.web.error.ErrorHandlingProperties;
import com.kuma.boot.web.error.mapper.ErrorCodeMapper;
import com.kuma.boot.web.error.mapper.ErrorMessageMapper;
import com.kuma.boot.web.error.mapper.HttpStatusMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;

/**
 * {@link ApiExceptionHandler} for {@link HttpMessageNotReadableException}. This typically happens
 * when Spring can't properly decode the incoming request to JSON.
 */
public class HttpMessageNotReadableApiExceptionHandler extends com.kuma.boot.web.error.handler.AbstractApiExceptionHandler {

    public HttpMessageNotReadableApiExceptionHandler(
            ErrorHandlingProperties properties,
            HttpStatusMapper httpStatusMapper,
            ErrorCodeMapper errorCodeMapper,
            ErrorMessageMapper errorMessageMapper) {
        super(httpStatusMapper, errorCodeMapper, errorMessageMapper);
    }

    @Override
    public boolean canHandle(Throwable exception) {
        return exception instanceof HttpMessageNotReadableException;
    }

    @Override
    public ApiErrorResponse handle(Throwable exception) {
        return new ApiErrorResponse(
                getHttpStatus(exception, HttpStatus.BAD_REQUEST),
                getErrorCode(exception),
                getErrorMessage(exception));
    }
}
