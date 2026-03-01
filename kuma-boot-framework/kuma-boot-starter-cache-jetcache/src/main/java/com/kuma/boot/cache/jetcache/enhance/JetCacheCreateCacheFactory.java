//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.kuma.boot.cache.jetcache.enhance;

import com.alicp.jetcache.Cache;
import com.alicp.jetcache.CacheManager;
import com.alicp.jetcache.anno.CacheType;
import com.alicp.jetcache.template.QuickConfig;
import com.kuma.boot.common.utils.lang.StringUtils;
import java.time.Duration;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.ObjectUtils;

public class JetCacheCreateCacheFactory {
    private final CacheManager cacheManager;

    public JetCacheCreateCacheFactory(CacheManager cacheManager) {
        this.cacheManager = cacheManager;
    }

    public <K, V> Cache<K, V> create(String name) {
        return this.create(name, Duration.ofHours(2L));
    }

    public <K, V> Cache<K, V> create(String name, Duration expire) {
        return this.create(name, expire, true);
    }

    public <K, V> Cache<K, V> create(String name, Duration expire, Boolean cacheNullValue) {
        return this.create(name, (Duration)expire, (Boolean)cacheNullValue, (Boolean)null);
    }

    public <K, V> Cache<K, V> create(String name, Duration expire, Boolean cacheNullValue, Boolean syncLocal) {
        return this.<K, V>create(name, CacheType.BOTH, expire, cacheNullValue, syncLocal);
    }

    public <K, V> Cache<K, V> create(String name, CacheType cacheType) {
        return this.create(name, (CacheType)cacheType, (Duration)null);
    }

    public <K, V> Cache<K, V> create(String name, CacheType cacheType, Duration expire) {
        return this.create(name, cacheType, expire, true);
    }

    public <K, V> Cache<K, V> create(String name, CacheType cacheType, Duration expire, Boolean cacheNullValue) {
        return this.<K, V>create(name, cacheType, expire, cacheNullValue, (Boolean)null);
    }

    public <K, V> Cache<K, V> create(String name, CacheType cacheType, Duration expire, Boolean cacheNullValue, Boolean syncLocal) {
        return this.<K, V>create((String)null, name, cacheType, expire, cacheNullValue, syncLocal);
    }

    public <K, V> Cache<K, V> create(String area, String name, CacheType cacheType, Duration expire, Boolean cacheNullValue, Boolean syncLocal) {
        return this.<K, V>create(area, name, cacheType, expire, cacheNullValue, syncLocal, (Duration)null);
    }

    public <K, V> Cache<K, V> create(String area, String name, CacheType cacheType, Duration expire, Boolean cacheNullValue, Boolean syncLocal, Duration localExpire) {
        return this.<K, V>create(area, name, cacheType, expire, cacheNullValue, syncLocal, localExpire, (Integer)null);
    }

    public <K, V> Cache<K, V> create(String area, String name, CacheType cacheType, Duration expire, Boolean cacheNullValue, Boolean syncLocal, Duration localExpire, Integer localLimit) {
        return this.<K, V>create(area, name, cacheType, expire, cacheNullValue, syncLocal, localExpire, localLimit, false);
    }

    public <K, V> Cache<K, V> create(String area, String name, CacheType cacheType, Duration expire, Boolean cacheNullValue, Boolean syncLocal, Duration localExpire, Integer localLimit, Boolean useAreaInPrefix) {
        return this.<K, V>create(area, name, cacheType, expire, cacheNullValue, syncLocal, localExpire, localLimit, useAreaInPrefix, false, (Duration)null);
    }

    public <K, V> Cache<K, V> create(String area, String name, CacheType cacheType, Duration expire, Boolean cacheNullValue, Boolean syncLocal, Duration localExpire, Integer localLimit, Boolean useAreaInPrefix, Boolean penetrationProtect, Duration penetrationProtectTimeout) {
        QuickConfig.Builder builder = StringUtils.isEmpty(area) ? QuickConfig.newBuilder(name) : QuickConfig.newBuilder(area, name);
        builder.cacheType(cacheType);
        builder.expire(expire);
        if (cacheType == CacheType.BOTH) {
            builder.syncLocal(syncLocal);
        }

        builder.localExpire(localExpire);
        builder.localLimit(localLimit);
        builder.cacheNullValue(cacheNullValue);
        builder.useAreaInPrefix(useAreaInPrefix);
        if (ObjectUtils.isNotEmpty(penetrationProtect)) {
            builder.penetrationProtect(penetrationProtect);
            if (BooleanUtils.isTrue(penetrationProtect) && ObjectUtils.isNotEmpty(penetrationProtectTimeout)) {
                builder.penetrationProtectTimeout(penetrationProtectTimeout);
            }
        }

        QuickConfig quickConfig = builder.build();
        return this.create(quickConfig);
    }

    private <K, V> Cache<K, V> create(QuickConfig quickConfig) {
        return this.cacheManager.getOrCreateCache(quickConfig);
    }
}
