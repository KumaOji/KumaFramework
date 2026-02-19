/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  cn.hutool.core.util.StrUtil
 *  org.jspecify.annotations.NonNull
 */
package com.kuma.boot.cache.redis.model;

import cn.hutool.core.util.StrUtil;

import java.time.Duration;
import java.util.Objects;
import org.jspecify.annotations.NonNull;

public class CacheHashKey
extends CacheKey {
    private @NonNull Object field;

    public CacheHashKey(@NonNull String key, @NonNull Object field) {
        super(key);
        this.field = field;
    }

    public CacheHashKey(@NonNull String key, @NonNull Object field, Duration expire) {
        super(key, expire);
        this.field = field;
    }

    public CacheKey tran() {
        return new CacheKey(StrUtil.join((CharSequence)":", (Object[])new Object[]{this.getKey(), this.getField()}), this.getExpire());
    }

    public @NonNull Object getField() {
        return this.field;
    }

    public void setField(@NonNull Object field) {
        this.field = field;
    }

    @Override
    public String toString() {
        return "CacheHashKey{field=" + String.valueOf(this.field) + "} " + super.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }
        CacheHashKey that = (CacheHashKey)o;
        return this.field.equals(that.field);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), this.field);
    }
}

