/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.kuma.boot.common.utils.log.LogUtils
 *  com.kuma.boot.security.justauth.justauth.AuthTokenPo
 *  com.kuma.boot.security.justauth.justauth.ConnectionData
 *  org.springframework.dao.DuplicateKeyException
 *  org.springframework.jdbc.core.JdbcOperations
 *  org.springframework.jdbc.core.JdbcTemplate
 *  org.springframework.jdbc.core.RowMapper
 *  org.springframework.jdbc.core.namedparam.MapSqlParameterSource
 *  org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
 *  org.springframework.jdbc.core.namedparam.SqlParameterSource
 *  org.springframework.lang.NonNull
 *  org.springframework.security.crypto.encrypt.TextEncryptor
 *  org.springframework.transaction.annotation.Propagation
 *  org.springframework.transaction.annotation.Transactional
 *  org.springframework.util.CollectionUtils
 *  org.springframework.util.LinkedMultiValueMap
 *  org.springframework.util.MultiValueMap
 */
package com.kuma.boot.security.spring.authentication.login.social.justauth.repository.jdbc;

import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.boot.security.justauth.justauth.AuthTokenPo;
import com.kuma.boot.security.justauth.justauth.ConnectionData;
import com.kuma.boot.security.spring.authentication.login.social.justauth.JustAuthRequestHolder;
import com.kuma.boot.security.spring.authentication.login.social.justauth.entity.ConnectionKey;
import com.kuma.boot.security.spring.authentication.login.social.justauth.properties.RepositoryProperties;
import com.kuma.boot.security.spring.authentication.login.social.justauth.repository.UsersConnectionRepository;
import com.kuma.boot.security.spring.authentication.login.social.justauth.repository.exception.DuplicateConnectionException;
import com.kuma.boot.security.spring.authentication.login.social.justauth.repository.exception.NoSuchConnectionException;
import com.kuma.boot.security.spring.authentication.login.social.justauth.repository.exception.NotConnectedException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.lang.NonNull;
import org.springframework.security.crypto.encrypt.TextEncryptor;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

public class Auth2JdbcUsersConnectionRepository
implements UsersConnectionRepository {
    private final JdbcTemplate jdbcTemplate;
    private final TextEncryptor textEncryptor;
    private final RepositoryProperties repositoryProperties;
    private final ServiceProviderConnectionDataMapper connectionDataMapper = new ServiceProviderConnectionDataMapper();

    public Auth2JdbcUsersConnectionRepository(JdbcTemplate auth2UserConnectionJdbcTemplate, TextEncryptor textEncryptor, RepositoryProperties repositoryProperties) {
        this.jdbcTemplate = auth2UserConnectionJdbcTemplate;
        this.textEncryptor = textEncryptor;
        this.repositoryProperties = repositoryProperties;
    }

    @Override
    public List<ConnectionData> findConnectionByProviderIdAndProviderUserId(String providerId, String providerUserId) {
        try {
            return this.jdbcTemplate.query(String.format("%s WHERE %s = ? AND %s = ? ORDER BY %s", this.repositoryProperties.getSelectFromUserConnectionSql(), this.repositoryProperties.getProviderIdColumnName(), this.repositoryProperties.getProviderUserIdColumnName(), this.repositoryProperties.getRankColumnName()), (RowMapper)this.connectionDataMapper, new Object[]{providerId, providerUserId});
        }
        catch (Exception e) {
            String msg = String.format("findConnectionByProviderIdAndProviderUserId, providerId=%s, providerUserId=%s. sql query error: %s", providerId, providerUserId, e.getMessage());
            LogUtils.error((String)msg, (Object[])new Object[]{e});
            return null;
        }
    }

    @Override
    public Set<String> findUserIdsConnectedTo(String providerId, Set<String> providerUserIds) {
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue(this.repositoryProperties.getProviderIdColumnName(), (Object)providerId);
        parameters.addValue(this.repositoryProperties.getProviderUserIdColumnName(), providerUserIds);
        HashSet<String> localUserIds = new HashSet<String>();
        try {
            new NamedParameterJdbcTemplate((JdbcOperations)this.jdbcTemplate).query(this.repositoryProperties.getFindUserIdsConnectedToSql(), (SqlParameterSource)parameters, rs -> {
                while (rs.next()) {
                    localUserIds.add(rs.getString(this.repositoryProperties.getUserIdColumnName()));
                }
                return localUserIds;
            });
        }
        catch (Exception e) {
            String msg = String.format("findUserIdsConnectedTo: providerId=%s, providerUserIds=%s. sql query error: %s", providerId, providerUserIds, e.getMessage());
            LogUtils.error((String)msg, (Object[])new Object[]{e});
        }
        return localUserIds;
    }

    @Override
    public MultiValueMap<String, ConnectionData> findAllConnections(String userId) {
        return this.getConnectionMap(this.findAllListConnections(userId));
    }

    @Override
    public List<ConnectionData> findConnections(String userId, String providerId) {
        return this.getConnectionDataList(userId, providerId);
    }

    @Override
    public MultiValueMap<String, ConnectionData> findConnectionsToUsers(String userId, MultiValueMap<String, String> providerUsers) {
        if (providerUsers == null || providerUsers.isEmpty()) {
            throw new IllegalArgumentException("Unable to execute find: no providerUsers provided");
        }
        StringBuilder providerUsersCriteriaSql = new StringBuilder();
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue(this.repositoryProperties.getUserIdColumnName(), (Object)userId);
        Iterator<Map.Entry<String, List<String>>> it = providerUsers.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry entry = (Map.Entry)it.next();
            this.fillingCriteriaSql(providerUsersCriteriaSql, parameters, it, entry);
        }
        return this.getConnectionMap(this.findConnectionsToUsers(parameters, providerUsersCriteriaSql.toString(), userId), providerUsers);
    }

    @Override
    public ConnectionData getConnection(String userId, ConnectionKey connectionKey) throws NoSuchConnectionException {
        try {
            return (ConnectionData)this.jdbcTemplate.queryForObject(String.format("%s where %s = ? and %s = ? and %s = ?", this.repositoryProperties.getSelectFromUserConnectionSql(), this.repositoryProperties.getUserIdColumnName(), this.repositoryProperties.getProviderIdColumnName(), this.repositoryProperties.getProviderUserIdColumnName()), (RowMapper)this.connectionDataMapper, new Object[]{userId, connectionKey.getProviderId(), connectionKey.getProviderUserId()});
        }
        catch (Exception e) {
            String msg = String.format("getConnection: userId=%s, connectionKey=%s. sql query error: %s", userId, connectionKey, e.getMessage());
            LogUtils.error((String)msg, (Object[])new Object[0]);
            throw new NoSuchConnectionException(connectionKey);
        }
    }

    @Override
    public ConnectionData findPrimaryConnection(String userId, String providerId) {
        List<ConnectionData> connectionDataList = this.getConnectionDataList(userId, providerId);
        if (connectionDataList != null && connectionDataList.size() > 0) {
            return connectionDataList.get(0);
        }
        return null;
    }

    @Override
    public ConnectionData getPrimaryConnection(String userId, String providerId) throws NotConnectedException {
        ConnectionData connection = this.findPrimaryConnection(userId, providerId);
        if (connection == null) {
            throw new NotConnectedException(userId + ":" + providerId);
        }
        return connection;
    }

    private List<ConnectionData> getConnectionDataList(String userId, String providerId) {
        try {
            return this.jdbcTemplate.query(String.format("%s where %s = ? and %s = ? order by %s", this.repositoryProperties.getSelectFromUserConnectionSql(), this.repositoryProperties.getUserIdColumnName(), this.repositoryProperties.getProviderIdColumnName(), this.repositoryProperties.getRankColumnName()), (RowMapper)this.connectionDataMapper, new Object[]{userId, providerId});
        }
        catch (Exception e) {
            String msg = String.format("getConnectionDataList: userId=%s, providerId=%s. sql query error: %s", userId, providerId, e.getMessage());
            LogUtils.error((String)msg, (Object[])new Object[]{e});
            return null;
        }
    }

    @Override
    @Transactional(rollbackFor={Exception.class}, propagation=Propagation.REQUIRED)
    public ConnectionData addConnection(ConnectionData connection) {
        this.addConnectionData(connection);
        return connection;
    }

    private void addConnectionData(ConnectionData connection) {
        try {
            int rank = (Integer)this.jdbcTemplate.queryForObject(this.repositoryProperties.getAddConnectionQueryForRankSql(), Integer.class, new Object[]{connection.getUserId(), connection.getProviderId()});
            this.jdbcTemplate.update(this.repositoryProperties.getAddConnectionSql(), new Object[]{connection.getUserId(), connection.getProviderId(), connection.getProviderUserId(), rank, connection.getDisplayName(), connection.getProfileUrl(), connection.getImageUrl(), this.encrypt(connection.getAccessToken()), connection.getTokenId(), this.encrypt(connection.getRefreshToken()), connection.getExpireTime()});
        }
        catch (DuplicateKeyException e) {
            throw new DuplicateConnectionException(new ConnectionKey(connection.getProviderId(), connection.getProviderUserId()));
        }
    }

    @Override
    @Transactional(rollbackFor={Exception.class}, propagation=Propagation.REQUIRED)
    public ConnectionData updateConnection(ConnectionData connection) {
        this.jdbcTemplate.update(this.repositoryProperties.getUpdateConnectionSql(), new Object[]{connection.getDisplayName(), connection.getProfileUrl(), connection.getImageUrl(), this.encrypt(connection.getAccessToken()), connection.getTokenId(), this.encrypt(connection.getRefreshToken()), connection.getExpireTime(), connection.getUserId(), connection.getProviderId(), connection.getProviderUserId()});
        return connection;
    }

    @Override
    @Transactional(rollbackFor={Exception.class}, propagation=Propagation.REQUIRED)
    public ConnectionData updateConnectionByTokenId(AuthTokenPo token) {
        ConnectionData connection = this.findConnectionByTokenId(token.getId());
        connection.setAccessToken(token.getAccessToken());
        connection.setRefreshToken(token.getRefreshToken());
        connection.setExpireTime(token.getExpireTime());
        this.updateConnection(connection);
        return connection;
    }

    @Override
    public ConnectionData findConnectionByTokenId(Long tokenId) {
        return (ConnectionData)this.jdbcTemplate.queryForObject(String.format("%s where %s = ?", this.repositoryProperties.getSelectFromUserConnectionSql(), this.repositoryProperties.getTokenIdColumnName()), (RowMapper)this.connectionDataMapper, new Object[]{tokenId});
    }

    @Override
    @Transactional(rollbackFor={Exception.class}, propagation=Propagation.REQUIRED)
    public void removeConnections(String userId, String providerId) {
        this.jdbcTemplate.update(this.repositoryProperties.getRemoveConnectionsSql(), new Object[]{userId, providerId});
    }

    @Override
    @Transactional(rollbackFor={Exception.class}, propagation=Propagation.REQUIRED)
    public void removeConnection(String userId, ConnectionKey connectionKey) {
        this.jdbcTemplate.update(this.repositoryProperties.getRemoveConnectionSql(), new Object[]{userId, connectionKey.getProviderId(), connectionKey.getProviderUserId()});
    }

    @Override
    public List<ConnectionData> findAllListConnections(String userId) {
        try {
            return this.jdbcTemplate.query(String.format("%s where %s = ? order by %s, %s", this.repositoryProperties.getSelectFromUserConnectionSql(), this.repositoryProperties.getUserIdColumnName(), this.repositoryProperties.getProviderIdColumnName(), this.repositoryProperties.getRankColumnName()), (RowMapper)this.connectionDataMapper, new Object[]{userId});
        }
        catch (Exception e) {
            String msg = String.format("findAllListConnections: userId=%s. sql query error: %s", userId, e.getMessage());
            LogUtils.error((String)msg, (Object[])new Object[]{e});
            return null;
        }
    }

    @Override
    public List<ConnectionData> findConnectionsToUsers(MapSqlParameterSource parameters, String providerUsersCriteriaSql, String userId) {
        try {
            return new NamedParameterJdbcTemplate((JdbcOperations)this.jdbcTemplate).query(String.format("%s where %s = :userId and %s order by %s, %s", this.repositoryProperties.getSelectFromUserConnectionSql(), this.repositoryProperties.getUserIdColumnName(), providerUsersCriteriaSql, this.repositoryProperties.getProviderIdColumnName(), this.repositoryProperties.getRankColumnName()), (SqlParameterSource)parameters, (RowMapper)this.connectionDataMapper);
        }
        catch (Exception e) {
            String msg = String.format("findConnectionsToUsers: userId=%s, parameters=%s, providerUsersCriteriaSql=%s. sql query error: %s", userId, parameters, providerUsersCriteriaSql, e.getMessage());
            LogUtils.error((String)msg, (Object[])new Object[]{e});
            return null;
        }
    }

    private MultiValueMap<String, ConnectionData> getConnectionMap(List<ConnectionData> connectionList) {
        LinkedMultiValueMap connections = new LinkedMultiValueMap();
        Collection<String> registeredProviderIds = JustAuthRequestHolder.getValidProviderIds();
        for (String registeredProviderId : registeredProviderIds) {
            connections.put((Object)registeredProviderId, Collections.emptyList());
        }
        for (ConnectionData connection : connectionList) {
            String providerId = connection.getProviderId();
            List list = (List)connections.get((Object)providerId);
            if (CollectionUtils.isEmpty((Collection)list)) {
                connections.put((Object)providerId, new LinkedList());
            }
            connections.add((Object)providerId, (Object)connection);
        }
        return connections;
    }

    private MultiValueMap<String, ConnectionData> getConnectionMap(List<ConnectionData> connectionList, MultiValueMap<String, String> providerUsers) {
        LinkedMultiValueMap connectionsForUsers = new LinkedMultiValueMap();
        for (ConnectionData connection : connectionList) {
            String providerId = connection.getProviderId();
            List userIds = (List)providerUsers.get((Object)providerId);
            ArrayList<ConnectionData> connections = (ArrayList<ConnectionData>)connectionsForUsers.get((Object)providerId);
            if (connections == null) {
                connections = new ArrayList<ConnectionData>(userIds.size());
                for (int i = 0; i < userIds.size(); ++i) {
                    connections.add(null);
                }
                connectionsForUsers.put((Object)providerId, connections);
            }
            String providerUserId = connection.getProviderUserId();
            int connectionIndex = userIds.indexOf(providerUserId);
            connections.set(connectionIndex, connection);
        }
        return connectionsForUsers;
    }

    private String encrypt(String text) {
        return text != null ? this.textEncryptor.encrypt(text) : null;
    }

    private void fillingCriteriaSql(StringBuilder providerUsersCriteriaSql, MapSqlParameterSource parameters, Iterator<Map.Entry<String, List<String>>> it, Map.Entry<String, List<String>> entry) {
        String providerId = entry.getKey();
        providerUsersCriteriaSql.append(String.format("%s = :providerId_", this.repositoryProperties.getProviderIdColumnName())).append(providerId).append(String.format(" and %s in (:providerUserIds_", this.repositoryProperties.getProviderUserIdColumnName())).append(providerId).append(")");
        parameters.addValue(String.format("%s_%s", this.repositoryProperties.getProviderIdColumnName(), providerId), (Object)providerId);
        parameters.addValue(String.format("%s_%s", this.repositoryProperties.getProviderUserIdColumnName(), providerId), entry.getValue());
        if (it.hasNext()) {
            providerUsersCriteriaSql.append(" or ");
        }
    }

    private final class ServiceProviderConnectionDataMapper
    implements RowMapper<ConnectionData> {
        private ServiceProviderConnectionDataMapper() {
        }

        public ConnectionData mapRow(@NonNull ResultSet rs, int rowNum) throws SQLException {
            return this.mapConnectionData(rs);
        }

        private ConnectionData mapConnectionData(ResultSet rs) throws SQLException {
            ConnectionData connectionData = new ConnectionData();
            connectionData.setUserId(rs.getString(Auth2JdbcUsersConnectionRepository.this.repositoryProperties.getUserIdColumnName()));
            connectionData.setProviderId(rs.getString(Auth2JdbcUsersConnectionRepository.this.repositoryProperties.getProviderIdColumnName()));
            connectionData.setProviderUserId(rs.getString(Auth2JdbcUsersConnectionRepository.this.repositoryProperties.getProviderUserIdColumnName()));
            connectionData.setDisplayName(rs.getString(Auth2JdbcUsersConnectionRepository.this.repositoryProperties.getDisplayNameColumnName()));
            connectionData.setProfileUrl(rs.getString(Auth2JdbcUsersConnectionRepository.this.repositoryProperties.getProfileUrlColumnName()));
            connectionData.setImageUrl(rs.getString(Auth2JdbcUsersConnectionRepository.this.repositoryProperties.getImageUrlColumnName()));
            connectionData.setAccessToken(this.decrypt(rs.getString(Auth2JdbcUsersConnectionRepository.this.repositoryProperties.getAccessTokenColumnName())));
            connectionData.setTokenId(Long.valueOf(rs.getLong(Auth2JdbcUsersConnectionRepository.this.repositoryProperties.getTokenIdColumnName())));
            connectionData.setRefreshToken(this.decrypt(rs.getString(Auth2JdbcUsersConnectionRepository.this.repositoryProperties.getRefreshTokenColumnName())));
            connectionData.setExpireTime(this.expireTime(rs.getLong(Auth2JdbcUsersConnectionRepository.this.repositoryProperties.getExpireTimeColumnName())));
            return connectionData;
        }

        private String decrypt(String encryptedText) {
            return encryptedText != null ? Auth2JdbcUsersConnectionRepository.this.textEncryptor.decrypt(encryptedText) : null;
        }

        private Long expireTime(long expireTime) {
            return expireTime == 0L ? null : Long.valueOf(expireTime);
        }
    }
}

