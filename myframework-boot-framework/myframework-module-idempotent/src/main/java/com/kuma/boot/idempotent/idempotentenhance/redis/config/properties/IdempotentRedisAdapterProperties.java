/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.kuma.boot.common.utils.lang.StringUtils
 *  org.springframework.boot.context.properties.ConfigurationProperties
 */
package com.kuma.boot.idempotent.idempotentenhance.redis.config.properties;

import com.kuma.boot.common.utils.lang.StringUtils;
import java.util.concurrent.TimeUnit;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix="enhance.idempotent.adapter.redis")
public class IdempotentRedisAdapterProperties {
    public static final String PREFIX = "enhance.idempotent.adapter.redis";
    private long expireTime = -1L;
    private TimeUnit unit = TimeUnit.SECONDS;
    private String keyPrefix = "idempotent:";
    private String dataKeyPrefix = StringUtils.endWith((CharSequence)this.keyPrefix, (CharSequence)":") ? this.keyPrefix + "data:" : this.keyPrefix + ":data:";

    public long getExpireTime() {
        return this.expireTime;
    }

    public void setExpireTime(long expireTime) {
        this.expireTime = expireTime;
    }

    public TimeUnit getUnit() {
        return this.unit;
    }

    public void setUnit(TimeUnit unit) {
        this.unit = unit;
    }

    public String getKeyPrefix() {
        return this.keyPrefix;
    }

    public void setKeyPrefix(String keyPrefix) {
        this.keyPrefix = keyPrefix;
    }

    public String getDataKeyPrefix() {
        return this.dataKeyPrefix;
    }

    public void setDataKeyPrefix(String dataKeyPrefix) {
        this.dataKeyPrefix = dataKeyPrefix;
    }
}

