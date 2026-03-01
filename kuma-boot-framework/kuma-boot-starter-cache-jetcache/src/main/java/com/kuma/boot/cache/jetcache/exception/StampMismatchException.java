//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.kuma.boot.cache.jetcache.exception;

import com.kuma.boot.common.enums.ResultEnum;
import com.kuma.boot.common.enums.StatusEnum;
import com.kuma.boot.common.exception.BootException;
import com.kuma.boot.common.model.Code;

public class StampMismatchException extends BootException {
    public StampMismatchException() {
    }

    public StampMismatchException(String message) {
        super(message);
    }

    public StampMismatchException(Throwable e) {
        super(e);
    }

    public StampMismatchException(String message, Throwable e) {
        super(message, e);
    }

    public StampMismatchException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public StampMismatchException(ResultEnum result) {
        super(result);
    }

    public StampMismatchException(StatusEnum status, ResultEnum result) {
        super(status, result);
    }

    public StampMismatchException(ResultEnum result, Throwable e) {
        super(result, e);
    }

    public StampMismatchException(StatusEnum status, ResultEnum result, Throwable e) {
        super(status, result, e);
    }

    public StampMismatchException(Code code, String message) {
        super(code, message);
    }

    public StampMismatchException(StatusEnum status, Code code, String message) {
        super(status, code, message);
    }

    public StampMismatchException(Code code, Throwable e) {
        super(code, e);
    }

    public StampMismatchException(StatusEnum status, Code code, Throwable e) {
        super(status, code, e);
    }

    public StampMismatchException(Code code, Throwable e, String message) {
        super(code, e, message);
    }

    public StampMismatchException(StatusEnum status, Code code, Throwable e, String message) {
        super(status, code, e, message);
    }
}
