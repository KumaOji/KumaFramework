package com.kuma.boot.sign.exception;

import com.kuma.boot.common.exception.BootException;

/**
 * 签名验签异常，继承统一 {@link BootException}，由全局异常处理器转换为标准响应
 */
public class SignatureException extends BootException {

    public SignatureException(String message) {
        super(message);
    }

    public SignatureException(String message, Throwable cause) {
        super(message, cause);
    }
}
