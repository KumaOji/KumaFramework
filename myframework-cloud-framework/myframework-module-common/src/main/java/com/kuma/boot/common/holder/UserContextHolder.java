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

package com.kuma.boot.common.holder;

import com.alibaba.ttl.TransmittableThreadLocal;
import com.kuma.boot.common.model.BaseSecurityUser;

/**
 * 负载均衡规则Holder
 *
 * @author kuma
 * @version 2021.9
 * @since 2021-09-02 19:34:15
 */
public class UserContextHolder {

    private UserContextHolder() {}

    /**
     * VERSION_CONTEXT
     */
    private static final ThreadLocal<BaseSecurityUser> LOGIN_USER_CONTEXT =
            new TransmittableThreadLocal<>();

    /**
     * setVersion
     * @param user version
     * @since 2021-09-02 19:34:26
     */
    public static void setUser(BaseSecurityUser user) {
        LOGIN_USER_CONTEXT.set(user);
    }

    /**
     * getVersion
     * @return {@link String }
     * @since 2021-09-02 19:34:28
     */
    public static BaseSecurityUser getUser() {
        return LOGIN_USER_CONTEXT.get();
    }

    public static Long getUserId() {
        return LOGIN_USER_CONTEXT.get().getUserId();
    }

    public static String getUserName() {
        return LOGIN_USER_CONTEXT.get().getUsername();
    }

    /**
     * clear
     *
     * @since 2021-09-02 19:34:32
     */
    public static void clear() {
        LOGIN_USER_CONTEXT.remove();
    }
}
