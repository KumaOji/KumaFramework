/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.cache.redis.redisson;

public enum RedisDelayQueueEnum {
    ORDER_PAYMENT_TIMEOUT("ORDER_PAYMENT_TIMEOUT", "\u8ba2\u5355\u652f\u4ed8\u8d85\u65f6\uff0c\u81ea\u52a8\u53d6\u6d88\u8ba2\u5355", "orderPaymentTimeout"),
    ORDER_TIMEOUT_NOT_EVALUATED("ORDER_TIMEOUT_NOT_EVALUATED", "\u8ba2\u5355\u8d85\u65f6\u672a\u8bc4\u4ef7\uff0c\u7cfb\u7edf\u9ed8\u8ba4\u597d\u8bc4", "orderTimeoutNotEvaluated");

    private String code;
    private String name;
    private String beanId;

    private RedisDelayQueueEnum(String code, String name, String beanId) {
        this.code = code;
        this.name = name;
        this.beanId = beanId;
    }

    public String getCode() {
        return this.code;
    }

    public String getName() {
        return this.name;
    }

    public String getBeanId() {
        return this.beanId;
    }
}

