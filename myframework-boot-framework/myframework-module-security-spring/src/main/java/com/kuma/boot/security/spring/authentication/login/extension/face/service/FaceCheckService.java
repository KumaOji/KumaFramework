/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.security.core.userdetails.UsernameNotFoundException
 */
package com.kuma.boot.security.spring.authentication.login.extension.face.service;

import org.springframework.security.core.userdetails.UsernameNotFoundException;

public interface FaceCheckService {
    public boolean check(String var1) throws UsernameNotFoundException;
}

