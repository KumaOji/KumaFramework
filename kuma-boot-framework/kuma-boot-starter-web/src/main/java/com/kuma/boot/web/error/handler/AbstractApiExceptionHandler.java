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

import com.kuma.boot.web.error.ApiExceptionHandler;
import com.kuma.boot.web.error.mapper.ErrorCodeMapper;
import com.kuma.boot.web.error.mapper.ErrorMessageMapper;
import com.kuma.boot.web.error.mapper.HttpStatusMapper;
import org.springframework.http.HttpStatus;

/**
 * AbstractApiExceptionHandler
 *
 * @author kuma
 * @version 2021.10
 * @since 2022-01-12 09:25:53
 */
public abstract class AbstractApiExceptionHandler implements ApiExceptionHandler {

    protected final HttpStatusMapper httpStatusMapper;
    protected final ErrorCodeMapper errorCodeMapper;
    protected final ErrorMessageMapper errorMessageMapper;

    public AbstractApiExceptionHandler(
            HttpStatusMapper httpStatusMapper,
            ErrorCodeMapper errorCodeMapper,
            ErrorMessageMapper errorMessageMapper) {
        this.httpStatusMapper = httpStatusMapper;
        this.errorCodeMapper = errorCodeMapper;
        this.errorMessageMapper = errorMessageMapper;
    }

    protected HttpStatus getHttpStatus(Throwable exception, HttpStatus defaultHttpStatus) {
        return httpStatusMapper.getHttpStatus(exception, defaultHttpStatus);
    }

    protected String getErrorCode(Throwable exception) {
        return errorCodeMapper.getErrorCode(exception);
    }

    protected String getErrorMessage(Throwable exception) {
        return errorMessageMapper.getErrorMessage(exception);
    }
}
