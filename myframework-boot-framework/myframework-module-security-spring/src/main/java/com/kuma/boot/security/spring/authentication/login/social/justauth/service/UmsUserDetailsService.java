/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  me.zhyd.oauth.model.AuthUser
 *  org.springframework.security.core.userdetails.UserDetails
 *  org.springframework.security.core.userdetails.UserDetailsService
 *  org.springframework.security.core.userdetails.UsernameNotFoundException
 */
package com.kuma.boot.security.spring.authentication.login.social.justauth.service;

import java.io.IOException;
import java.util.List;
import me.zhyd.oauth.model.AuthUser;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public interface UmsUserDetailsService
extends UserDetailsService,
UserDetailsRegisterService {
    public UserDetails loadUserByUserId(String var1) throws UsernameNotFoundException;

    public List<Boolean> existedByUsernames(String ... var1) throws IOException;

    default public String[] generateUsernames(AuthUser authUser) {
        return new String[]{authUser.getUsername(), authUser.getUsername() + "_" + authUser.getSource(), authUser.getUsername() + "_" + authUser.getSource() + "_" + authUser.getUuid()};
    }
}

