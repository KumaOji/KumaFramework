/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.security.spring.authentication.login.extension.wechatminiapp.service;

import com.kuma.boot.security.spring.authentication.login.extension.wechatminiapp.client.WechatMiniAppClient;

public class DefaultWechatWechatMiniAppClientService
implements WechatMiniAppClientService {
    @Override
    public WechatMiniAppClient get(String clientId) {
        WechatMiniAppClient wechatMiniAppClient = new WechatMiniAppClient();
        wechatMiniAppClient.setClientId(clientId);
        wechatMiniAppClient.setAppId("wxcd395c35c45eb823");
        wechatMiniAppClient.setSecret("75f9a12c82bd24ecac0d37bf1156c749");
        return wechatMiniAppClient;
    }
}

