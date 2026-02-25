/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.ratelimit.ratelimitbs.extend.cache;

import java.util.List;
import java.util.concurrent.TimeUnit;

public abstract class AbstractCommonCacheService
implements ICommonCacheService {
    @Override
    public void set(String key, String value) {
        this.set(key, value, 0L);
    }

    @Override
    public void expire(String key, long expireTime, TimeUnit timeUnit) {
        long currentMills = System.currentTimeMillis();
        long actualMills = currentMills + timeUnit.toMillis(expireTime);
        this.expireAt(key, actualMills);
    }

    @Override
    public Object eval(String script, List<String> keys, List<String> params) {
        return this.eval(script, keys.size(), AbstractCommonCacheService.getParams(keys, params));
    }

    @Override
    public Object eval(String script) {
        return this.eval(script, 0, new String[0]);
    }

    protected static String[] getParams(List<String> keys, List<String> args) {
        int i;
        int keyCount = keys.size();
        int argCount = args.size();
        String[] params = new String[keyCount + args.size()];
        for (i = 0; i < keyCount; ++i) {
            params[i] = keys.get(i);
        }
        for (i = 0; i < argCount; ++i) {
            params[keyCount + i] = args.get(i);
        }
        return params;
    }
}

