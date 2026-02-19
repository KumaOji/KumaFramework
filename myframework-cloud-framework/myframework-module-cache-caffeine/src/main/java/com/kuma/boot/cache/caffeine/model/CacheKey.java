/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  org.jspecify.annotations.NonNull
 */
package com.kuma.boot.cache.caffeine.model;

import java.time.Duration;
import java.util.Objects;
import org.jspecify.annotations.NonNull;

public class CacheKey {
    private String key;
    private Duration expire;

    public CacheKey(@NonNull String key) {
        this.key = key;
    }

    public CacheKey() {
    }

    public CacheKey(@NonNull String key, Duration expire) {
        this.key = key;
        this.expire = expire;
    }

    public String toString() {
        return "CacheKey{key='" + this.key + "', expire=" + String.valueOf(this.expire) + "}";
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        CacheKey cacheKey = (CacheKey)o;
        return this.key.equals(cacheKey.key) && Objects.equals(this.expire, cacheKey.expire);
    }

    public int hashCode() {
        return Objects.hash(this.key, this.expire);
    }

    public @NonNull String getKey() {
        return this.key;
    }

    public void setKey(@NonNull String key) {
        this.key = key;
    }

    public Duration getExpire() {
        return this.expire;
    }

    public void setExpire(Duration expire) {
        this.expire = expire;
    }
}

