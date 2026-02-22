/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.security.spring.core.jwt.model;

import java.io.Serializable;
import java.time.LocalDateTime;

public class Token
implements Serializable {
    private static final long serialVersionUID = -8482946147572784305L;
    private String token;
    private Long expire;
    private LocalDateTime expiration;

    public Token() {
    }

    public Token(String token, Long expire, LocalDateTime expiration) {
        this.token = token;
        this.expire = expire;
        this.expiration = expiration;
    }

    public String getToken() {
        return this.token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Long getExpire() {
        return this.expire;
    }

    public void setExpire(Long expire) {
        this.expire = expire;
    }

    public LocalDateTime getExpiration() {
        return this.expiration;
    }

    public void setExpiration(LocalDateTime expiration) {
        this.expiration = expiration;
    }
}

