/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.lang.NonNull
 *  org.springframework.lang.Nullable
 *  org.springframework.security.core.userdetails.UserDetails
 */
package com.kuma.boot.security.spring.authentication.login.extension.oneClick.service;

import java.util.Map;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.security.core.userdetails.UserDetails;

public interface OneClickLoginService {
    @NonNull
    public String callback(@NonNull String var1, @Nullable Map<String, String> var2);

    public void otherParamsHandler(@NonNull UserDetails var1, @Nullable Map<String, String> var2);
}

