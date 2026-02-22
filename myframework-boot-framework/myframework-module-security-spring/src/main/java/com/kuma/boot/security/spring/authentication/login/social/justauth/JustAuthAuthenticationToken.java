/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.security.authentication.AbstractAuthenticationToken
 *  org.springframework.security.core.GrantedAuthority
 *  org.springframework.util.Assert
 */
package com.kuma.boot.security.spring.authentication.login.social.justauth;

import java.util.Collection;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.util.Assert;

public class JustAuthAuthenticationToken
extends AbstractAuthenticationToken {
    private static final long serialVersionUID = 620L;
    private final Object principal;
    private final String providerId;

    public JustAuthAuthenticationToken(Object principal, Collection<? extends GrantedAuthority> authorities, String providerId) {
        super(authorities);
        Assert.notNull((Object)principal, (String)"principal cannot be null");
        Assert.hasText((String)providerId, (String)"providerId cannot be empty");
        this.principal = principal;
        this.providerId = providerId;
        this.setAuthenticated(true);
    }

    public Object getPrincipal() {
        return this.principal;
    }

    public Object getCredentials() {
        return "";
    }

    public String getProviderId() {
        return this.providerId;
    }
}

