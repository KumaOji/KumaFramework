/*
 * Copyright (c) 2020-2030, kuma (2569277704@qq.com & https://blog.kumacloud.top/).
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

package com.kuma.boot.common.exception;

import com.kuma.boot.common.enums.ResultEnum;
import com.kuma.boot.common.enums.StatusEnum;
import com.kuma.boot.common.model.Code;

/**
 * IdempotencyException
 *
 * @author kuma
 * @version 2021.9
 * @since 2021-09-02 20:13:21
 */
public class IdempotencyException extends BootException {

    public IdempotencyException() {
    }

    public IdempotencyException(String message) {
        super(message);
    }

    public IdempotencyException(Throwable e) {
        super(e);
    }

    public IdempotencyException(String message, Throwable e) {
        super(message, e);
    }

    public IdempotencyException(String message, Throwable cause, boolean enableSuppression,
                                boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public IdempotencyException(ResultEnum result) {
        super(result);
    }

    public IdempotencyException(StatusEnum status, ResultEnum result) {
        super(status, result);
    }

    public IdempotencyException(ResultEnum result, Throwable e) {
        super(result, e);
    }

    public IdempotencyException(StatusEnum status, ResultEnum result, Throwable e) {
        super(status, result, e);
    }

    public IdempotencyException(Code code, String message) {
        super(code, message);
    }

    public IdempotencyException(StatusEnum status, Code code,
                                String message) {
        super(status, code, message);
    }

    public IdempotencyException(Code code, Throwable e) {
        super(code, e);
    }

    public IdempotencyException(StatusEnum status, Code code,
                                Throwable e) {
        super(status, code, e);
    }

    public IdempotencyException(Code code, Throwable e, String message) {
        super(code, e, message);
    }

    public IdempotencyException(StatusEnum status, Code code,
                                Throwable e, String message) {
        super(status, code, e, message);
    }
}
