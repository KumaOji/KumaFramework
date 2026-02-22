/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.security.spring.core.jwt.model;

import java.io.Serializable;

public class JwtUserInfo
implements Serializable {
    private Long userId;
    private String account;
    private String name;

    public JwtUserInfo() {
    }

    public JwtUserInfo(Long userId, String account, String name) {
        this.userId = userId;
        this.account = account;
        this.name = name;
    }

    public Long getUserId() {
        return this.userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getAccount() {
        return this.account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }
}

