/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.security.core.userdetails.UserDetails
 *  org.springframework.security.core.userdetails.UsernameNotFoundException
 */
package com.kuma.boot.security.spring.authentication.login.extension.email.service;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public interface EmailUserDetailsService {
    public UserDetails loadUserByEmail(String var1) throws UsernameNotFoundException;
}

