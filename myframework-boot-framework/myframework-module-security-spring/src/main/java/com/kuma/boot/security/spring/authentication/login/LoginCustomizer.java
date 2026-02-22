/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.security.config.annotation.web.builders.HttpSecurity
 */
package com.kuma.boot.security.spring.authentication.login;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;

public interface LoginCustomizer {
    public void loginCustomizer(HttpSecurity var1) throws Exception;
}

