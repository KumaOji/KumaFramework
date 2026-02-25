/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.ratelimit.ratelimitbs.extend.cache;

import java.io.Serializable;

public class CommonCacheValueDto
implements Serializable {
    private String value;
    private Long expireTime;

    public static CommonCacheValueDto of(String value, Long expireTime) {
        CommonCacheValueDto dto = new CommonCacheValueDto();
        dto.setValue(value);
        if (expireTime != null && expireTime > 0L) {
            dto.setExpireTime(expireTime);
        }
        return dto;
    }

    public String getValue() {
        return this.value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Long getExpireTime() {
        return this.expireTime;
    }

    public void setExpireTime(Long expireTime) {
        this.expireTime = expireTime;
    }

    public String toString() {
        return "CommonCacheValueDto{value='" + this.value + "', expireTime=" + this.expireTime + "}";
    }
}

