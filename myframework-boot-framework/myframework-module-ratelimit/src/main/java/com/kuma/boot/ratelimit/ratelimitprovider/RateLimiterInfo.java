/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.ratelimit.ratelimitprovider;

public class RateLimiterInfo {
    private String key;
    private long rate;
    private long rateInterval;

    public RateLimiterInfo(String key, long rate, long rateInterval) {
        this.key = key;
        this.rate = rate;
        this.rateInterval = rateInterval;
    }

    public String getKey() {
        return this.key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public long getRate() {
        return this.rate;
    }

    public void setRate(long rate) {
        this.rate = rate;
    }

    public long getRateInterval() {
        return this.rateInterval;
    }

    public void setRateInterval(long rateInterval) {
        this.rateInterval = rateInterval;
    }
}

