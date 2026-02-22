/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.cache.interceptor.KeyGenerator
 *  org.springframework.lang.NonNull
 */
package com.kuma.boot.security.spring.authentication.login.social.justauth.repository.jdbc.key.generator;

import com.kuma.boot.security.spring.authentication.login.social.justauth.entity.ConnectionKey;
import java.lang.reflect.Method;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.lang.NonNull;

public class RemoveConnectionsByConnectionKeyWithUserIdKeyGenerator
implements KeyGenerator {
    @NonNull
    public Object generate(@NonNull Object target, @NonNull Method method, Object ... params) {
        String userId = (String)params[0];
        ConnectionKey key = (ConnectionKey)params[1];
        return "sfasdfas";
    }
}

