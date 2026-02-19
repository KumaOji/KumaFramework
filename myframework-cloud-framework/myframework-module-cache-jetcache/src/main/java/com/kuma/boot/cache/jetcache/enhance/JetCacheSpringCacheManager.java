//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.kuma.boot.cache.jetcache.enhance;

import com.alicp.jetcache.Cache;
import com.kuma.boot.common.constant.SymbolConstants;
import com.kuma.boot.common.utils.lang.StringUtils;
import java.time.Duration;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.CacheManager;

public class JetCacheSpringCacheManager implements CacheManager {
    private static final Logger log = LoggerFactory.getLogger(JetCacheSpringCacheManager.class);
    private boolean dynamic = true;
    private boolean allowNullValues = true;
    private final Map<String, org.springframework.cache.Cache> cacheMap = new ConcurrentHashMap(16);
    private final JetCacheCreateCacheFactory jetCacheCreateCacheFactory;

    public JetCacheSpringCacheManager(JetCacheCreateCacheFactory jetCacheCreateCacheFactory) {
        this.jetCacheCreateCacheFactory = jetCacheCreateCacheFactory;
    }

    public JetCacheSpringCacheManager(JetCacheCreateCacheFactory jetCacheCreateCacheFactory, String... cacheNames) {
        this.jetCacheCreateCacheFactory = jetCacheCreateCacheFactory;
        this.setCacheNames(Arrays.asList(cacheNames));
    }

    public void setAllowNullValues(boolean allowNullValues) {
        this.allowNullValues = allowNullValues;
    }

    public boolean isAllowNullValues() {
        return this.allowNullValues;
    }

    private void setCacheNames(@Nullable Collection<String> cacheNames) {
        if (cacheNames != null) {
            for(String name : cacheNames) {
                this.cacheMap.put(name, this.createJetCache(name));
            }

            this.dynamic = false;
        } else {
            this.dynamic = true;
        }

    }

    protected org.springframework.cache.Cache createJetCache(String name) {
        Cache<Object, Object> cache = this.jetCacheCreateCacheFactory.create(name);
        log.debug("[kmc] |- CACHE - Herodotus cache [{}] is CREATED.", name);
        return new JetCacheSpringCache(name, cache, this.allowNullValues);
    }

    protected org.springframework.cache.Cache createJetCache(String name, Duration expire) {
        Cache<Object, Object> cache = this.jetCacheCreateCacheFactory.create(name, expire, this.allowNullValues, true);
        log.debug("[kmc] |- CACHE - Herodotus cache [{}] with expire is CREATED.", name);
        return new JetCacheSpringCache(name, cache, this.allowNullValues);
    }

    private String availableCacheName(String name) {
        return StringUtils.endWith(name, SymbolConstants.COLON) ? name : name + SymbolConstants.COLON;
    }

    public org.springframework.cache.@Nullable Cache getCache(String name) {
        String usedName = this.availableCacheName(name);
        return (org.springframework.cache.Cache)this.cacheMap.computeIfAbsent(usedName, (cacheName) -> this.dynamic ? this.createJetCache(cacheName) : null);
    }

    public Collection<String> getCacheNames() {
        return Collections.unmodifiableSet(this.cacheMap.keySet());
    }
}
