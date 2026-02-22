/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  com.kuma.boot.common.utils.log.LogUtils
 *  org.apache.commons.codec.digest.DigestUtils
 *  org.springframework.security.core.userdetails.UserDetails
 */
package com.kuma.boot.security.spring.authentication.login.extension.wechatminiapp.service;

import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.boot.security.spring.authentication.login.extension.wechatminiapp.client.WechatMiniAppRequest;
import com.kuma.boot.security.spring.core.userdetails.TtcUser;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.security.core.userdetails.UserDetails;

public class DefaultWechatWechatMiniAppUserDetailsService
implements WechatMiniAppUserDetailsService {
    @Override
    public UserDetails register(WechatMiniAppRequest request, String sessionKey) {
        LogUtils.info((String)"WechatMiniAppRequest: {}", (Object[])new Object[]{request});
        String signature = DigestUtils.sha1Hex((String)(request.getRawData() + sessionKey));
        if (!request.getSignature().equals(signature)) {
            throw new RuntimeException("\u6570\u5b57\u7b7e\u540d\u9a8c\u8bc1\u5931\u8d25");
        }
        return new TtcUser();
    }

    @Override
    public UserDetails loadByOpenId(String clientId, String openId) {
        LogUtils.info((String)clientId, (Object[])new Object[0]);
        LogUtils.info((String)openId, (Object[])new Object[0]);
        return null;
    }
}

