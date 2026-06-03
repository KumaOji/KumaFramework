package com.kuma.boot.idempotent.idempotentenhance.core.exception;

/**
 * 幂等异常
 *
 * @author wenpan 2022/12/31 15:52
 */
public class IdempotentException extends RuntimeException {

    public IdempotentException(String message) {
        super(message);
    }

}
