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

package com.kuma.boot.ratelimit.ratelimitaspect;

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
public class LimitException extends BootException {

    public LimitException() {
    }

    public LimitException(String message) {
        super(message);
    }

    public LimitException(Throwable e) {
        super(e);
    }

    public LimitException(String message, Throwable e) {
        super(message, e);
    }

    public LimitException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public LimitException(ResultEnum result) {
        super(result);
    }

    public LimitException(StatusEnum status, ResultEnum result) {
        super(status, result);
    }

    public LimitException(ResultEnum result, Throwable e) {
        super(result, e);
    }

    public LimitException(StatusEnum status, ResultEnum result, Throwable e) {
        super(status, result, e);
    }

    public LimitException(Code code, String message) {
        super(code, message);
    }

    public LimitException(StatusEnum status, Code code,
                          String message) {
        super(status, code, message);
    }

    public LimitException(Code code, Throwable e) {
        super(code, e);
    }

    public LimitException(StatusEnum status, Code code,
                          Throwable e) {
        super(status, code, e);
    }

    public LimitException(Code code, Throwable e, String message) {
        super(code, e, message);
    }

    public LimitException(StatusEnum status, Code code,
                          Throwable e, String message) {
        super(status, code, e, message);
    }
}
