/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.collections4.MapUtils
 *  org.dromara.hutool.core.cache.Cache
 *  org.dromara.hutool.core.cache.impl.LRUCache
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.kuma.boot.security.spring.authorization;

import com.kuma.boot.security.spring.access.security.SecurityConfigAttribute;
import com.kuma.boot.security.spring.access.security.SecurityRequest;
import com.kuma.boot.security.spring.access.security.SecurityRequestMatcher;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.collections4.MapUtils;
import org.dromara.hutool.core.cache.Cache;
import org.dromara.hutool.core.cache.impl.LRUCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SecurityMetadataSourceStorage {
    private static final Logger log = LoggerFactory.getLogger(SecurityMetadataSourceStorage.class);
    private final Cache<String, LinkedHashMap<SecurityRequest, List<SecurityConfigAttribute>>> compatible = new LRUCache(100);
    private final Cache<SecurityRequest, List<SecurityConfigAttribute>> indexable = new LRUCache(100);
    private static final String KEY_COMPATIBLE = "COMPATIBLE";

    private LinkedHashMap<SecurityRequest, List<SecurityConfigAttribute>> readFromCompatible() {
        LinkedHashMap compatible = (LinkedHashMap)this.compatible.get((Object)KEY_COMPATIBLE);
        if (MapUtils.isNotEmpty((Map)compatible)) {
            return compatible;
        }
        return new LinkedHashMap<SecurityRequest, List<SecurityConfigAttribute>>();
    }

    private void writeToCompatible(LinkedHashMap<SecurityRequest, List<SecurityConfigAttribute>> compatible) {
        this.compatible.put((Object)KEY_COMPATIBLE, compatible);
    }

    private List<SecurityConfigAttribute> readFromIndexable(SecurityRequest securityRequest) {
        return (List)this.indexable.get((Object)securityRequest);
    }

    private void writeToIndexable(SecurityRequest securityRequest, List<SecurityConfigAttribute> configAttributes) {
        this.indexable.put((Object)securityRequest, configAttributes);
    }

    public List<SecurityConfigAttribute> getConfigAttribute(String url, String method) {
        SecurityRequest securityRequest = new SecurityRequest(url, method);
        return this.readFromIndexable(securityRequest);
    }

    public LinkedHashMap<SecurityRequest, List<SecurityConfigAttribute>> getCompatible() {
        return this.readFromCompatible();
    }

    private void appendToCompatible(SecurityRequest securityRequest, List<SecurityConfigAttribute> configAttributes) {
        LinkedHashMap<SecurityRequest, List<SecurityConfigAttribute>> compatible = this.getCompatible();
        compatible.put(securityRequest, configAttributes);
        log.info("Append [{}] to Compatible cache, current size is [{}]", (Object)securityRequest, (Object)compatible.size());
        this.writeToCompatible(compatible);
    }

    private void appendToCompatible(LinkedHashMap<SecurityRequest, List<SecurityConfigAttribute>> configAttributes) {
        configAttributes.forEach(this::appendToCompatible);
    }

    private void appendToIndexable(SecurityRequest securityRequest, List<SecurityConfigAttribute> configAttributes) {
        this.writeToIndexable(securityRequest, configAttributes);
    }

    private void appendToIndexable(LinkedHashMap<SecurityRequest, List<SecurityConfigAttribute>> configAttributes) {
        configAttributes.forEach(this::appendToIndexable);
    }

    public void addToStorage(LinkedHashMap<SecurityRequest, List<SecurityConfigAttribute>> configAttributes, boolean isIndexable) {
        if (MapUtils.isNotEmpty(configAttributes)) {
            if (isIndexable) {
                this.appendToIndexable(configAttributes);
            } else {
                this.appendToCompatible(configAttributes);
            }
        }
    }

    public void addToStorage(LinkedHashMap<SecurityRequest, List<SecurityConfigAttribute>> matchers, LinkedHashMap<SecurityRequest, List<SecurityConfigAttribute>> configAttributes, boolean isIndexable) {
        LinkedHashMap<SecurityRequest, List<SecurityConfigAttribute>> result = new LinkedHashMap<SecurityRequest, List<SecurityConfigAttribute>>();
        if (MapUtils.isNotEmpty(matchers) && MapUtils.isNotEmpty(configAttributes)) {
            result = this.checkConflict(matchers, configAttributes);
        }
        this.addToStorage(result, isIndexable);
    }

    private LinkedHashMap<SecurityRequest, List<SecurityConfigAttribute>> checkConflict(LinkedHashMap<SecurityRequest, List<SecurityConfigAttribute>> matchers, LinkedHashMap<SecurityRequest, List<SecurityConfigAttribute>> configAttributes) {
        LinkedHashMap<SecurityRequest, List<SecurityConfigAttribute>> result = new LinkedHashMap<SecurityRequest, List<SecurityConfigAttribute>>(configAttributes);
        for (SecurityRequest matcher : matchers.keySet()) {
            Iterator<SecurityRequest> iterator = configAttributes.keySet().iterator();
            while (iterator.hasNext()) {
                SecurityRequestMatcher requestMatcher = new SecurityRequestMatcher(matcher);
                SecurityRequest item = iterator.next();
                if (!requestMatcher.matches(item)) continue;
                result.remove(item);
                log.info("Pattern [{}] is conflict with [{}], so remove it.", (Object)item.getPattern(), (Object)matcher.getPattern());
            }
        }
        return result;
    }
}

