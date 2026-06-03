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

package com.kuma.boot.idempotent.exception;

import com.kuma.boot.common.enums.ResultEnum;
import com.kuma.boot.common.enums.StatusEnum;
import com.kuma.boot.common.exception.BaseException;
import com.kuma.boot.common.exception.BootException;
import com.kuma.boot.common.model.Code;

/**
 * 幂等校验异常
 *
 * @author kuma
 * @version 2021.9
 * @since 2021-09-02 22:19:03
 */
public class IdempotentException extends BootException {

    public IdempotentException() {
    }

    public IdempotentException(String message) {
        super(message);
    }

    public IdempotentException(Throwable e) {
        super(e);
    }

    public IdempotentException(String message, Throwable e) {
        super(message, e);
    }

    public IdempotentException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public IdempotentException(ResultEnum result) {
        super(result);
    }

    public IdempotentException(StatusEnum status, ResultEnum result) {
        super(status, result);
    }

    public IdempotentException(ResultEnum result, Throwable e) {
        super(result, e);
    }

    public IdempotentException(StatusEnum status, ResultEnum result, Throwable e) {
        super(status, result, e);
    }

    public IdempotentException(Code code, String message) {
        super(code, message);
    }

    public IdempotentException(StatusEnum status, Code code,
                               String message) {
        super(status, code, message);
    }

    public IdempotentException(Code code, Throwable e) {
        super(code, e);
    }

    public IdempotentException(StatusEnum status, Code code,
                               Throwable e) {
        super(status, code, e);
    }

    public IdempotentException(Code code, Throwable e, String message) {
        super(code, e, message);
    }

    public IdempotentException(StatusEnum status, Code code,
                               Throwable e, String message) {
        super(status, code, e, message);
    }
}
