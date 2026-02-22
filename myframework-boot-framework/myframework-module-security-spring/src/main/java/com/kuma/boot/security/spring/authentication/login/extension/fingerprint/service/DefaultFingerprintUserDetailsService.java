/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  org.springframework.security.core.userdetails.UserDetails
 *  org.springframework.security.core.userdetails.UsernameNotFoundException
 */
package com.kuma.boot.security.spring.authentication.login.extension.fingerprint.service;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public class DefaultFingerprintUserDetailsService
implements FingerprintUserDetailsService {
    @Override
    public UserDetails loadUserByFingerprint(String username) throws UsernameNotFoundException {
        return null;
    }
}

