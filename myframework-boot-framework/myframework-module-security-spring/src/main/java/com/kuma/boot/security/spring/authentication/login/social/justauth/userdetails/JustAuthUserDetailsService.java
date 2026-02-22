/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  org.springframework.security.core.userdetails.UserDetails
 *  org.springframework.security.core.userdetails.UsernameNotFoundException
 */
package com.kuma.boot.security.spring.authentication.login.social.justauth.userdetails;

import java.io.IOException;
import java.util.List;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public interface JustAuthUserDetailsService
extends JustAuthUserDetailsRegisterService {
    public UserDetails loadUserByUserId(String var1) throws UsernameNotFoundException;

    public List<Boolean> existedByUsernames(String ... var1) throws IOException;
}

