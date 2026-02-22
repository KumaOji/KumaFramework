/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.security.spring.authentication.stamp;

public class SignInErrorStatus {
    private int errorTimes;
    private int remainTimes;
    private Boolean locked;

    public int getErrorTimes() {
        return this.errorTimes;
    }

    public void setErrorTimes(int errorTimes) {
        this.errorTimes = errorTimes;
    }

    public int getRemainTimes() {
        return this.remainTimes;
    }

    public void setRemainTimes(int remainTimes) {
        this.remainTimes = remainTimes;
    }

    public Boolean getLocked() {
        return this.locked;
    }

    public void setLocked(Boolean locked) {
        this.locked = locked;
    }
}

