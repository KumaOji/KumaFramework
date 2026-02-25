/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.ratelimit.ratelimitbs.core.dto;

import java.io.Serializable;
import java.util.List;

public class RateLimitSlideWindowInfo
implements Serializable {
    private long initTime;
    private List<RateLimitSlideWindowDto> windowList;

    public long getInitTime() {
        return this.initTime;
    }

    public void setInitTime(long initTime) {
        this.initTime = initTime;
    }

    public List<RateLimitSlideWindowDto> getWindowList() {
        return this.windowList;
    }

    public void setWindowList(List<RateLimitSlideWindowDto> windowList) {
        this.windowList = windowList;
    }

    public String toString() {
        return "RateLimitSlideWindowInfo{initTime=" + this.initTime + ", windowList=" + String.valueOf(this.windowList) + "}";
    }
}

