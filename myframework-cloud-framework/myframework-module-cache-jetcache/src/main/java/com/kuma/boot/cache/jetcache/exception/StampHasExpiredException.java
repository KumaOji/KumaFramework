//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.kuma.boot.cache.jetcache.exception;

import com.kuma.boot.common.enums.ResultEnum;
import com.kuma.boot.common.enums.StatusEnum;
import com.kuma.boot.common.exception.BootException;
import com.kuma.boot.common.model.Code;

public class StampHasExpiredException extends BootException {
    public StampHasExpiredException() {
    }

    public StampHasExpiredException(String message) {
        super(message);
    }

    public StampHasExpiredException(Throwable e) {
        super(e);
    }

    public StampHasExpiredException(String message, Throwable e) {
        super(message, e);
    }

    public StampHasExpiredException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public StampHasExpiredException(ResultEnum result) {
        super(result);
    }

    public StampHasExpiredException(StatusEnum status, ResultEnum result) {
        super(status, result);
    }

    public StampHasExpiredException(ResultEnum result, Throwable e) {
        super(result, e);
    }

    public StampHasExpiredException(StatusEnum status, ResultEnum result, Throwable e) {
        super(status, result, e);
    }

    public StampHasExpiredException(Code code, String message) {
        super(code, message);
    }

    public StampHasExpiredException(StatusEnum status, Code code, String message) {
        super(status, code, message);
    }

    public StampHasExpiredException(Code code, Throwable e) {
        super(code, e);
    }

    public StampHasExpiredException(StatusEnum status, Code code, Throwable e) {
        super(status, code, e);
    }

    public StampHasExpiredException(Code code, Throwable e, String message) {
        super(code, e, message);
    }

    public StampHasExpiredException(StatusEnum status, Code code, Throwable e, String message) {
        super(status, code, e, message);
    }
}
