/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  org.springframework.boot.context.properties.ConfigurationProperties
 *  org.springframework.cloud.context.config.annotation.RefreshScope
 */
package com.kuma.boot.cache.redis.autoconfigure.properties;

import com.kuma.boot.cache.redis.enums.CacheType;
import com.kuma.boot.cache.redis.enums.SerializerType;
import java.time.Duration;
import java.util.Map;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;

@RefreshScope
@ConfigurationProperties(prefix="kuma.boot.cache.redis.cache-manager")
public class CacheManagerProperties {
    public static final String PREFIX = "kuma.boot.cache.redis.cache-manager";
    private boolean enabled = true;
    private CacheType type = CacheType.REDIS;
    private SerializerType serializerType = SerializerType.JACK_SON;
    private Boolean cacheNullVal = true;
    private Cache def = new Cache();
    private Map<String, Cache> configs;
    private Stream stream = new Stream();

    public CacheType getType() {
        return this.type;
    }

    public void setType(CacheType type) {
        this.type = type;
    }

    public SerializerType getSerializerType() {
        return this.serializerType;
    }

    public void setSerializerType(SerializerType serializerType) {
        this.serializerType = serializerType;
    }

    public Boolean getCacheNullVal() {
        return this.cacheNullVal;
    }

    public void setCacheNullVal(Boolean cacheNullVal) {
        this.cacheNullVal = cacheNullVal;
    }

    public Cache getDef() {
        return this.def;
    }

    public void setDef(Cache def) {
        this.def = def;
    }

    public Map<String, Cache> getConfigs() {
        return this.configs;
    }

    public void setConfigs(Map<String, Cache> configs) {
        this.configs = configs;
    }

    public boolean getEnabled() {
        return this.enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public Stream getStream() {
        return this.stream;
    }

    public void setStream(Stream stream) {
        this.stream = stream;
    }

    public static class Cache {
        private Duration timeToLive = Duration.ofDays(1L);
        private boolean cacheNullValues = true;
        private String keyPrefix;
        private boolean useKeyPrefix = true;
        private int maxSize = 1000;

        public Duration getTimeToLive() {
            return this.timeToLive;
        }

        public void setTimeToLive(Duration timeToLive) {
            this.timeToLive = timeToLive;
        }

        public boolean isCacheNullValues() {
            return this.cacheNullValues;
        }

        public void setCacheNullValues(boolean cacheNullValues) {
            this.cacheNullValues = cacheNullValues;
        }

        public String getKeyPrefix() {
            return this.keyPrefix;
        }

        public void setKeyPrefix(String keyPrefix) {
            this.keyPrefix = keyPrefix;
        }

        public boolean isUseKeyPrefix() {
            return this.useKeyPrefix;
        }

        public void setUseKeyPrefix(boolean useKeyPrefix) {
            this.useKeyPrefix = useKeyPrefix;
        }

        public int getMaxSize() {
            return this.maxSize;
        }

        public void setMaxSize(int maxSize) {
            this.maxSize = maxSize;
        }
    }

    public static class Stream {
        public static final String PREFIX = "kuma.boot.cache.redis.cache-manager.stream";
        boolean enable = false;
        String consumerGroup;
        String consumerName;
        Integer pollBatchSize;
        Duration pollTimeout;

        public boolean isEnable() {
            return this.enable;
        }

        public void setEnable(boolean enable) {
            this.enable = enable;
        }

        public String getConsumerGroup() {
            return this.consumerGroup;
        }

        public void setConsumerGroup(String consumerGroup) {
            this.consumerGroup = consumerGroup;
        }

        public String getConsumerName() {
            return this.consumerName;
        }

        public void setConsumerName(String consumerName) {
            this.consumerName = consumerName;
        }

        public Integer getPollBatchSize() {
            return this.pollBatchSize;
        }

        public void setPollBatchSize(Integer pollBatchSize) {
            this.pollBatchSize = pollBatchSize;
        }

        public Duration getPollTimeout() {
            return this.pollTimeout;
        }

        public void setPollTimeout(Duration pollTimeout) {
            this.pollTimeout = pollTimeout;
        }
    }
}

