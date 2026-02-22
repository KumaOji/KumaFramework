/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.boot.context.properties.ConfigurationProperties
 *  org.springframework.lang.NonNull
 */
package com.kuma.boot.security.spring.authentication.login.social.justauth.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.lang.NonNull;

@ConfigurationProperties(value="ums.repository")
public class RepositoryProperties {
    private String textEncryptorPassword = "7ca5d913a17b4942942d16a974e3fecc";
    private String textEncryptorSalt = "cd538b1b077542aca5f86942b6507fe2";
    private Boolean enableStartUpInitializeTable = Boolean.TRUE;
    private String queryDatabaseNameSql = "select database();";
    private String authTokenTableName = "auth_token";
    private String queryAuthTokenTableExistSql = "SELECT COUNT(1) FROM information_schema.tables WHERE table_name = '%s' AND table_schema = '%s'";
    private String createAuthTokenTableSql = "CREATE TABLE `%s` (\n  `id` bigint(20) NOT NULL AUTO_INCREMENT,\n  `enableRefresh` tinyint(1) NOT NULL DEFAULT '1' COMMENT '\u662f\u5426\u652f\u6301 refreshToken, \u9ed8\u8ba4: 1. 1 \u8868\u793a\u652f\u6301, 0 \u8868\u793a\u4e0d\u652f\u6301',\n  `providerId` varchar(20) DEFAULT NULL COMMENT '\u7b2c\u4e09\u65b9\u670d\u52a1\u5546,\u5982: qq,github',\n  `accessToken` varchar(512) COMMENT 'accessToken',\n  `expireIn` bigint(20) DEFAULT '-1' COMMENT 'accessToken \u8fc7\u671f\u65f6\u95f4, \u65e0\u8fc7\u671f\u65f6\u95f4\u9ed8\u8ba4\u4e3a -1',\n  `refreshTokenExpireIn` bigint(20) DEFAULT '-1' COMMENT 'refreshToken \u8fc7\u671f\u65f6\u95f4, \u65e0\u8fc7\u671f\u65f6\u95f4\u9ed8\u8ba4\u4e3a -1',\n  `refreshToken` varchar(512) COMMENT 'refreshToken',\n  `uid` varchar(20) COMMENT 'alipay userId',\n  `openId` varchar(256) COMMENT 'qq/mi/toutiao/wechatMp/wechatOpen/weibo/jd/kujiale/dingTalk/douyin/feishu',\n  `accessCode` varchar(256) COMMENT 'dingTalk, taobao \u9644\u5e26\u5c5e\u6027',\n  `unionId` varchar(256) COMMENT 'QQ\u9644\u5e26\u5c5e\u6027',\n  `scope` varchar(256) COMMENT 'Google\u9644\u5e26\u5c5e\u6027',\n  `tokenType` varchar(20) COMMENT 'Google\u9644\u5e26\u5c5e\u6027',\n  `idToken` varchar(256) COMMENT 'Google\u9644\u5e26\u5c5e\u6027',\n  `macAlgorithm` varchar(20) COMMENT '\u5c0f\u7c73\u9644\u5e26\u5c5e\u6027',\n  `macKey` varchar(256) COMMENT '\u5c0f\u7c73\u9644\u5e26\u5c5e\u6027',\n  `code` varchar(256) COMMENT '\u4f01\u4e1a\u5fae\u4fe1\u9644\u5e26\u5c5e\u6027',\n  `oauthToken` varchar(256) COMMENT 'Twitter\u9644\u5e26\u5c5e\u6027',\n  `oauthTokenSecret` varchar(256) COMMENT 'Twitter\u9644\u5e26\u5c5e\u6027',\n  `userId` varchar(64) COMMENT 'Twitter\u9644\u5e26\u5c5e\u6027',\n  `screenName` varchar(64) COMMENT 'Twitter\u9644\u5e26\u5c5e\u6027',\n  `oauthCallbackConfirmed` varchar(64) COMMENT 'Twitter\u9644\u5e26\u5c5e\u6027',\n  `expireTime` bigint(20) DEFAULT '-1' COMMENT '\u8fc7\u671f\u65f6\u95f4, \u57fa\u4e8e 1970-01-01T00:00:00Z, \u65e0\u8fc7\u671f\u65f6\u95f4\u9ed8\u8ba4\u4e3a -1',\n  PRIMARY KEY (`id`)\n) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;";
    private String userConnectionTableName = "user_connection";
    private String userIdColumnName = "userId";
    private String providerIdColumnName = "providerId";
    private String providerUserIdColumnName = "providerUserId";
    private String rankColumnName = "`rank`";
    private String displayNameColumnName = "displayName";
    private String profileUrlColumnName = "profileUrl";
    private String imageUrlColumnName = "imageUrl";
    private String accessTokenColumnName = "accessToken";
    private String tokenIdColumnName = "tokenId";
    private String refreshTokenColumnName = "refreshToken";
    private String expireTimeColumnName = "expireTime";
    private String creatUserConnectionTableSql = "CREATE TABLE %s (\t%s varchar(36) NOT NULL COMMENT '\u672c\u5730\u7528\u6237id',\n\t%s varchar(20) NOT NULL COMMENT '\u7b2c\u4e09\u65b9\u670d\u52a1\u5546',\n\t%s varchar(36) NOT NULL COMMENT '\u7b2c\u4e09\u65b9\u7528\u6237id',\n\t%s int(11) NOT NULL COMMENT 'userId \u7ed1\u5b9a\u540c\u4e00\u4e2a providerId \u7684\u6392\u5e8f',\n\t%s varchar(64) COMMENT '\u7b2c\u4e09\u65b9\u7528\u6237\u540d',\n\t%s varchar(256) COMMENT '\u4e3b\u9875',\n\t%s varchar(256) COMMENT '\u5934\u50cf',\n\t%s varchar(512) NOT NULL COMMENT 'accessToken',\n\t%s bigint(20) COMMENT 'auth_token.id',\n\t%s varchar(512) COMMENT 'refreshToken',\n\t%s bigint(20) DEFAULT '-1' COMMENT '\u8fc7\u671f\u65f6\u95f4, \u57fa\u4e8e 1970-01-01T00:00:00Z, \u65e0\u8fc7\u671f\u65f6\u95f4\u9ed8\u8ba4\u4e3a -1',\n\tPRIMARY KEY (%s, %s, %s),\n\tunique KEY `idx_userId_providerId_rank`(%s, %s, %s),\n\tKEY `idx_providerId_providerUserId_rank` (%s, %s, %s),\n\tKEY `idx_tokenId` (%s)\n\t) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;";
    private String queryUserConnectionTableExistSql = "SELECT COUNT(1) FROM information_schema.tables WHERE table_schema='%s' AND table_name = '%s'";
    private String findUserIdsWithConnectionSql = "select %s from %s where %s = ? and %s = ?";
    private String findUserIdsConnectedToSql = "select %s from %S where %s = :%s and %s in (:%s)";
    private String selectFromUserConnectionSql = "select %s, %s, %s, %s, %s, %s, %s, %s, %s, %s from %s";
    private String updateConnectionSql = "update %s set %s = ?, %s = ?, %s = ?, %s = ?, %s = ?, %s = ?, %s = ? where %s = ? and %s = ? and %s = ?";
    private String addConnectionSql = "insert into %s(%s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s) values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
    private String addConnectionQueryForRankSql = "select coalesce(max(%s) + 1, 1) as %s from %s where %s = ? and %s = ?";
    private String removeConnectionsSql = "delete from %s where %s = ? and %s = ?";
    private String removeConnectionSql = "delete from %s where %s = ? and %s = ? and %s = ?";

    public String getQueryAuthTokenTableExistSql(@NonNull String database) {
        return String.format(this.queryAuthTokenTableExistSql, this.authTokenTableName, database);
    }

    public String getCreateAuthTokenTableSql() {
        return String.format(this.createAuthTokenTableSql, this.authTokenTableName);
    }

    public String getCreatUserConnectionTableSql() {
        return String.format(this.creatUserConnectionTableSql, this.userConnectionTableName, this.userIdColumnName, this.providerIdColumnName, this.providerUserIdColumnName, this.rankColumnName, this.displayNameColumnName, this.profileUrlColumnName, this.imageUrlColumnName, this.accessTokenColumnName, this.tokenIdColumnName, this.refreshTokenColumnName, this.expireTimeColumnName, this.userIdColumnName, this.providerIdColumnName, this.providerUserIdColumnName, this.userIdColumnName, this.providerIdColumnName, this.rankColumnName, this.providerIdColumnName, this.providerUserIdColumnName, this.rankColumnName, this.tokenIdColumnName);
    }

    public String getQueryUserConnectionTableExistSql(String databaseName) {
        return String.format(this.queryUserConnectionTableExistSql, databaseName, this.userConnectionTableName);
    }

    public String getFindUserIdsWithConnectionSql() {
        return String.format(this.findUserIdsWithConnectionSql, this.userIdColumnName, this.userConnectionTableName, this.providerIdColumnName, this.providerUserIdColumnName);
    }

    public String getFindUserIdsConnectedToSql() {
        return String.format(this.findUserIdsConnectedToSql, this.userIdColumnName, this.userConnectionTableName, this.providerIdColumnName, this.providerIdColumnName, this.providerUserIdColumnName, this.providerUserIdColumnName);
    }

    public String getSelectFromUserConnectionSql() {
        return String.format(this.selectFromUserConnectionSql, this.userIdColumnName, this.providerIdColumnName, this.providerUserIdColumnName, this.displayNameColumnName, this.profileUrlColumnName, this.imageUrlColumnName, this.accessTokenColumnName, this.tokenIdColumnName, this.refreshTokenColumnName, this.expireTimeColumnName, this.userConnectionTableName);
    }

    public String getUpdateConnectionSql() {
        return String.format(this.updateConnectionSql, this.userConnectionTableName, this.displayNameColumnName, this.profileUrlColumnName, this.imageUrlColumnName, this.accessTokenColumnName, this.tokenIdColumnName, this.refreshTokenColumnName, this.expireTimeColumnName, this.userIdColumnName, this.providerIdColumnName, this.providerUserIdColumnName);
    }

    public String getAddConnectionSql() {
        return String.format(this.addConnectionSql, this.userConnectionTableName, this.userIdColumnName, this.providerIdColumnName, this.providerUserIdColumnName, this.rankColumnName, this.displayNameColumnName, this.profileUrlColumnName, this.imageUrlColumnName, this.accessTokenColumnName, this.tokenIdColumnName, this.refreshTokenColumnName, this.expireTimeColumnName);
    }

    public String getAddConnectionQueryForRankSql() {
        return String.format(this.addConnectionQueryForRankSql, this.rankColumnName, this.rankColumnName, this.userConnectionTableName, this.userIdColumnName, this.providerIdColumnName);
    }

    public String getRemoveConnectionsSql() {
        return String.format(this.removeConnectionsSql, this.userConnectionTableName, this.userIdColumnName, this.providerIdColumnName);
    }

    public String getRemoveConnectionSql() {
        return String.format(this.removeConnectionSql, this.userConnectionTableName, this.userIdColumnName, this.providerIdColumnName, this.providerUserIdColumnName);
    }

    public String getTextEncryptorPassword() {
        return this.textEncryptorPassword;
    }

    public void setTextEncryptorPassword(String textEncryptorPassword) {
        this.textEncryptorPassword = textEncryptorPassword;
    }

    public String getTextEncryptorSalt() {
        return this.textEncryptorSalt;
    }

    public void setTextEncryptorSalt(String textEncryptorSalt) {
        this.textEncryptorSalt = textEncryptorSalt;
    }

    public Boolean getEnableStartUpInitializeTable() {
        return this.enableStartUpInitializeTable;
    }

    public void setEnableStartUpInitializeTable(Boolean enableStartUpInitializeTable) {
        this.enableStartUpInitializeTable = enableStartUpInitializeTable;
    }

    public String getQueryDatabaseNameSql() {
        return this.queryDatabaseNameSql;
    }

    public void setQueryDatabaseNameSql(String queryDatabaseNameSql) {
        this.queryDatabaseNameSql = queryDatabaseNameSql;
    }

    public String getAuthTokenTableName() {
        return this.authTokenTableName;
    }

    public void setAuthTokenTableName(String authTokenTableName) {
        this.authTokenTableName = authTokenTableName;
    }

    public String getQueryAuthTokenTableExistSql() {
        return this.queryAuthTokenTableExistSql;
    }

    public void setQueryAuthTokenTableExistSql(String queryAuthTokenTableExistSql) {
        this.queryAuthTokenTableExistSql = queryAuthTokenTableExistSql;
    }

    public void setCreateAuthTokenTableSql(String createAuthTokenTableSql) {
        this.createAuthTokenTableSql = createAuthTokenTableSql;
    }

    public String getUserConnectionTableName() {
        return this.userConnectionTableName;
    }

    public void setUserConnectionTableName(String userConnectionTableName) {
        this.userConnectionTableName = userConnectionTableName;
    }

    public String getUserIdColumnName() {
        return this.userIdColumnName;
    }

    public void setUserIdColumnName(String userIdColumnName) {
        this.userIdColumnName = userIdColumnName;
    }

    public String getProviderIdColumnName() {
        return this.providerIdColumnName;
    }

    public void setProviderIdColumnName(String providerIdColumnName) {
        this.providerIdColumnName = providerIdColumnName;
    }

    public String getProviderUserIdColumnName() {
        return this.providerUserIdColumnName;
    }

    public void setProviderUserIdColumnName(String providerUserIdColumnName) {
        this.providerUserIdColumnName = providerUserIdColumnName;
    }

    public String getRankColumnName() {
        return this.rankColumnName;
    }

    public void setRankColumnName(String rankColumnName) {
        this.rankColumnName = rankColumnName;
    }

    public String getDisplayNameColumnName() {
        return this.displayNameColumnName;
    }

    public void setDisplayNameColumnName(String displayNameColumnName) {
        this.displayNameColumnName = displayNameColumnName;
    }

    public String getProfileUrlColumnName() {
        return this.profileUrlColumnName;
    }

    public void setProfileUrlColumnName(String profileUrlColumnName) {
        this.profileUrlColumnName = profileUrlColumnName;
    }

    public String getImageUrlColumnName() {
        return this.imageUrlColumnName;
    }

    public void setImageUrlColumnName(String imageUrlColumnName) {
        this.imageUrlColumnName = imageUrlColumnName;
    }

    public String getAccessTokenColumnName() {
        return this.accessTokenColumnName;
    }

    public void setAccessTokenColumnName(String accessTokenColumnName) {
        this.accessTokenColumnName = accessTokenColumnName;
    }

    public String getTokenIdColumnName() {
        return this.tokenIdColumnName;
    }

    public void setTokenIdColumnName(String tokenIdColumnName) {
        this.tokenIdColumnName = tokenIdColumnName;
    }

    public String getRefreshTokenColumnName() {
        return this.refreshTokenColumnName;
    }

    public void setRefreshTokenColumnName(String refreshTokenColumnName) {
        this.refreshTokenColumnName = refreshTokenColumnName;
    }

    public String getExpireTimeColumnName() {
        return this.expireTimeColumnName;
    }

    public void setExpireTimeColumnName(String expireTimeColumnName) {
        this.expireTimeColumnName = expireTimeColumnName;
    }

    public void setCreatUserConnectionTableSql(String creatUserConnectionTableSql) {
        this.creatUserConnectionTableSql = creatUserConnectionTableSql;
    }

    public String getQueryUserConnectionTableExistSql() {
        return this.queryUserConnectionTableExistSql;
    }

    public void setQueryUserConnectionTableExistSql(String queryUserConnectionTableExistSql) {
        this.queryUserConnectionTableExistSql = queryUserConnectionTableExistSql;
    }

    public void setFindUserIdsWithConnectionSql(String findUserIdsWithConnectionSql) {
        this.findUserIdsWithConnectionSql = findUserIdsWithConnectionSql;
    }

    public void setFindUserIdsConnectedToSql(String findUserIdsConnectedToSql) {
        this.findUserIdsConnectedToSql = findUserIdsConnectedToSql;
    }

    public void setSelectFromUserConnectionSql(String selectFromUserConnectionSql) {
        this.selectFromUserConnectionSql = selectFromUserConnectionSql;
    }

    public void setUpdateConnectionSql(String updateConnectionSql) {
        this.updateConnectionSql = updateConnectionSql;
    }

    public void setAddConnectionSql(String addConnectionSql) {
        this.addConnectionSql = addConnectionSql;
    }

    public void setAddConnectionQueryForRankSql(String addConnectionQueryForRankSql) {
        this.addConnectionQueryForRankSql = addConnectionQueryForRankSql;
    }

    public void setRemoveConnectionsSql(String removeConnectionsSql) {
        this.removeConnectionsSql = removeConnectionsSql;
    }

    public void setRemoveConnectionSql(String removeConnectionSql) {
        this.removeConnectionSql = removeConnectionSql;
    }
}

