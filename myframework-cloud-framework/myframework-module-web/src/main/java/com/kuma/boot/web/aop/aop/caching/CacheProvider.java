/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.web.aop.aop.caching;

public interface CacheProvider {
    public <T> void put(String var1, T var2, long var3);

    public <T> T get(String var1, long var2);
}

