/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.boot.context.properties.ConfigurationProperties
 */
package com.kuma.boot.security.spring.core.jwt;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix="kuma.boot.security.authentication")
public class JwtProperties {
    public static final String PREFIX = "kuma.boot.security.authentication";
    private Long expire = 7200L;
    private Long refreshExpire = 28800L;
    private Long allowedClockSkewSeconds = 60L;

    public JwtProperties() {
    }

    public JwtProperties(Long expire, Long refreshExpire, Long allowedClockSkewSeconds) {
        this.expire = expire;
        this.refreshExpire = refreshExpire;
        this.allowedClockSkewSeconds = allowedClockSkewSeconds;
    }

    public Long getExpire() {
        return this.expire;
    }

    public void setExpire(Long expire) {
        this.expire = expire;
    }

    public Long getRefreshExpire() {
        return this.refreshExpire;
    }

    public void setRefreshExpire(Long refreshExpire) {
        this.refreshExpire = refreshExpire;
    }

    public Long getAllowedClockSkewSeconds() {
        return this.allowedClockSkewSeconds;
    }

    public void setAllowedClockSkewSeconds(Long allowedClockSkewSeconds) {
        this.allowedClockSkewSeconds = allowedClockSkewSeconds;
    }
}

