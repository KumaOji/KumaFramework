/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.lang.Nullable
 *  org.springframework.security.core.Authentication
 */
package com.kuma.boot.security.spring.authentication.login.extension.mfa.jwt;

import org.springframework.lang.Nullable;
import org.springframework.security.core.Authentication;

@FunctionalInterface
public interface TokenGenerator<T> {
    @Nullable
    public T generate(Authentication var1);
}

