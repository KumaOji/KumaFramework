/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.ratelimit.ratelimitbs.api.dto;

import java.io.Serializable;
import java.util.concurrent.TimeUnit;

public class RateLimitConfigDto
implements Serializable {
    private int permits;
    private TimeUnit timeUnit;
    private long interval;
    private Long count;
    private boolean enable;

    public int getPermits() {
        return this.permits;
    }

    public void setPermits(int permits) {
        this.permits = permits;
    }

    public TimeUnit getTimeUnit() {
        return this.timeUnit;
    }

    public void setTimeUnit(TimeUnit timeUnit) {
        this.timeUnit = timeUnit;
    }

    public long getInterval() {
        return this.interval;
    }

    public void setInterval(long interval) {
        this.interval = interval;
    }

    public Long getCount() {
        return this.count;
    }

    public void setCount(Long count) {
        this.count = count;
    }

    public boolean isEnable() {
        return this.enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    public String toString() {
        return "RateLimitConfigDto{permits=" + this.permits + ", timeUnit=" + String.valueOf((Object)this.timeUnit) + ", interval=" + this.interval + ", count=" + this.count + ", enable=" + this.enable + "}";
    }
}

