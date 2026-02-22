/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.kuma.boot.security.justauth.justauth.AuthTokenPo
 *  com.kuma.boot.security.justauth.justauth.EnableRefresh
 *  org.springframework.lang.NonNull
 *  org.springframework.lang.Nullable
 */
package com.kuma.boot.security.spring.authentication.login.social.justauth.repository;

import com.kuma.boot.security.justauth.justauth.AuthTokenPo;
import com.kuma.boot.security.justauth.justauth.EnableRefresh;
import java.util.List;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

public interface UsersConnectionTokenRepository {
    @Nullable
    public AuthTokenPo findAuthTokenById(@NonNull String var1) throws Exception;

    @NonNull
    public AuthTokenPo saveAuthToken(@NonNull AuthTokenPo var1) throws Exception;

    @NonNull
    public AuthTokenPo updateAuthToken(@NonNull AuthTokenPo var1) throws Exception;

    public void delAuthTokenById(@NonNull String var1) throws Exception;

    @NonNull
    public Long getMaxTokenId() throws Exception;

    @NonNull
    public List<AuthTokenPo> findAuthTokenByExpireTimeAndBetweenId(@NonNull Long var1, @NonNull Long var2, @NonNull Long var3) throws Exception;

    public void updateEnableRefreshByTokenId(@NonNull EnableRefresh var1, @NonNull Long var2) throws Exception;
}

