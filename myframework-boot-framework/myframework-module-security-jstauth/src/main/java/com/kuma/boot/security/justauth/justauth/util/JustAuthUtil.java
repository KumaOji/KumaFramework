/*
 * Copyright (c) 2020-2030, kuma (2569277704@qq.com & https://blog.kumacloud.top/).
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

package com.kuma.boot.security.justauth.justauth.util;

import com.kuma.boot.security.justauth.justauth.AuthTokenPo;
import com.kuma.boot.security.justauth.justauth.ConnectionData;
import com.kuma.boot.security.justauth.justauth.request.Auth2DefaultRequest;
import com.xkcoding.http.config.HttpConfig;
import me.zhyd.oauth.model.AuthToken;
import me.zhyd.oauth.model.AuthUser;
import org.springframework.beans.BeanUtils;

/**
 * JustAuth util
 * @author YongWu zheng
 * @version V1.0  Created by 2020/10/6 22:00
 */
public class JustAuthUtil {

    /**
     * 根据传入的参数生成 {@link AuthTokenPo}
     * @param token         {@link AuthToken}
     * @param providerId    第三方服务商 ID, 如: qq, github
     * @param timeout       {@link HttpConfig#getTimeout()}
     * @return {@link AuthTokenPo}
     */
    public static AuthTokenPo getAuthTokenPo(AuthToken token, String providerId, Integer timeout) {
        AuthTokenPo authToken = new AuthTokenPo();
        BeanUtils.copyProperties(token, authToken);
        authToken.setProviderId(providerId);
        // 有效期转时间戳
        Auth2DefaultRequest.expireIn2Timestamp(timeout, token.getExpireIn(), authToken);
        return authToken;
    }

    /**
     * 根据传入的参数生成 {@link ConnectionData}
     * @param providerId    第三方服务商 ID, 如: qq, github
     * @param authUser      {@link AuthUser}
     * @param userId        本地账户用户 Id
     * @param authToken     {@link AuthTokenPo}
     */
    public static ConnectionData getConnectionData(
            String providerId, AuthUser authUser, String userId, AuthTokenPo authToken) {
        // @formatter:off
        return ConnectionData.builder()
                .userId(userId)
                .displayName(authUser.getUsername())
                .imageUrl(authUser.getAvatar())
                .profileUrl(authUser.getBlog())
                .providerId(providerId)
                .providerUserId(authUser.getUuid())
                .accessToken(authToken.getAccessToken())
                .tokenId(authToken.getId())
                .refreshToken(authToken.getRefreshToken())
                .expireTime(authToken.getExpireTime())
                .build();
        // @formatter:on
    }
}
