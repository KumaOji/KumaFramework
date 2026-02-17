/*
 * Copyright (c) 2020-2030, Shuigedeng (2569277704@qq.com & https://blog.kumacloud.top/).
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
 * 通用的运行时异常
 *
 * @author kuma
 * @version 2022.04
 * @since 2022-04-22 10:49:26
 */
public class BootException extends BaseException {

    public BootException() {
    }

    public BootException(String message) {
        super(message);
    }

    public BootException(Throwable e) {
        super(e);
    }

    public BootException(String message, Throwable e) {
        super(message, e);
    }

    public BootException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public BootException(ResultEnum result) {
        super(result);
    }

    public BootException(StatusEnum status, ResultEnum result) {
        super(status, result);
    }

    public BootException(ResultEnum result, Throwable e) {
        super(result, e);
    }

    public BootException(StatusEnum status, ResultEnum result, Throwable e) {
        super(status, result, e);
    }

    public BootException(Code code, String message) {
        super(code, message);
    }

    public BootException(StatusEnum status, Code code,
                         String message) {
        super(status, code, message);
    }

    public BootException(Code code, Throwable e) {
        super(code, e);
    }

    public BootException(StatusEnum status, Code code,
                         Throwable e) {
        super(status, code, e);
    }

    public BootException(Code code, Throwable e, String message) {
        super(code, e, message);
    }

    public BootException(StatusEnum status, Code code,
                         Throwable e, String message) {
        super(status, code, e, message);
    }
}
