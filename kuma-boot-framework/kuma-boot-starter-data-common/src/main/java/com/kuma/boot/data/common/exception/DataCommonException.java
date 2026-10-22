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

package com.kuma.boot.data.common.exception;


import com.kuma.boot.common.enums.ResultEnum;
import com.kuma.boot.common.enums.StatusEnum;
import com.kuma.boot.common.exception.BootException;
import com.kuma.boot.common.model.Code;
import java.io.Serial;

/**
 * 数据操作异常，复用框架错误码并保留调用方消息及原始异常。
 *
 * @author kuma
 * @version 2021.9
 * @since 2021-09-02 20:09:13
 */
public class DataCommonException extends BootException {

    @Serial private static final long serialVersionUID = 6610083281801529147L;

    public DataCommonException() {}

    public DataCommonException(String message) {
        super(message);
    }

    public DataCommonException(Throwable cause) {
        super(cause);
    }

    public DataCommonException(String message, Throwable cause) {
        super(message, cause);
    }

    public DataCommonException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public DataCommonException(ResultEnum result) {
        super(result);
    }

    public DataCommonException(ResultEnum result, String message) {
        super(result.code(), message);
    }

    public DataCommonException(ResultEnum result, Throwable cause) {
        super(result, cause);
    }

    public DataCommonException(ResultEnum result, String message, Throwable cause) {
        super(result.code(), cause, message);
    }

    public DataCommonException(StatusEnum status, ResultEnum result) {
        super(status, result);
    }

    public DataCommonException(StatusEnum status, ResultEnum result, Throwable cause) {
        super(status, result, cause);
    }

    public DataCommonException(Code code, String message) {
        super(code, message);
    }

    public DataCommonException(Code code, Throwable cause) {
        super(code, cause);
    }

    public DataCommonException(Code code, Throwable cause, String message) {
        super(code, cause, message);
    }

    public DataCommonException(StatusEnum status, Code code, String message) {
        super(status, code, message);
    }

    public DataCommonException(StatusEnum status, Code code, Throwable cause) {
        super(status, code, cause);
    }

    public DataCommonException(StatusEnum status, Code code, Throwable cause, String message) {
        super(status, code, cause, message);
    }
}
