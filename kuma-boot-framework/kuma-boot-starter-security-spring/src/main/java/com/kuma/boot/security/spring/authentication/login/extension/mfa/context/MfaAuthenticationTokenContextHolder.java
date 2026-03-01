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

package com.kuma.boot.security.spring.authentication.login.extension.mfa.context;

/**
 * A holder of the {@link MfaTokenContext} that associates it with the current thread using a {@code ThreadLocal}.
 *
 * @author: ReLive27
 * @since: 2023/1/7 23:20
 */
public class MfaAuthenticationTokenContextHolder {
    private static final ThreadLocal<MfaTokenContext> holder = new ThreadLocal<>();

    private MfaAuthenticationTokenContextHolder() {}

    /**
     * Returns the {@link MfaTokenContext} bound to the current thread.
     *
     * @return
     */
    public static MfaTokenContext getMfaTokenContext() {
        return holder.get();
    }

    /**
     * Bind the given {@link MfaTokenContext} to the current thread.
     *
     * @param tokenContext
     */
    public static void setMfaTokenContext(MfaTokenContext tokenContext) {
        if (tokenContext == null) {
            resetMfaTokenContext();
        } else {
            holder.set(tokenContext);
        }
    }

    /**
     * Reset the {@link MfaTokenContext} bound to the current thread.
     */
    public static void resetMfaTokenContext() {
        holder.remove();
    }
}
