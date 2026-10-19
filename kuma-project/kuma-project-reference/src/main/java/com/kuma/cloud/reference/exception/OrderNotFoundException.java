package com.kuma.cloud.reference.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.UUID;

@ResponseStatus(HttpStatus.NOT_FOUND)
public final class OrderNotFoundException extends RuntimeException {
    public OrderNotFoundException(UUID id) { super("订单不存在：" + id); }
}
