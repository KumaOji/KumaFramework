/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.security.oauth2.core.AuthorizationGrantType
 */
package com.kuma.boot.security.spring.oauth2;

import org.springframework.security.oauth2.core.AuthorizationGrantType;

public interface TtcAuthorizationGrantType {
    public static final AuthorizationGrantType SOCIAL = new AuthorizationGrantType("social_credentials");
    public static final AuthorizationGrantType PASSWORD = new AuthorizationGrantType("password");
}

