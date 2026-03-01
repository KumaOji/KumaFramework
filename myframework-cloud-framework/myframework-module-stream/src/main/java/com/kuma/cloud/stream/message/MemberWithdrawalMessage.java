/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.cloud.stream.message;

import java.math.BigDecimal;

public class MemberWithdrawalMessage {
    private BigDecimal price;
    private Long memberId;
    private String status;
    private String destination;

    public BigDecimal getPrice() {
        return this.price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Long getMemberId() {
        return this.memberId;
    }

    public void setMemberId(Long memberId) {
        this.memberId = memberId;
    }

    public String getStatus() {
        return this.status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDestination() {
        return this.destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }
}

