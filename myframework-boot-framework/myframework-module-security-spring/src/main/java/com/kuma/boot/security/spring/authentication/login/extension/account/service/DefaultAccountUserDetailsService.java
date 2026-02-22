/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  com.kuma.boot.common.enums.LoginTypeEnum
 *  org.springframework.security.core.userdetails.UserDetails
 *  org.springframework.security.core.userdetails.UsernameNotFoundException
 */
package com.kuma.boot.security.spring.authentication.login.extension.account.service;

import com.kuma.boot.common.enums.LoginTypeEnum;
import com.kuma.boot.security.spring.core.userdetails.TtcUser;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public class DefaultAccountUserDetailsService
implements AccountUserDetailsService {
    @Override
    public UserDetails loadUserByUsername(String username, String type) throws UsernameNotFoundException {
        if (LoginTypeEnum.B_PC_ACCOUNT.getType().equals(type)) {
            // empty if block
        }
        if (LoginTypeEnum.C_PC_ACCOUNT.getType().equals(type)) {
            // empty if block
        }
        if (LoginTypeEnum.C_APP_ACCOUNT.getType().equals(type)) {
            // empty if block
        }
        return new TtcUser();
    }
}

