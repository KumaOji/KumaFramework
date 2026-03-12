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

package com.kuma.boot.cache.jetcache.exception;

import com.kuma.boot.common.enums.ResultEnum;
import com.kuma.boot.common.enums.StatusEnum;
import com.kuma.boot.common.exception.BootException;
import com.kuma.boot.common.model.Code;

import java.io.Serial;

/** Stamp 参数非法异常。 */
public class StampParameterIllegalException extends BootException {

    @Serial
    private static final long serialVersionUID = 1L;

    public StampParameterIllegalException() {}

    public StampParameterIllegalException(String message) { super(message); }

    public StampParameterIllegalException(Throwable e) { super(e); }

    public StampParameterIllegalException(String message, Throwable e) { super(message, e); }

    public StampParameterIllegalException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public StampParameterIllegalException(ResultEnum result) { super(result); }

    public StampParameterIllegalException(StatusEnum status, ResultEnum result) { super(status, result); }

    public StampParameterIllegalException(ResultEnum result, Throwable e) { super(result, e); }

    public StampParameterIllegalException(StatusEnum status, ResultEnum result, Throwable e) { super(status, result, e); }

    public StampParameterIllegalException(Code code, String message) { super(code, message); }

    public StampParameterIllegalException(StatusEnum status, Code code, String message) { super(status, code, message); }

    public StampParameterIllegalException(Code code, Throwable e) { super(code, e); }

    public StampParameterIllegalException(StatusEnum status, Code code, Throwable e) { super(status, code, e); }

    public StampParameterIllegalException(Code code, Throwable e, String message) { super(code, e, message); }

    public StampParameterIllegalException(StatusEnum status, Code code, Throwable e, String message) {
        super(status, code, e, message);
    }
}
