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

import com.kuma.boot.security.spring.authentication.login.extension.mfa.exception.MfaAuthenticationException;
import com.kuma.boot.security.spring.authentication.login.extension.mfa.totp.MfaAuthenticationManager;
import com.kuma.boot.security.spring.authentication.login.extension.mfa.userdetails.MfaUserDetails;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.util.Assert;

/**
 * @author: ReLive27
 * @since: 2023/1/7 22:52
 */
public class MfaAuthenticationProvider implements AuthenticationProvider {
    private final MfaAuthenticationManager mfaAuthenticationManager;
    private final UserDetailsService userDetailsService;

    public MfaAuthenticationProvider(
            UserDetailsService userDetailsService,
            MfaAuthenticationManager mfaAuthenticationManager) {
        Assert.notNull(userDetailsService, "userDetailsService cannot be null");
        Assert.notNull(mfaAuthenticationManager, "mfaAuthenticationManager cannot be null");
        this.userDetailsService = userDetailsService;
        this.mfaAuthenticationManager = mfaAuthenticationManager;
    }

    @Override
    public Authentication authenticate(Authentication authentication)
            throws AuthenticationException {
        MfaAuthenticationToken mfaAuthenticationToken = (MfaAuthenticationToken) authentication;

        String username = mfaAuthenticationToken.getPrincipal().toString();
        UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);
        if (userDetails == null) {
            throw new InternalAuthenticationServiceException(
                    "UserDetailsService returned null, which is an interface contract violation");
        }

        if (userDetails instanceof MfaUserDetails) {
            MfaUserDetails mfaUserDetails = (MfaUserDetails) userDetails;
            if (mfaUserDetails.isEnableMfa()) {
                if (!this.mfaAuthenticationManager.validCode(
                        mfaUserDetails.getSecret(), mfaAuthenticationToken.getCredentials())) {

                    throw new MfaAuthenticationException("Code verification failed", null);
                }
            }

            return new MfaAuthenticationToken(
                    username, mfaAuthenticationToken.getCredentials(), true);
        }
        throw new MfaAuthenticationException(
                "MfaUserDetails must be an instance of UserDetails", null);
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return MfaAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
