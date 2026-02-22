/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  org.springframework.security.core.userdetails.UserDetails
 *  org.springframework.security.core.userdetails.UsernameNotFoundException
 */
package com.kuma.boot.security.spring.authentication.login.extension.oneClick.service;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public class DefaultOneClickUserDetailsService
implements OneClickUserDetailsService {
    @Override
    public UserDetails loadUserByOneClick(String phone) throws UsernameNotFoundException {
        return null;
    }

    @Override
    public UserDetails registerUser(String principal) {
        return null;
    }
}

