/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  org.springframework.security.core.userdetails.UserDetails
 */
package com.kuma.boot.security.spring.authentication.login.extension.email.service;

import org.springframework.security.core.userdetails.UserDetails;

public class DefaultEmailUserDetailsService
implements EmailUserDetailsService {
    @Override
    public UserDetails loadUserByEmail(String email) {
        return null;
    }
}

