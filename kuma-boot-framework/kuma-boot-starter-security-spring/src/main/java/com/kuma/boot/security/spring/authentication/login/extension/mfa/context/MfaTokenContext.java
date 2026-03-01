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
 * @author: ReLive27
 * @since: 2023/1/7 23:20
 */
public final class MfaTokenContext {
    private final boolean mfa;

    public MfaTokenContext(boolean mfa) {
        this.mfa = mfa;
    }

    public boolean isMfa() {
        return this.mfa;
    }
}
