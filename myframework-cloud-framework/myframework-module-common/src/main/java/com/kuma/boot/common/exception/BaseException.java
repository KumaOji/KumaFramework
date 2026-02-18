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
import java.io.Serializable;

/**
 * 基础异常
 *
 * @author kuma
 * @version 2021.9
 * @since 2021-09-02 19:29:47
 */
public class BaseException extends RuntimeException implements Serializable {

    @Serial
    private static final long serialVersionUID = 6610083281801529147L;

    /**
     * 异常码
     */
    private Code code = ResultEnum.FAILED.code();

    private StatusEnum status = StatusEnum.FAILURE;

    public BaseException() {
        super();
    }

    public BaseException(String message) {
        super(message);
    }

    public BaseException(Throwable e) {
        super(e);
    }

    public BaseException(String message, Throwable e) {
        super(message, e);
    }

    protected BaseException(String message, Throwable cause,
                            boolean enableSuppression,
                            boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public BaseException(ResultEnum result) {
        super(result.getDesc());
        this.code = result.code();
    }

    public BaseException(StatusEnum status, ResultEnum result) {
        super(result.getDesc());
        this.status = status;
        this.code = result.code();
    }

    public BaseException(ResultEnum result, Throwable e) {
        super(result.getDesc(), e);
        this.code = result.code();
    }

    public BaseException(StatusEnum status, ResultEnum result, Throwable e) {
        super(result.getDesc(), e);
        this.status = status;
        this.code = result.code();
    }

    public BaseException(Code code, String message) {
        super(message);
        this.code = code;
    }

    public BaseException(StatusEnum status, Code code, String message) {
        super(message);
        this.status = status;
        this.code = code;
    }

    public BaseException(Code code, Throwable e) {
        super(e);
        this.code = code;
    }


    public BaseException(StatusEnum status, Code code, Throwable e) {
        super(e);
        this.status = status;
        this.code = code;
    }

    public BaseException(Code code, Throwable e, String message) {
        super(message, e);
        this.code = code;
    }

    public BaseException(StatusEnum status, Code code, Throwable e, String message) {
        super(message, e);
        this.status = status;
        this.code = code;
    }

    public BaseException(String status, String code, String message) {
        super(message);
        this.status = StatusEnum.valueOf(status);
        this.code = Code.raw(code);
    }

    public Code getCode() {
        return code;
    }

    public void setCode(Code code) {
        this.code = code;
    }

    public StatusEnum getStatus() {
        return status;
    }

    public void setStatus(StatusEnum status) {
        this.status = status;
    }

    @Override
    public String getMessage() {
        return super.getMessage();
    }
}
