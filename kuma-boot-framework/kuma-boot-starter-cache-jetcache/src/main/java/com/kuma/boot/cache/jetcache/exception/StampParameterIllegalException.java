//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.kuma.boot.cache.jetcache.exception;

import com.kuma.boot.common.enums.ResultEnum;
import com.kuma.boot.common.enums.StatusEnum;
import com.kuma.boot.common.exception.BootException;
import com.kuma.boot.common.model.Code;

public class StampParameterIllegalException extends BootException {
    public StampParameterIllegalException() {
    }

    public StampParameterIllegalException(String message) {
        super(message);
    }

    public StampParameterIllegalException(Throwable e) {
        super(e);
    }

    public StampParameterIllegalException(String message, Throwable e) {
        super(message, e);
    }

    public StampParameterIllegalException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public StampParameterIllegalException(ResultEnum result) {
        super(result);
    }

    public StampParameterIllegalException(StatusEnum status, ResultEnum result) {
        super(status, result);
    }

    public StampParameterIllegalException(ResultEnum result, Throwable e) {
        super(result, e);
    }

    public StampParameterIllegalException(StatusEnum status, ResultEnum result, Throwable e) {
        super(status, result, e);
    }

    public StampParameterIllegalException(Code code, String message) {
        super(code, message);
    }

    public StampParameterIllegalException(StatusEnum status, Code code, String message) {
        super(status, code, message);
    }

    public StampParameterIllegalException(Code code, Throwable e) {
        super(code, e);
    }

    public StampParameterIllegalException(StatusEnum status, Code code, Throwable e) {
        super(status, code, e);
    }

    public StampParameterIllegalException(Code code, Throwable e, String message) {
        super(code, e, message);
    }

    public StampParameterIllegalException(StatusEnum status, Code code, Throwable e, String message) {
        super(status, code, e, message);
    }
}
