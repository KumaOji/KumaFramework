/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  org.springframework.security.core.userdetails.UserDetails
 *  org.springframework.security.core.userdetails.UsernameNotFoundException
 */
package com.kuma.boot.security.spring.authentication.login.form.sms.service;

import com.kuma.boot.security.spring.core.authority.TtcGrantedAuthority;
import com.kuma.boot.security.spring.core.userdetails.TtcUser;
import java.util.ArrayList;
import java.util.HashSet;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public class DefaultFormSmsUserDetailsService
implements FormSmsUserDetailsService {
    @Override
    public UserDetails loadUserByPhone(String phone, String type) throws UsernameNotFoundException {
        ArrayList<TtcGrantedAuthority> authorities = new ArrayList<TtcGrantedAuthority>();
        authorities.add(new TtcGrantedAuthority("manager.book.read1111"));
        authorities.add(new TtcGrantedAuthority("manager.book.write1111"));
        HashSet<String> roles = new HashSet<String>();
        roles.add("ROLE_A1");
        roles.add("ROLE_A2");
        return new TtcUser();
    }
}

