/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  jakarta.servlet.http.HttpServletRequest
 *  org.springframework.lang.NonNull
 */
package com.kuma.boot.security.spring.authentication.login.social.justauth.service;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.lang.NonNull;

public interface Auth2StateCoder {
    public String encode(@NonNull String var1, @NonNull HttpServletRequest var2);

    public String decode(@NonNull String var1);
}

