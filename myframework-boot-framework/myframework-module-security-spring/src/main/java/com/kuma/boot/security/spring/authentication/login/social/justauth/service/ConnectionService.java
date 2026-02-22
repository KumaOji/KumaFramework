/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.kuma.boot.security.justauth.justauth.ConnectionData
 *  me.zhyd.oauth.model.AuthUser
 *  org.springframework.lang.NonNull
 *  org.springframework.lang.Nullable
 *  org.springframework.security.core.userdetails.UserDetails
 *  org.springframework.util.MultiValueMap
 */
package com.kuma.boot.security.spring.authentication.login.social.justauth.service;

import com.kuma.boot.security.justauth.justauth.ConnectionData;
import com.kuma.boot.security.spring.authentication.login.social.justauth.entity.ConnectionDto;
import com.kuma.boot.security.spring.authentication.login.social.justauth.repository.exception.UpdateConnectionException;
import com.kuma.boot.security.spring.exception.RegisterUserFailureException;
import java.util.List;
import me.zhyd.oauth.model.AuthUser;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.MultiValueMap;

public interface ConnectionService {
    @NonNull
    public UserDetails signUp(@NonNull AuthUser var1, @NonNull String var2, @NonNull String var3) throws RegisterUserFailureException;

    public void updateUserConnectionAndAuthToken(@NonNull AuthUser var1, @NonNull ConnectionData var2) throws UpdateConnectionException;

    public void binding(@NonNull UserDetails var1, @NonNull AuthUser var2, @NonNull String var3);

    public void unbinding(@NonNull String var1, @NonNull String var2, @NonNull String var3);

    @Nullable
    public List<ConnectionData> findConnectionByProviderIdAndProviderUserId(@NonNull String var1, @NonNull String var2);

    @NonNull
    public MultiValueMap<String, ConnectionDto> listAllConnections(@NonNull String var1);
}

