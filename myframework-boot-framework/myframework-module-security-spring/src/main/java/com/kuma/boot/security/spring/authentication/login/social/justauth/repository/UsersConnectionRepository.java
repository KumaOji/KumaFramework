/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.kuma.boot.security.justauth.justauth.AuthTokenPo
 *  com.kuma.boot.security.justauth.justauth.ConnectionData
 *  org.springframework.jdbc.core.namedparam.MapSqlParameterSource
 *  org.springframework.util.MultiValueMap
 */
package com.kuma.boot.security.spring.authentication.login.social.justauth.repository;

import com.kuma.boot.security.justauth.justauth.AuthTokenPo;
import com.kuma.boot.security.justauth.justauth.ConnectionData;
import com.kuma.boot.security.spring.authentication.login.social.justauth.entity.ConnectionKey;
import com.kuma.boot.security.spring.authentication.login.social.justauth.repository.exception.NoSuchConnectionException;
import com.kuma.boot.security.spring.authentication.login.social.justauth.repository.exception.NotConnectedException;
import java.util.List;
import java.util.Set;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.util.MultiValueMap;

public interface UsersConnectionRepository {
    public List<ConnectionData> findConnectionByProviderIdAndProviderUserId(String var1, String var2);

    public Set<String> findUserIdsConnectedTo(String var1, Set<String> var2);

    public MultiValueMap<String, ConnectionData> findAllConnections(String var1);

    public List<ConnectionData> findConnections(String var1, String var2);

    public MultiValueMap<String, ConnectionData> findConnectionsToUsers(String var1, MultiValueMap<String, String> var2);

    public ConnectionData getPrimaryConnection(String var1, String var2) throws NotConnectedException;

    public ConnectionData addConnection(ConnectionData var1);

    public ConnectionData updateConnection(ConnectionData var1);

    public void removeConnections(String var1, String var2);

    public void removeConnection(String var1, ConnectionKey var2);

    public ConnectionData findPrimaryConnection(String var1, String var2);

    public ConnectionData getConnection(String var1, ConnectionKey var2) throws NoSuchConnectionException;

    public List<ConnectionData> findAllListConnections(String var1);

    public List<ConnectionData> findConnectionsToUsers(MapSqlParameterSource var1, String var2, String var3);

    public ConnectionData updateConnectionByTokenId(AuthTokenPo var1);

    public ConnectionData findConnectionByTokenId(Long var1);
}

