/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.cloud.stream.framework.trigger.message;

public class PintuanOrderMessage {
    private Long pintuanId;
    private String orderSn;

    public Long getPintuanId() {
        return this.pintuanId;
    }

    public void setPintuanId(Long pintuanId) {
        this.pintuanId = pintuanId;
    }

    public String getOrderSn() {
        return this.orderSn;
    }

    public void setOrderSn(String orderSn) {
        this.orderSn = orderSn;
    }
}

