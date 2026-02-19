/*
 * Copyright (c) 2020-2030, Shuigedeng (2569277704@qq.com & https://blog.kumacloud.top/).
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

package com.kuma.boot.data.mybatis.interceptor.easylog.common.model;

import java.io.Serializable;

/**
 * UserCache
 *
 * @author kuma
 * @version 2026.01
 * @since 2025-12-19 09:30:45
 */
public class UserCache implements Serializable {

    /**
     * 透传用户信息 操作用户ID
     */
    private String userId;

    /**
     * 操作用户名
     */
    private String userName;

    /**
     * 请求ID
     */
    private String requestId;

    public UserCache() {
    }

    public UserCache( String userId, String userName, String requestId ) {
        this.userId = userId;
        this.userName = userName;
        this.requestId = requestId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId( String userId ) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName( String userName ) {
        this.userName = userName;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId( String requestId ) {
        this.requestId = requestId;
    }
}
