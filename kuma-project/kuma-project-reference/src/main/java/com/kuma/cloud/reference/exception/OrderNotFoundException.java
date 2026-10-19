package com.kuma.cloud.reference.exception;

import java.util.UUID;

public final class OrderNotFoundException extends RuntimeException {
    public OrderNotFoundException(UUID id) { super("订单不存在：" + id); }
}
