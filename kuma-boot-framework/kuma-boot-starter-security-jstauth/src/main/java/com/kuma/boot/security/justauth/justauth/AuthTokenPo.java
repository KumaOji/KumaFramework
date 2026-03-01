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

package com.kuma.boot.security.justauth.justauth;

import java.io.Serial;
import me.zhyd.oauth.model.AuthToken;

/**
 * {@link AuthToken} 持久化 PO
 * @author YongWu zheng
 * @version V2.0  Created by 2020/10/10 14:10
 */
public class AuthTokenPo extends AuthToken {
    @Serial private static final long serialVersionUID = -295423281641462728L;

    /**
     * tokenId
     */
    private Long id;

    /**
     * 第三方服务商(如: qq,github)
     */
    private String providerId;

    /**
     * 过期日期, 基于 1970-01-01T00:00:00Z, 无过期时间默认为 -1
     */
    private Long expireTime;

    /**
     * 是否支持 refreshToken, 默认: {@code EnableRefresh.YES}. 数据库存储 int 值:1 表示支持, 0 表示不支持
     */
    private EnableRefresh enableRefresh = EnableRefresh.YES;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getProviderId() {
        return providerId;
    }

    public void setProviderId(String providerId) {
        this.providerId = providerId;
    }

    public Long getExpireTime() {
        return expireTime;
    }

    public void setExpireTime(Long expireTime) {
        this.expireTime = expireTime;
    }

    public EnableRefresh getEnableRefresh() {
        return enableRefresh;
    }

    public void setEnableRefresh(EnableRefresh enableRefresh) {
        this.enableRefresh = enableRefresh;
    }
}
