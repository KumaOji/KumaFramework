package com.kuma.cloud.reference.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public final class OrderConflictException extends RuntimeException {
    public OrderConflictException(String message) { super(message); }
}
