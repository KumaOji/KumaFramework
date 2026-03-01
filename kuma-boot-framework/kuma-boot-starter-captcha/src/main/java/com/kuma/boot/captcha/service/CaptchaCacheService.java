/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.captcha.service;

public interface CaptchaCacheService {
    public void set(String var1, String var2, long var3);

    public boolean exists(String var1);

    public void delete(String var1);

    public String get(String var1);

    public String type();

    default public Long increment(String key, long val) {
        return 0L;
    }
}

