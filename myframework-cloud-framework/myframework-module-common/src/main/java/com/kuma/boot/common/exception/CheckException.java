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
 * CheckedException
 *
 * @author kuma
 * @version 2021.9
 * @since 2021-09-02 20:09:16
 */
public class CheckException extends BootException {

    public CheckException() {
    }

    public CheckException(String message) {
        super(message);
    }

    public CheckException(Throwable e) {
        super(e);
    }

    public CheckException(String message, Throwable e) {
        super(message, e);
    }

    public CheckException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public CheckException(ResultEnum result) {
        super(result);
    }

    public CheckException(StatusEnum status, ResultEnum result) {
        super(status, result);
    }

    public CheckException(ResultEnum result, Throwable e) {
        super(result, e);
    }

    public CheckException(StatusEnum status, ResultEnum result, Throwable e) {
        super(status, result, e);
    }

    public CheckException(Code code, String message) {
        super(code, message);
    }

    public CheckException(StatusEnum status, Code code,
                          String message) {
        super(status, code, message);
    }

    public CheckException(Code code, Throwable e) {
        super(code, e);
    }

    public CheckException(StatusEnum status, Code code,
                          Throwable e) {
        super(status, code, e);
    }

    public CheckException(Code code, Throwable e, String message) {
        super(code, e, message);
    }

    public CheckException(StatusEnum status, Code code,
                          Throwable e, String message) {
        super(status, code, e, message);
    }
}
