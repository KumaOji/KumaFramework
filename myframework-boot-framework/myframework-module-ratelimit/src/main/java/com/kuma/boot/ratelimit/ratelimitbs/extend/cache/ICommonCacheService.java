/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.ratelimit.ratelimitbs.extend.cache;

import java.util.List;
import java.util.concurrent.TimeUnit;

public interface ICommonCacheService {
    public void set(String var1, String var2);

    public void set(String var1, String var2, long var3);

    public String set(String var1, String var2, String var3, String var4, int var5);

    public String get(String var1);

    public boolean contains(String var1);

    public void remove(String var1);

    public long ttl(String var1);

    public void expire(String var1, long var2, TimeUnit var4);

    public void expireAt(String var1, long var2);

    public long expireAt(String var1);

    public Object eval(String var1, int var2, String ... var3);

    public Object eval(String var1, List<String> var2, List<String> var3);

    public Object eval(String var1);
}

