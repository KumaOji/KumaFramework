/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  com.kuma.boot.common.enums.ResultEnum
 *  com.kuma.boot.common.enums.StatusEnum
 *  com.kuma.boot.common.exception.BootException
 *  com.kuma.boot.common.model.Code
 */
package com.kuma.boot.captcha.model;

import com.kuma.boot.common.enums.ResultEnum;
import com.kuma.boot.common.enums.StatusEnum;
import com.kuma.boot.common.exception.BootException;
import com.kuma.boot.common.model.Code;

public class CaptchaException
extends BootException {
    public CaptchaException() {
    }

    public CaptchaException(String message) {
        super(message);
    }

    public CaptchaException(Throwable e) {
        super(e);
    }

    public CaptchaException(String message, Throwable e) {
        super(message, e);
    }

    public CaptchaException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public CaptchaException(ResultEnum result) {
        super(result);
    }

    public CaptchaException(StatusEnum status, ResultEnum result) {
        super(status, result);
    }

    public CaptchaException(ResultEnum result, Throwable e) {
        super(result, e);
    }

    public CaptchaException(StatusEnum status, ResultEnum result, Throwable e) {
        super(status, result, e);
    }

    public CaptchaException(Code code, String message) {
        super(code, message);
    }

    public CaptchaException(StatusEnum status, Code code, String message) {
        super(status, code, message);
    }

    public CaptchaException(Code code, Throwable e) {
        super(code, e);
    }

    public CaptchaException(StatusEnum status, Code code, Throwable e) {
        super(status, code, e);
    }

    public CaptchaException(Code code, Throwable e, String message) {
        super(code, e, message);
    }

    public CaptchaException(StatusEnum status, Code code, Throwable e, String message) {
        super(status, code, e, message);
    }

    public CaptchaException(CaptchaCodeEnum captchaCodeEnum) {
    }
}

