/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.security.spring.oauth2.token1;

import com.kuma.boot.security.spring.core.PrincipalDetails;

public interface BearerTokenResolver {
    public PrincipalDetails resolve(String var1);
}

