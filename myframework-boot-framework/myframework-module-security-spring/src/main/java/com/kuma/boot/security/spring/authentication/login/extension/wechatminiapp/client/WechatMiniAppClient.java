/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.security.spring.authentication.login.extension.wechatminiapp.client;

public class WechatMiniAppClient {
    private String clientId;
    private String appId;
    private String secret;

    public String getClientId() {
        return this.clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getAppId() {
        return this.appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getSecret() {
        return this.secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }
}

