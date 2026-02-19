/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  cn.hutool.core.collection.CollUtil
 *  cn.hutool.core.convert.Convert
 *  cn.hutool.core.util.ObjUtil
 *  cn.hutool.core.util.StrUtil
 *  org.jspecify.annotations.NonNull
 *  org.jspecify.annotations.Nullable
 */
package com.kuma.boot.cache.caffeine.model;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.StrUtil;

import java.time.Duration;
import java.util.ArrayList;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

@FunctionalInterface
public interface CacheKeyBuilder {
    default public @NonNull String getTenant() {
        return "";
    }

    public @NonNull String getPrefix();

    default public @Nullable Duration getExpire() {
        return null;
    }

    default public CacheKey key(Object ... suffix) {
        String field = suffix.length > 0 ? Convert.toStr((Object)suffix[0], (String)"") : "";
        return this.hashFieldKey(field, suffix);
    }

    default public CacheHashKey hashFieldKey(@NonNull Object field, Object ... suffix) {
        String key = this.getKey(suffix);
        return new CacheHashKey(key, field, this.getExpire());
    }

    default public CacheHashKey hashKey(Object ... suffix) {
        String key = this.getKey(suffix);
        return new CacheHashKey(key, null, this.getExpire());
    }

    default public String getKey(Object ... suffix) {
        ArrayList<String> regionList = new ArrayList<String>();
        String tenant = this.getTenant();
        if (StrUtil.isNotEmpty((CharSequence)tenant)) {
            regionList.add(tenant);
        }
        String prefix = this.getPrefix();
        regionList.add(prefix);
        for (Object s : suffix) {
            if (!ObjUtil.isNotEmpty((Object)s)) continue;
            regionList.add(String.valueOf(s));
        }
        return CollUtil.join(regionList, (CharSequence)":");
    }
}

