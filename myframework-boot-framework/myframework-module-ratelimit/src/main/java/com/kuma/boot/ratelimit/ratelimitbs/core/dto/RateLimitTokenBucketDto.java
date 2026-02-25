/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.ratelimit.ratelimitbs.core.dto;

import java.io.Serializable;

public class RateLimitTokenBucketDto
implements Serializable {
    private long rate;
    private long capacity;
    private volatile long tokenNum;
    private volatile long lastUpdateTime;

    public long getRate() {
        return this.rate;
    }

    public void setRate(long rate) {
        this.rate = rate;
    }

    public long getCapacity() {
        return this.capacity;
    }

    public void setCapacity(long capacity) {
        this.capacity = capacity;
    }

    public long getTokenNum() {
        return this.tokenNum;
    }

    public void setTokenNum(long tokenNum) {
        this.tokenNum = tokenNum;
    }

    public long getLastUpdateTime() {
        return this.lastUpdateTime;
    }

    public void setLastUpdateTime(long lastUpdateTime) {
        this.lastUpdateTime = lastUpdateTime;
    }

    public String toString() {
        return "RateLimitBucketDto{rate=" + this.rate + ", capacity=" + this.capacity + ", tokenNum=" + this.tokenNum + ", lastUpdateTime=" + this.lastUpdateTime + "}";
    }
}

