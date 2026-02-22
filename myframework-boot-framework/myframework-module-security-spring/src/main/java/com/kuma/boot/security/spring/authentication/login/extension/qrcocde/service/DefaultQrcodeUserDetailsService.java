/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  org.springframework.security.core.userdetails.UserDetails
 *  org.springframework.security.core.userdetails.UsernameNotFoundException
 */
package com.kuma.boot.security.spring.authentication.login.extension.qrcocde.service;

import com.kuma.boot.security.spring.core.userdetails.TtcUser;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public class DefaultQrcodeUserDetailsService
implements QrcodeUserDetailsService {
    @Override
    public UserDetails loadUserByPhone(String phone) throws UsernameNotFoundException {
        TtcUser ttcUser = new TtcUser();
        return ttcUser;
    }
}

