//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.kuma.boot.cache.jetcache.exception;

import com.kuma.boot.common.enums.ResultEnum;
import com.kuma.boot.common.enums.StatusEnum;
import com.kuma.boot.common.exception.BootException;
import com.kuma.boot.common.model.Code;

public class MaximumLimitExceededException extends BootException {
    public MaximumLimitExceededException() {
    }

    public MaximumLimitExceededException(String message) {
        super(message);
    }

    public MaximumLimitExceededException(Throwable e) {
        super(e);
    }

    public MaximumLimitExceededException(String message, Throwable e) {
        super(message, e);
    }

    public MaximumLimitExceededException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public MaximumLimitExceededException(ResultEnum result) {
        super(result);
    }

    public MaximumLimitExceededException(StatusEnum status, ResultEnum result) {
        super(status, result);
    }

    public MaximumLimitExceededException(ResultEnum result, Throwable e) {
        super(result, e);
    }

    public MaximumLimitExceededException(StatusEnum status, ResultEnum result, Throwable e) {
        super(status, result, e);
    }

    public MaximumLimitExceededException(Code code, String message) {
        super(code, message);
    }

    public MaximumLimitExceededException(StatusEnum status, Code code, String message) {
        super(status, code, message);
    }

    public MaximumLimitExceededException(Code code, Throwable e) {
        super(code, e);
    }

    public MaximumLimitExceededException(StatusEnum status, Code code, Throwable e) {
        super(status, code, e);
    }

    public MaximumLimitExceededException(Code code, Throwable e, String message) {
        super(code, e, message);
    }

    public MaximumLimitExceededException(StatusEnum status, Code code, Throwable e, String message) {
        super(status, code, e, message);
    }
}
