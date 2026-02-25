/*
 *  com.kuma.boot.common.enums.ResultEnum
 *  com.kuma.boot.common.enums.StatusEnum
 *  com.kuma.boot.common.exception.BootException
 *  com.kuma.boot.common.model.Code
 */
package com.kuma.boot.ratelimit.ratelimitguava;

import com.kuma.boot.common.enums.ResultEnum;
import com.kuma.boot.common.enums.StatusEnum;
import com.kuma.boot.common.exception.BootException;
import com.kuma.boot.common.model.Code;

public class GuavaLimitException
extends BootException {
    public GuavaLimitException() {
    }

    public GuavaLimitException(String message) {
        super(message);
    }

    public GuavaLimitException(Throwable e) {
        super(e);
    }

    public GuavaLimitException(String message, Throwable e) {
        super(message, e);
    }

    public GuavaLimitException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public GuavaLimitException(ResultEnum result) {
        super(result);
    }

    public GuavaLimitException(StatusEnum status, ResultEnum result) {
        super(status, result);
    }

    public GuavaLimitException(ResultEnum result, Throwable e) {
        super(result, e);
    }

    public GuavaLimitException(StatusEnum status, ResultEnum result, Throwable e) {
        super(status, result, e);
    }

    public GuavaLimitException(Code code, String message) {
        super(code, message);
    }

    public GuavaLimitException(StatusEnum status, Code code, String message) {
        super(status, code, message);
    }

    public GuavaLimitException(Code code, Throwable e) {
        super(code, e);
    }

    public GuavaLimitException(StatusEnum status, Code code, Throwable e) {
        super(status, code, e);
    }

    public GuavaLimitException(Code code, Throwable e, String message) {
        super(code, e, message);
    }

    public GuavaLimitException(StatusEnum status, Code code, Throwable e, String message) {
        super(status, code, e, message);
    }
}

