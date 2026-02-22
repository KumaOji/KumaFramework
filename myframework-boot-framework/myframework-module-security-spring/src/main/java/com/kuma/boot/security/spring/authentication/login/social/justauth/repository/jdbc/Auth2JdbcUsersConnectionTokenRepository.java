/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.kuma.boot.security.justauth.justauth.AuthTokenPo
 *  com.kuma.boot.security.justauth.justauth.EnableRefresh
 *  org.springframework.dao.DataAccessException
 *  org.springframework.dao.IncorrectResultSizeDataAccessException
 *  org.springframework.jdbc.core.JdbcTemplate
 *  org.springframework.jdbc.core.RowMapper
 *  org.springframework.lang.NonNull
 *  org.springframework.lang.Nullable
 *  org.springframework.security.crypto.encrypt.TextEncryptor
 *  org.springframework.transaction.annotation.Propagation
 *  org.springframework.transaction.annotation.Transactional
 */
package com.kuma.boot.security.spring.authentication.login.social.justauth.repository.jdbc;

import com.kuma.boot.security.justauth.justauth.AuthTokenPo;
import com.kuma.boot.security.justauth.justauth.EnableRefresh;
import com.kuma.boot.security.spring.authentication.login.social.justauth.repository.UsersConnectionTokenRepository;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.security.crypto.encrypt.TextEncryptor;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

public class Auth2JdbcUsersConnectionTokenRepository
implements UsersConnectionTokenRepository {
    private final JdbcTemplate jdbcTemplate;
    private final TextEncryptor textEncryptor;
    private final String authTokenTableName;
    private final AuthTokenPoMapper authTokenPoMapper = new AuthTokenPoMapper();

    public Auth2JdbcUsersConnectionTokenRepository(JdbcTemplate auth2UserConnectionJdbcTemplate, TextEncryptor textEncryptor, String authTokenTableName) {
        this.jdbcTemplate = auth2UserConnectionJdbcTemplate;
        this.textEncryptor = textEncryptor;
        this.authTokenTableName = authTokenTableName;
    }

    @Override
    @Nullable
    public AuthTokenPo findAuthTokenById(@NonNull String tokenId) throws DataAccessException {
        return (AuthTokenPo)this.jdbcTemplate.queryForObject("SELECT `id`, `enableRefresh`, `providerId`, `accessToken`, `expireIn`, `refreshTokenExpireIn`, `refreshToken`, `uid`, `openId`, `accessCode`, `unionId`, `scope`, `tokenType`, `idToken`, `macAlgorithm`, `macKey`, `code`, `oauthToken`, `oauthTokenSecret`, `userId`, `screenName`, `oauthCallbackConfirmed`, `expireTime` FROM `" + this.authTokenTableName + "` WHERE id = ?;", (RowMapper)this.authTokenPoMapper, new Object[]{tokenId});
    }

    @Override
    @Transactional(rollbackFor={Exception.class}, propagation=Propagation.REQUIRED)
    @NonNull
    public AuthTokenPo saveAuthToken(@NonNull AuthTokenPo authToken) throws DataAccessException {
        this.jdbcTemplate.update("INSERT INTO " + this.authTokenTableName + "(`enableRefresh` ,`providerId`, `accessToken`, `expireIn`, `refreshTokenExpireIn`, `refreshToken`, `uid`, `openId`, `accessCode`, `unionId`, `scope`, `tokenType`, `idToken`, `macAlgorithm`, `macKey`, `code`, `oauthToken`, `oauthTokenSecret`, `userId`, `screenName`, `oauthCallbackConfirmed`, `expireTime`) VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);", new Object[]{authToken.getEnableRefresh().getCode(), authToken.getProviderId(), this.encrypt(authToken.getAccessToken()), authToken.getExpireIn(), authToken.getRefreshTokenExpireIn(), this.encrypt(authToken.getRefreshToken()), authToken.getUid(), authToken.getOpenId(), this.encrypt(authToken.getAccessCode()), authToken.getUnionId(), authToken.getScope(), authToken.getTokenType(), this.encrypt(authToken.getIdToken()), authToken.getMacAlgorithm(), this.encrypt(authToken.getMacKey()), this.encrypt(authToken.getCode()), this.encrypt(authToken.getOauthToken()), this.encrypt(authToken.getOauthTokenSecret()), authToken.getUserId(), authToken.getScreenName(), authToken.getOauthCallbackConfirmed(), authToken.getExpireTime()});
        Long id = (Long)this.jdbcTemplate.queryForObject("SELECT LAST_INSERT_ID();", Long.class);
        authToken.setId(id);
        return authToken;
    }

    @Override
    @Transactional(rollbackFor={Exception.class}, propagation=Propagation.REQUIRED)
    @NonNull
    public AuthTokenPo updateAuthToken(@NonNull AuthTokenPo authToken) throws DataAccessException {
        if (authToken.getId() == null) {
            throw new RuntimeException("authToken id cannot be null");
        }
        this.jdbcTemplate.update("UPDATE `" + this.authTokenTableName + "` SET `enableRefresh` = ?, `providerId` = ?, `accessToken` = ?, `expireIn` = ?, `refreshTokenExpireIn` = ?, `refreshToken` = ?, `uid` = ?, `openId` = ?, `accessCode` = ?, `unionId` = ?, `scope` = ?, `tokenType` = ?, `idToken` = ?, `macAlgorithm` = ?, `macKey` = ?, `code` = ?, `oauthToken` = ?, `oauthTokenSecret` = ?, `userId` = ?, `screenName` = ?, `oauthCallbackConfirmed` = ?, `expireTime` = ? WHERE `id` = ?;", new Object[]{authToken.getEnableRefresh().getCode(), authToken.getProviderId(), this.encrypt(authToken.getAccessToken()), authToken.getExpireIn(), authToken.getRefreshTokenExpireIn(), this.encrypt(authToken.getRefreshToken()), authToken.getUid(), authToken.getOpenId(), this.encrypt(authToken.getAccessCode()), authToken.getUnionId(), authToken.getScope(), authToken.getTokenType(), this.encrypt(authToken.getIdToken()), authToken.getMacAlgorithm(), this.encrypt(authToken.getMacKey()), this.encrypt(authToken.getCode()), this.encrypt(authToken.getOauthToken()), this.encrypt(authToken.getOauthTokenSecret()), authToken.getUserId(), authToken.getScreenName(), authToken.getOauthCallbackConfirmed(), authToken.getExpireTime(), authToken.getId()});
        return authToken;
    }

    @Override
    @Transactional(rollbackFor={Exception.class}, propagation=Propagation.REQUIRED)
    public void delAuthTokenById(@NonNull String tokenId) throws DataAccessException {
        this.jdbcTemplate.update("DELETE FROM `" + this.authTokenTableName + "` WHERE id = ?;", new Object[]{tokenId});
    }

    @Override
    @NonNull
    public Long getMaxTokenId() throws IncorrectResultSizeDataAccessException {
        Long max = (Long)this.jdbcTemplate.queryForObject("SELECT MAX(`id`) FROM `" + this.authTokenTableName + "`", Long.class);
        if (Objects.isNull(max)) {
            return 1L;
        }
        return max;
    }

    @Override
    @NonNull
    public List<AuthTokenPo> findAuthTokenByExpireTimeAndBetweenId(@NonNull Long expiredTime, @NonNull Long startId, @NonNull Long endId) throws DataAccessException {
        return this.jdbcTemplate.query("SELECT `id`, `enableRefresh`, `providerId`, `accessToken`, `expireIn`, `refreshTokenExpireIn`, `refreshToken`, `uid`, `openId`, `accessCode`, `unionId`, `scope`, `tokenType`, `idToken`, `macAlgorithm`, `macKey`, `code`, `oauthToken`, `oauthTokenSecret`, `userId`, `screenName`, `oauthCallbackConfirmed`, `expireTime` FROM `" + this.authTokenTableName + "` WHERE id BETWEEN ? AND ? AND `expireTime` <= ? AND enableRefresh = " + EnableRefresh.YES.getCode() + ";", (RowMapper)this.authTokenPoMapper, new Object[]{startId, endId, expiredTime});
    }

    @Override
    @Transactional(rollbackFor={Exception.class}, propagation=Propagation.REQUIRED)
    public void updateEnableRefreshByTokenId(@NonNull EnableRefresh enableRefresh, @NonNull Long tokenId) throws DataAccessException {
        this.jdbcTemplate.update("update `" + this.authTokenTableName + "` set `enableRefresh` = ? where id = ?", new Object[]{enableRefresh.getCode(), tokenId});
    }

    private String encrypt(String text) {
        return text != null ? this.textEncryptor.encrypt(text) : null;
    }

    private final class AuthTokenPoMapper
    implements RowMapper<AuthTokenPo> {
        private AuthTokenPoMapper() {
        }

        public AuthTokenPo mapRow(@NonNull ResultSet rs, int rowNum) throws SQLException {
            return this.mapAuthToken(rs);
        }

        private AuthTokenPo mapAuthToken(ResultSet rs) throws SQLException {
            AuthTokenPo token = new AuthTokenPo();
            token.setId(Long.valueOf(rs.getLong("id")));
            int enableRefresh = rs.getInt("enableRefresh");
            token.setProviderId(rs.getString("providerId"));
            token.setAccessToken(this.decrypt(rs.getString("accessToken")));
            token.setExpireIn(rs.getInt("expireIn"));
            token.setRefreshTokenExpireIn(rs.getInt("refreshTokenExpireIn"));
            token.setRefreshToken(this.decrypt(rs.getString("refreshToken")));
            token.setUid(rs.getString("uid"));
            token.setOpenId(rs.getString("openId"));
            token.setAccessCode(this.decrypt(rs.getString("accessCode")));
            token.setUnionId(rs.getString("unionId"));
            token.setScope(rs.getString("scope"));
            token.setTokenType(rs.getString("tokenType"));
            token.setIdToken(this.decrypt(rs.getString("idToken")));
            token.setMacAlgorithm(rs.getString("macAlgorithm"));
            token.setMacKey(this.decrypt(rs.getString("macKey")));
            token.setCode(this.decrypt(rs.getString("code")));
            token.setOauthToken(this.decrypt(rs.getString("oauthToken")));
            token.setOauthTokenSecret(this.decrypt(rs.getString("oauthTokenSecret")));
            token.setUserId(rs.getString("userId"));
            token.setScreenName(rs.getString("screenName"));
            token.setOauthCallbackConfirmed(Boolean.valueOf(rs.getBoolean("oauthCallbackConfirmed")));
            token.setExpireTime(Long.valueOf(rs.getLong("expireTime")));
            return token;
        }

        private String decrypt(String encryptedText) {
            return encryptedText != null ? Auth2JdbcUsersConnectionTokenRepository.this.textEncryptor.decrypt(encryptedText) : null;
        }

        private Long expireTime(long expireTime) {
            return expireTime == 0L ? null : Long.valueOf(expireTime);
        }
    }
}

