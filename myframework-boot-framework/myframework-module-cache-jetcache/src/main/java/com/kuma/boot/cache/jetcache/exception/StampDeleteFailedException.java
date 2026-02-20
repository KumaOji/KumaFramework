//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.kuma.boot.cache.jetcache.exception;

import com.kuma.boot.common.enums.ResultEnum;
import com.kuma.boot.common.enums.StatusEnum;
import com.kuma.boot.common.exception.BootException;
import com.kuma.boot.common.model.Code;

public class StampDeleteFailedException extends BootException {
    public StampDeleteFailedException() {
    }

    public StampDeleteFailedException(String message) {
        super(message);
    }

    public StampDeleteFailedException(Throwable e) {
        super(e);
    }

    public StampDeleteFailedException(String message, Throwable e) {
        super(message, e);
    }

    public StampDeleteFailedException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public StampDeleteFailedException(ResultEnum result) {
        super(result);
    }

    public StampDeleteFailedException(StatusEnum status, ResultEnum result) {
        super(status, result);
    }

    public StampDeleteFailedException(ResultEnum result, Throwable e) {
        super(result, e);
    }

    public StampDeleteFailedException(StatusEnum status, ResultEnum result, Throwable e) {
        super(status, result, e);
    }

    public StampDeleteFailedException(Code code, String message) {
        super(code, message);
    }

    public StampDeleteFailedException(StatusEnum status, Code code, String message) {
        super(status, code, message);
    }

    public StampDeleteFailedException(Code code, Throwable e) {
        super(code, e);
    }

    public StampDeleteFailedException(StatusEnum status, Code code, Throwable e) {
        super(status, code, e);
    }

    public StampDeleteFailedException(Code code, Throwable e, String message) {
        super(code, e, message);
    }

    public StampDeleteFailedException(StatusEnum status, Code code, Throwable e, String message) {
        super(status, code, e, message);
    }
}
