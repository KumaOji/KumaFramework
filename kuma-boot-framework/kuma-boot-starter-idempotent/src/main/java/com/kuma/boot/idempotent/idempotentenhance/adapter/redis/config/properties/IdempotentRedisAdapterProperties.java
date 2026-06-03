package com.kuma.boot.idempotent.idempotentenhance.adapter.redis.config.properties;

import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.concurrent.TimeUnit;

/**
 * properties
 *
 * @author wenpanfeng 2023/01/05 12:32
 */
@ConfigurationProperties(prefix = IdempotentRedisAdapterProperties.PREFIX)
public class IdempotentRedisAdapterProperties {

    public static final String PREFIX = "enhance.idempotent.adapter.redis";

    /**
     * 幂等记录过期时间，默认-1 表示永不过期
     */
    private long expireTime = -1;

    /**
     * 过期时间单位，默认：秒
     */
    private TimeUnit unit = TimeUnit.SECONDS;

    /**
     * 幂等记录key前缀（默认：idempotent:）
     */
    private String keyPrefix = "idempotent:";

    /**
     * 幂等记录key前缀(默认是keyPrefix 拼接上data字符串)
     */
    private String dataKeyPrefix = StringUtils.endsWith(keyPrefix, ":") ? keyPrefix + "data:" : keyPrefix + ":data:";

    public long getExpireTime() {
        return expireTime;
    }

    public void setExpireTime( long expireTime ) {
        this.expireTime = expireTime;
    }

    public TimeUnit getUnit() {
        return unit;
    }

    public void setUnit( TimeUnit unit ) {
        this.unit = unit;
    }

    public String getKeyPrefix() {
        return keyPrefix;
    }

    public void setKeyPrefix( String keyPrefix ) {
        this.keyPrefix = keyPrefix;
    }

    public String getDataKeyPrefix() {
        return dataKeyPrefix;
    }

    public void setDataKeyPrefix( String dataKeyPrefix ) {
        this.dataKeyPrefix = dataKeyPrefix;
    }
}
