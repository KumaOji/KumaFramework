/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.ratelimit.ratelimitbs.core.dto;

import java.io.Serializable;

public class RateLimitLeakyBucketDto
implements Serializable {
    private long rate;
    private long capacity;
    private volatile long water;
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

    public long getWater() {
        return this.water;
    }

    public void setWater(long water) {
        this.water = water;
    }

    public long getLastUpdateTime() {
        return this.lastUpdateTime;
    }

    public void setLastUpdateTime(long lastUpdateTime) {
        this.lastUpdateTime = lastUpdateTime;
    }

    public String toString() {
        return "RateLimitLeakyBucketDto{rate=" + this.rate + ", capacity=" + this.capacity + ", water=" + this.water + ", lastUpdateTime=" + this.lastUpdateTime + "}";
    }
}

