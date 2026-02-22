/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.boot.context.properties.ConfigurationProperties
 */
package com.kuma.boot.security.spring.authentication.login.social.justauth.properties;

import java.time.Duration;
import java.util.HashSet;
import java.util.Set;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(value="ums.cache.redis")
public class RedisCacheProperties {
    private final Cache cache = new Cache();
    private Boolean open = false;
    private Boolean useIocRedisConnectionFactory = false;

    public Cache getCache() {
        return this.cache;
    }

    public Boolean getOpen() {
        return this.open;
    }

    public void setOpen(Boolean open) {
        this.open = open;
    }

    public Boolean getUseIocRedisConnectionFactory() {
        return this.useIocRedisConnectionFactory;
    }

    public void setUseIocRedisConnectionFactory(Boolean useIocRedisConnectionFactory) {
        this.useIocRedisConnectionFactory = useIocRedisConnectionFactory;
    }

    public static class Cache {
        private Integer databaseIndex = 0;
        private Duration defaultExpireTime = Duration.ofSeconds(200L);
        private Duration entryTtl = Duration.ofSeconds(180L);
        private Set<String> cacheNames = new HashSet<String>();

        public Integer getDatabaseIndex() {
            return this.databaseIndex;
        }

        public void setDatabaseIndex(Integer databaseIndex) {
            this.databaseIndex = databaseIndex;
        }

        public Duration getDefaultExpireTime() {
            return this.defaultExpireTime;
        }

        public void setDefaultExpireTime(Duration defaultExpireTime) {
            this.defaultExpireTime = defaultExpireTime;
        }

        public Duration getEntryTtl() {
            return this.entryTtl;
        }

        public void setEntryTtl(Duration entryTtl) {
            this.entryTtl = entryTtl;
        }

        public Set<String> getCacheNames() {
            return this.cacheNames;
        }

        public void setCacheNames(Set<String> cacheNames) {
            this.cacheNames = cacheNames;
        }
    }
}

