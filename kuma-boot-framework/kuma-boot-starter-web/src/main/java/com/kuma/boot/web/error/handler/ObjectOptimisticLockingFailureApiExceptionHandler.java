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
import com.kuma.boot.web.error.ErrorHandlingProperties;
import com.kuma.boot.web.error.mapper.ErrorCodeMapper;
import com.kuma.boot.web.error.mapper.ErrorMessageMapper;
import com.kuma.boot.web.error.mapper.HttpStatusMapper;
import org.springframework.http.HttpStatus;

// import org.springframework.orm.ObjectOptimisticLockingFailureException;

/**
 * ObjectOptimisticLockingFailureApiExceptionHandler
 *
 * @author kuma
 * @version 2021.10
 * @since 2022-01-12 09:25:53
 */
public class ObjectOptimisticLockingFailureApiExceptionHandler extends com.kuma.boot.web.error.handler.AbstractApiExceptionHandler {

    public ObjectOptimisticLockingFailureApiExceptionHandler(
            ErrorHandlingProperties properties,
            HttpStatusMapper httpStatusMapper,
            ErrorCodeMapper errorCodeMapper,
            ErrorMessageMapper errorMessageMapper) {
        super(httpStatusMapper, errorCodeMapper, errorMessageMapper);
    }

    @Override
    public boolean canHandle(Throwable exception) {
        //        return exception instanceof ObjectOptimisticLockingFailureException;
        return false;
    }

    @Override
    public ApiErrorResponse handle(Throwable exception) {
        ApiErrorResponse response =
                new ApiErrorResponse(
                        getHttpStatus(exception, HttpStatus.CONFLICT),
                        getErrorCode(exception),
                        getErrorMessage(exception));
        //        ObjectOptimisticLockingFailureException ex =
        // (ObjectOptimisticLockingFailureException) exception;
        //        response.addErrorProperty("identifier", ex.getIdentifier());
        //        response.addErrorProperty("persistentClassName", ex.getPersistentClassName());
        return response;
    }
}
