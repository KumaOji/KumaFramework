/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.security.core.GrantedAuthority
 */
package com.kuma.boot.security.spring.utils;

import com.kuma.boot.security.spring.core.authority.TtcGrantedAuthority;
import java.util.HashSet;
import java.util.Set;
import org.springframework.security.core.GrantedAuthority;

public class AuthorityUtils {
    public static Set<GrantedAuthority> createAuthorityList(String ... authorities) {
        HashSet<GrantedAuthority> grantedAuthorities = new HashSet<GrantedAuthority>(authorities.length);
        for (String authority : authorities) {
            grantedAuthorities.add(new TtcGrantedAuthority(authority));
        }
        return grantedAuthorities;
    }
}

