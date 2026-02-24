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

package com.kuma.boot.security.spring.authentication.login.extension.mfa.userdetails;

import com.kuma.boot.common.utils.log.LogUtils;
import java.util.HashMap;
import java.util.Map;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsPasswordService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.util.Assert;

/**
 * @author: ReLive27
 * @since: 2023/2/3 19:58
 */
public class InMemoryMfaUserDetailsManager
        implements UserDetailsManager, UserDetailsPasswordService {
    private final Map<String, UserDetails> users = new HashMap<>();
    private AuthenticationManager authenticationManager;

    public InMemoryMfaUserDetailsManager() {}

    public InMemoryMfaUserDetailsManager(UserDetails... users) {
        UserDetails[] userDetails = users;
        int length = users.length;

        for (int i = 0; i < length; ++i) {
            UserDetails user = userDetails[i];
            this.createUser(user);
        }
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        MfaUserDetails user = (MfaUserDetails) this.users.get(username.toLowerCase());
        if (user == null) {
            throw new UsernameNotFoundException(username);
        } else {
            return new MfaUserDetails(
                    user.getUsername(),
                    user.getPassword(),
                    user.isEnableMfa(),
                    user.getSecret(),
                    user.isEnabled(),
                    user.isAccountNonExpired(),
                    user.isCredentialsNonExpired(),
                    user.isAccountNonLocked(),
                    user.getAuthorities());
        }
    }

    @Override
    public void createUser(UserDetails user) {
        Assert.isTrue(!this.userExists(user.getUsername()), "user should not exist");
        this.users.put(user.getUsername().toLowerCase(), user);
    }

    @Override
    public void updateUser(UserDetails user) {
        Assert.isTrue(this.userExists(user.getUsername()), "user should exist");
        this.users.put(user.getUsername().toLowerCase(), user);
    }

    @Override
    public void deleteUser(String username) {
        this.users.remove(username.toLowerCase());
    }

    @Override
    public void changePassword(String oldPassword, String newPassword) {
        Authentication currentUser = SecurityContextHolder.getContext().getAuthentication();
        if (currentUser == null) {
            throw new AccessDeniedException(
                    "Can't change password as no Authentication object found in context for current user.");
        } else {
            String username = currentUser.getName();
            LogUtils.debug(String.format("Changing password for user '%s'", username));
            if (this.authenticationManager != null) {
                LogUtils.debug(
                        String.format(
                                "Reauthenticating user '%s' for password change request.",
                                username));
                this.authenticationManager.authenticate(
                        UsernamePasswordAuthenticationToken.unauthenticated(username, oldPassword));
            } else {
                LogUtils.debug("No authentication manager set. Password won't be re-checked.");
            }

            MfaUserDetails user = (MfaUserDetails) this.users.get(username);
            Assert.state(user != null, "Current user doesn't exist in database.");
            user.setPassword(newPassword);
        }
    }

    @Override
    public boolean userExists(String username) {
        return this.users.containsKey(username.toLowerCase());
    }

    public void setAuthenticationManager(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }

    @Override
    public UserDetails updatePassword(UserDetails user, String newPassword) {
        String username = user.getUsername();
        MfaUserDetails mfaUserDetails = (MfaUserDetails) this.users.get(username.toLowerCase());
        mfaUserDetails.setPassword(newPassword);
        return mfaUserDetails;
    }
}
