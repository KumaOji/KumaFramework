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

/** Stamp 值不匹配异常。 */
public class StampMismatchException extends BootException {

    @Serial
    private static final long serialVersionUID = 1L;

    public StampMismatchException() {}

    public StampMismatchException(String message) { super(message); }

    public StampMismatchException(Throwable e) { super(e); }

    public StampMismatchException(String message, Throwable e) { super(message, e); }

    public StampMismatchException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public StampMismatchException(ResultEnum result) { super(result); }

    public StampMismatchException(StatusEnum status, ResultEnum result) { super(status, result); }

    public StampMismatchException(ResultEnum result, Throwable e) { super(result, e); }

    public StampMismatchException(StatusEnum status, ResultEnum result, Throwable e) { super(status, result, e); }

    public StampMismatchException(Code code, String message) { super(code, message); }

    public StampMismatchException(StatusEnum status, Code code, String message) { super(status, code, message); }

    public StampMismatchException(Code code, Throwable e) { super(code, e); }

    public StampMismatchException(StatusEnum status, Code code, Throwable e) { super(status, code, e); }

    public StampMismatchException(Code code, Throwable e, String message) { super(code, e, message); }

    public StampMismatchException(StatusEnum status, Code code, Throwable e, String message) {
        super(status, code, e, message);
    }
}
