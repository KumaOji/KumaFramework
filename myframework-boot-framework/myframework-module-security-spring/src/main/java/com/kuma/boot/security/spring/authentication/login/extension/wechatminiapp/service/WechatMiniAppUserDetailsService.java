/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.security.core.userdetails.UserDetails
 */
package com.kuma.boot.security.spring.authentication.login.extension.wechatminiapp.service;

import com.kuma.boot.security.spring.authentication.login.extension.wechatminiapp.client.WechatMiniAppRequest;
import org.springframework.security.core.userdetails.UserDetails;

public interface WechatMiniAppUserDetailsService {
    public UserDetails register(WechatMiniAppRequest var1, String var2);

    public UserDetails loadByOpenId(String var1, String var2);
}

