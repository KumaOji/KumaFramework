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

public class DefaultJustAuthUserDetailsService
implements JustAuthUserDetailsService {
    @Override
    public UserDetails loadUserByUserId(String userId) throws UsernameNotFoundException {
        return null;
    }

    @Override
    public List<Boolean> existedByUsernames(String ... usernames) throws IOException {
        return List.of();
    }
}

