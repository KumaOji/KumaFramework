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
import java.io.Serial;

/**
 * BusinessException
 *
 * @author kuma
 * @version 2021.9
 * @since 2021-09-02 20:09:13
 */
public class BusinessException extends BaseException {

    @Serial private static final long serialVersionUID = 6610083281801529147L;

    public BusinessException(StatusEnum status, Code code, Throwable e, String message) {
        super(status, code, e, message);
    }

    public BusinessException(Code code, Throwable e, String message) {
        super(code, e, message);
    }

    public BusinessException(StatusEnum status, Code code, Throwable e) {
        super(status, code, e);
    }

    public BusinessException(Code code, Throwable e) {
        super(code, e);
    }

    public BusinessException(StatusEnum status, Code code,
                             String message) {
        super(status, code, message);
    }

    public BusinessException(Code code, String message) {
        super(code, message);
    }

    public BusinessException(StatusEnum status, ResultEnum result, Throwable e) {
        super(status, result, e);
    }

    public BusinessException(ResultEnum result, Throwable e) {
        super(result, e);
    }

    public BusinessException(StatusEnum status, ResultEnum result) {
        super(status, result);
    }

    public BusinessException(ResultEnum result) {
        super(result);
    }

    public BusinessException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public BusinessException(String message, Throwable e) {
        super(message, e);
    }

    public BusinessException(Throwable e) {
        super(e);
    }

    public BusinessException(String message) {
        super(message);
    }

    public BusinessException() {
    }
}
