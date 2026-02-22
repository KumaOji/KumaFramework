/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.security.spring.authentication.login.extension.wechatminiapp.service;

import com.kuma.boot.security.spring.authentication.login.extension.wechatminiapp.client.WechatMiniAppClient;

@FunctionalInterface
public interface WechatMiniAppClientService {
    public WechatMiniAppClient get(String var1);
}

