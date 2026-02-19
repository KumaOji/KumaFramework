//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.kuma.boot.cache.jetcache.enhance;

import com.kuma.boot.cache.jetcache.autoconfigure.properties.Expire;
import com.kuma.boot.cache.jetcache.autoconfigure.properties.JetCacheProperties;
import com.kuma.boot.common.constant.SymbolConstants;
import com.kuma.boot.common.utils.lang.StringUtils;
import java.util.Map;
import org.apache.commons.collections4.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.Cache;

public class HerodotusCacheManager extends JetCacheSpringCacheManager {
    private static final Logger log = LoggerFactory.getLogger(HerodotusCacheManager.class);
    private final JetCacheProperties cacheProperties;

    public HerodotusCacheManager(JetCacheCreateCacheFactory jetCacheCreateCacheFactory, JetCacheProperties cacheProperties) {
        super(jetCacheCreateCacheFactory);
        this.cacheProperties = cacheProperties;
        this.setAllowNullValues(cacheProperties.getAllowNullValues());
    }

    public HerodotusCacheManager(JetCacheCreateCacheFactory jetCacheCreateCacheFactory, JetCacheProperties cacheProperties, String... cacheNames) {
        super(jetCacheCreateCacheFactory, cacheNames);
        this.cacheProperties = cacheProperties;
    }

    protected Cache createJetCache(String name) {
        Map<String, Expire> expires = this.cacheProperties.getExpires();
        if (MapUtils.isNotEmpty(expires)) {
            String key = StringUtils.replace(name, SymbolConstants.COLON, this.cacheProperties.getSeparator());
            if (expires.containsKey(key)) {
                Expire expire = (Expire)expires.get(key);
                log.debug("[kmc] |- CACHE - Cache [{}] is set to use CUSTOM expire.", name);
                return super.createJetCache(name, expire.getTtl());
            }
        }

        return super.createJetCache(name);
    }
}
