package com.kuma.boot.idempotent.idempotentenhance.core.exception;

/**
 * 幂等并发异常
 *
 * @author wenpan 2023/01/07 12:57
 */
public class ConcurrentRequestException extends RuntimeException {

    public ConcurrentRequestException(String message) {
        super(message);
    }
}
