/*
 *  com.kuma.boot.common.enums.ResultEnum
 *  com.kuma.boot.common.enums.StatusEnum
 *  com.kuma.boot.common.exception.BootException
 *  com.kuma.boot.common.model.Code
 */
package com.kuma.boot.ratelimit.ratelimitaspect;

import com.kuma.boot.common.enums.ResultEnum;
import com.kuma.boot.common.enums.StatusEnum;
import com.kuma.boot.common.exception.BootException;
import com.kuma.boot.common.model.Code;

public class LimitException
extends BootException {
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

    public LimitException(StatusEnum status, Code code, String message) {
        super(status, code, message);
    }

    public LimitException(Code code, Throwable e) {
        super(code, e);
    }

    public LimitException(StatusEnum status, Code code, Throwable e) {
        super(status, code, e);
    }

    public LimitException(Code code, Throwable e, String message) {
        super(code, e, message);
    }

    public LimitException(StatusEnum status, Code code, Throwable e, String message) {
        super(status, code, e, message);
    }
}

