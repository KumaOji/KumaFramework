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

package com.kuma.boot.security.spring.authentication.login.extension.mfa.authentication;

import java.util.Collection;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

/**
 * @author: ReLive27
 * @since: 2023/1/8 21:13
 */
public class MfaAuthenticationToken extends AbstractAuthenticationToken {
    private final Object principal;
    private final String credentials;
    private boolean mfa;

    public MfaAuthenticationToken(Object principal, String credentials) {
        super((Collection<? extends GrantedAuthority>) null);
        this.principal = principal;
        this.credentials = credentials;
        this.setAuthenticated(false);
    }

    public MfaAuthenticationToken(Object principal, String credentials, boolean mfa) {
        super((Collection<? extends GrantedAuthority>) null);
        this.principal = principal;
        this.credentials = credentials;
        this.mfa = mfa;
    }

    @Override
    public String getCredentials() {
        return this.credentials;
    }

    @Override
    public Object getPrincipal() {
        return this.principal;
    }

    public boolean isMfa() {
        return mfa;
    }
}
