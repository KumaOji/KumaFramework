/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.ratelimit.ratelimitbs.core.dto;

import java.io.Serializable;

public class RateLimitSlideWindowDto
implements Serializable {
    private int count;
    private long expireTime;

    public int getCount() {
        return this.count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public long getExpireTime() {
        return this.expireTime;
    }

    public void setExpireTime(long expireTime) {
        this.expireTime = expireTime;
    }

    public String toString() {
        return "RateLimitSlideWindowDto{count=" + this.count + ", expireTime=" + this.expireTime + "}";
    }
}

