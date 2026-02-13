/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.common.exception;

import com.kuma.boot.common.enums.ResultEnum;
import com.kuma.boot.common.enums.StatusEnum;
import com.kuma.boot.common.model.Code;
import java.io.Serializable;

public class BaseException
extends RuntimeException
implements Serializable {
    private static final long serialVersionUID = 6610083281801529147L;
    private Code code = ResultEnum.FAILED.code();
    private StatusEnum status = StatusEnum.FAILURE;

    public BaseException() {
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

    protected BaseException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
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
        return this.code;
    }

    public void setCode(Code code) {
        this.code = code;
    }

    public StatusEnum getStatus() {
        return this.status;
    }

    public void setStatus(StatusEnum status) {
        this.status = status;
    }

    @Override
    public String getMessage() {
        return super.getMessage();
    }
}

