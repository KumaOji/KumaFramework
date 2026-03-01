//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.kuma.boot.cache.jetcache.utils;

import com.alicp.jetcache.Cache;
import com.alicp.jetcache.anno.CacheType;
import com.kuma.boot.cache.jetcache.enhance.JetCacheCreateCacheFactory;
import java.time.Duration;
import org.apache.commons.lang3.ObjectUtils;

public class JetCacheUtils {
    private static volatile JetCacheUtils instance;
    private JetCacheCreateCacheFactory jetCacheCreateCacheFactory;

    private JetCacheUtils() {
    }

    private void init(JetCacheCreateCacheFactory jetCacheCreateCacheFactory) {
        this.jetCacheCreateCacheFactory = jetCacheCreateCacheFactory;
    }

    private JetCacheCreateCacheFactory getJetCacheCreateCacheFactory() {
        return this.jetCacheCreateCacheFactory;
    }

    public static JetCacheUtils getInstance() {
        if (ObjectUtils.isEmpty(instance)) {
            synchronized(JetCacheUtils.class) {
                if (ObjectUtils.isEmpty(instance)) {
                    instance = new JetCacheUtils();
                }
            }
        }

        return instance;
    }

    public static void setJetCacheCreateCacheFactory(JetCacheCreateCacheFactory jetCacheCreateCacheFactory) {
        getInstance().init(jetCacheCreateCacheFactory);
    }

    public static <K, V> Cache<K, V> create(String name, Duration expire) {
        return create(name, expire, true);
    }

    public static <K, V> Cache<K, V> create(String name, Duration expire, Boolean cacheNullValue) {
        return create(name, (Duration)expire, (Boolean)cacheNullValue, (Boolean)null);
    }

    public static <K, V> Cache<K, V> create(String name, Duration expire, Boolean cacheNullValue, Boolean syncLocal) {
        return create(name, CacheType.BOTH, expire, cacheNullValue, syncLocal);
    }

    public static <K, V> Cache<K, V> create(String name, CacheType cacheType) {
        return create(name, (CacheType)cacheType, (Duration)null);
    }

    public static <K, V> Cache<K, V> create(String name, CacheType cacheType, Duration expire) {
        return create(name, cacheType, expire, true);
    }

    public static <K, V> Cache<K, V> create(String name, CacheType cacheType, Duration expire, Boolean cacheNullValue) {
        return create(name, cacheType, expire, cacheNullValue, (Boolean)null);
    }

    public static <K, V> Cache<K, V> create(String name, CacheType cacheType, Duration expire, Boolean cacheNullValue, Boolean syncLocal) {
        return getInstance().getJetCacheCreateCacheFactory().create(name, cacheType, expire, cacheNullValue, syncLocal);
    }
}
