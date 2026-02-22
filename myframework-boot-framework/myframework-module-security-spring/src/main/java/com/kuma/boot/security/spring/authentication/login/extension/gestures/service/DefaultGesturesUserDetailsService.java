/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  org.springframework.security.core.userdetails.UserDetails
 *  org.springframework.security.core.userdetails.UsernameNotFoundException
 */
package com.kuma.boot.security.spring.authentication.login.extension.gestures.service;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public class DefaultGesturesUserDetailsService
implements GesturesUserDetailsService {
    @Override
    public UserDetails loadUserByPhone(String phone) throws UsernameNotFoundException {
        return null;
    }
}

