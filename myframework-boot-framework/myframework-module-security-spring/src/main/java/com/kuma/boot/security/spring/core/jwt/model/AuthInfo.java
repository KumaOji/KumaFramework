/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.security.spring.core.jwt.model;

import java.time.LocalDateTime;

public class AuthInfo {
    private String token;
    private String tokenType;
    private String refreshToken;
    private String name;
    private String account;
    private String avatar;
    private String workDescribe;
    private Long userId;
    private long expire;
    private LocalDateTime expiration;
    private Long expireMillis;

    public AuthInfo() {
    }

    public AuthInfo(String token, String tokenType, String refreshToken, String name, String account, String avatar, String workDescribe, Long userId, long expire, LocalDateTime expiration, Long expireMillis) {
        this.token = token;
        this.tokenType = tokenType;
        this.refreshToken = refreshToken;
        this.name = name;
        this.account = account;
        this.avatar = avatar;
        this.workDescribe = workDescribe;
        this.userId = userId;
        this.expire = expire;
        this.expiration = expiration;
        this.expireMillis = expireMillis;
    }

    public String getToken() {
        return this.token;
    }

    public AuthInfo setToken(String token) {
        this.token = token;
        return this;
    }

    public String getTokenType() {
        return this.tokenType;
    }

    public AuthInfo setTokenType(String tokenType) {
        this.tokenType = tokenType;
        return this;
    }

    public String getRefreshToken() {
        return this.refreshToken;
    }

    public AuthInfo setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
        return this;
    }

    public String getName() {
        return this.name;
    }

    public AuthInfo setName(String name) {
        this.name = name;
        return this;
    }

    public String getAccount() {
        return this.account;
    }

    public AuthInfo setAccount(String account) {
        this.account = account;
        return this;
    }

    public String getAvatar() {
        return this.avatar;
    }

    public AuthInfo setAvatar(String avatar) {
        this.avatar = avatar;
        return this;
    }

    public String getWorkDescribe() {
        return this.workDescribe;
    }

    public AuthInfo setWorkDescribe(String workDescribe) {
        this.workDescribe = workDescribe;
        return this;
    }

    public Long getUserId() {
        return this.userId;
    }

    public AuthInfo setUserId(Long userId) {
        this.userId = userId;
        return this;
    }

    public long getExpire() {
        return this.expire;
    }

    public AuthInfo setExpire(long expire) {
        this.expire = expire;
        return this;
    }

    public LocalDateTime getExpiration() {
        return this.expiration;
    }

    public AuthInfo setExpiration(LocalDateTime expiration) {
        this.expiration = expiration;
        return this;
    }

    public Long getExpireMillis() {
        return this.expireMillis;
    }

    public AuthInfo setExpireMillis(Long expireMillis) {
        this.expireMillis = expireMillis;
        return this;
    }
}

