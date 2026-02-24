/*
 * Copyright (c) 2020-2030, Kuma (2569277704@qq.com & https://blog.kumacloud.top/).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.kuma.boot.security.spring.authentication.login.extension.wechatminiapp.service;

import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.boot.security.spring.authentication.login.extension.wechatminiapp.client.WechatMiniAppRequest;
import com.kuma.boot.security.spring.core.userdetails.KmcUser;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.security.core.userdetails.UserDetails;

/** // 小程序用户 自动注册和检索 1 在此处配置 优先级最高 2 注册为Spring Bean 可以免配置 */
public class DefaultWechatWechatMiniAppUserDetailsService
        implements WechatMiniAppUserDetailsService {

    //    @Autowired
    //    private IFeignUserApi userApi;
    //
    //    @Autowired
    //    private IFeignMemberApi memberApi;

    @Override
    public UserDetails register(WechatMiniAppRequest request, String sessionKey) {
        LogUtils.info("WechatMiniAppRequest: {}", request);

        String signature = DigestUtils.sha1Hex(request.getRawData() + sessionKey);
        if (!request.getSignature().equals(signature)) {
            throw new RuntimeException("数字签名验证失败");
        }

        //        String encryptedData = request.getEncryptedData();
        //        String iv = request.getIv();
        //
        //        // 解密encryptedData数据
        //        String decrypt = WxUtils.decrypt(sessionKey, iv, encryptedData);
        //        WechatMiniAppUserInfo wechatMiniAppUserInfo = JsonUtils.toObject(decrypt,
        // WechatMiniAppUserInfo.class);
        //        wechatMiniAppUserInfo.setSessionKey(sessionKey);
        //
        //        LogUtils.info("wechatMiniAppUserInfo: {}",wechatMiniAppUserInfo);

        // 调用数据库 微信小程序用户注册

        // 模拟
        return new KmcUser();
    }

    @Override
    public UserDetails loadByOpenId(String clientId, String openId) {
        LogUtils.info(clientId);
        LogUtils.info(openId);

        // 模拟 根据openid 查询 小程序用户信息
        return null;
    }
}
