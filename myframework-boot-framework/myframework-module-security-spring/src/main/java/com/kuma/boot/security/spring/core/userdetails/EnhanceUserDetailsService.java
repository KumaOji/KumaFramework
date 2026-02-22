/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  org.springframework.security.core.AuthenticationException
 *  org.springframework.security.core.userdetails.UserDetails
 *  org.springframework.security.core.userdetails.UserDetailsService
 *  org.springframework.security.core.userdetails.UsernameNotFoundException
 */
package com.kuma.boot.security.spring.core.userdetails;

import com.kuma.boot.security.spring.core.AccessPrincipal;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public interface EnhanceUserDetailsService
extends UserDetailsService {
    public UserDetails loadUserBySocial(String var1, AccessPrincipal var2) throws AuthenticationException;

    public TtcUser loadTtcUserByUsername(String var1) throws UsernameNotFoundException;
}

