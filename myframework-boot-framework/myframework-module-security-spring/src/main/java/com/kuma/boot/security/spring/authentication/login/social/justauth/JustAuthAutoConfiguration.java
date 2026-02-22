/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  com.kuma.boot.common.utils.log.LogUtils
 *  org.springframework.beans.factory.InitializingBean
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.beans.factory.annotation.Qualifier
 *  org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
 *  org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
 *  org.springframework.context.annotation.Bean
 *  org.springframework.context.annotation.Configuration
 *  org.springframework.jdbc.core.JdbcTemplate
 *  org.springframework.security.crypto.encrypt.Encryptors
 *  org.springframework.security.crypto.encrypt.TextEncryptor
 *  org.springframework.util.StringUtils
 */
package com.kuma.boot.security.spring.authentication.login.social.justauth;

import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.boot.security.spring.authentication.login.social.justauth.properties.JustAuthProperties;
import com.kuma.boot.security.spring.authentication.login.social.justauth.properties.RepositoryProperties;
import com.kuma.boot.security.spring.authentication.login.social.justauth.repository.UsersConnectionRepository;
import com.kuma.boot.security.spring.authentication.login.social.justauth.repository.UsersConnectionTokenRepository;
import com.kuma.boot.security.spring.authentication.login.social.justauth.repository.factory.Auth2JdbcUsersConnectionRepositoryFactory;
import com.kuma.boot.security.spring.authentication.login.social.justauth.repository.factory.UsersConnectionRepositoryFactory;
import com.kuma.boot.security.spring.authentication.login.social.justauth.repository.jdbc.Auth2JdbcUsersConnectionTokenRepository;
import com.kuma.boot.security.spring.authentication.login.social.justauth.service.Auth2StateCoder;
import com.kuma.boot.security.spring.authentication.login.social.justauth.service.Auth2UserService;
import com.kuma.boot.security.spring.authentication.login.social.justauth.service.ConnectionService;
import com.kuma.boot.security.spring.authentication.login.social.justauth.service.DefaultAuth2UserService;
import com.kuma.boot.security.spring.authentication.login.social.justauth.service.DefaultConnectionService;
import com.kuma.boot.security.spring.authentication.login.social.justauth.service.UmsUserDetailsService;
import com.kuma.boot.security.spring.authentication.login.social.justauth.userdetails.converter.AuthenticationToUserDetailsConverter;
import com.kuma.boot.security.spring.authentication.login.social.justauth.userdetails.converter.Oauth2TokenAuthenticationTokenToUserConverter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javax.sql.DataSource;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.encrypt.Encryptors;
import org.springframework.security.crypto.encrypt.TextEncryptor;
import org.springframework.util.StringUtils;

public class JustAuthAutoConfiguration
implements InitializingBean {
    private final RepositoryProperties repositoryProperties;
    private final JustAuthProperties justAuthProperties;
    private final DataSource dataSource;

    public JustAuthAutoConfiguration(RepositoryProperties repositoryProperties, JustAuthProperties justAuthProperties, DataSource dataSource) {
        this.repositoryProperties = repositoryProperties;
        this.justAuthProperties = justAuthProperties;
        this.dataSource = dataSource;
    }

    @Bean
    @ConditionalOnMissingBean(type={"top.dcenter.ums.security.core.oauth.userdetails.converter.AuthenticationToUserDetailsConverter"})
    public AuthenticationToUserDetailsConverter authenticationToUserDetailsConverter() {
        return new Oauth2TokenAuthenticationTokenToUserConverter();
    }

    @Bean
    @ConditionalOnMissingBean(type={"top.dcenter.ums.security.core.oauth.service.Auth2UserService"})
    public Auth2UserService auth2UserService() {
        return new DefaultAuth2UserService();
    }

    @Bean
    @ConditionalOnMissingBean(type={"org.springframework.jdbc.core.JdbcTemplate"})
    @ConditionalOnProperty(prefix="ums.oauth", name={"enable-user-connection-and-auth-token-table"}, havingValue="true")
    public JdbcTemplate auth2UserConnectionJdbcTemplate() {
        return new JdbcTemplate(this.dataSource);
    }

    @Bean
    @ConditionalOnProperty(prefix="ums.oauth", name={"enable-user-connection-and-auth-token-table"}, havingValue="true")
    public UsersConnectionRepository usersConnectionRepository(UsersConnectionRepositoryFactory usersConnectionRepositoryFactory, JdbcTemplate auth2UserConnectionJdbcTemplate, @Qualifier(value="connectionTextEncryptor") TextEncryptor connectionTextEncryptor) {
        return usersConnectionRepositoryFactory.getUsersConnectionRepository(auth2UserConnectionJdbcTemplate, connectionTextEncryptor, this.repositoryProperties);
    }

    @Bean
    @ConditionalOnMissingBean(type={"top.dcenter.ums.security.core.oauth.repository.factory.UsersConnectionRepositoryFactory"})
    @ConditionalOnProperty(prefix="ums.oauth", name={"enable-user-connection-and-auth-token-table"}, havingValue="true")
    public UsersConnectionRepositoryFactory usersConnectionRepositoryFactory() {
        return new Auth2JdbcUsersConnectionRepositoryFactory();
    }

    @Bean
    public TextEncryptor connectionTextEncryptor(RepositoryProperties repositoryProperties) {
        return Encryptors.text((CharSequence)repositoryProperties.getTextEncryptorPassword(), (CharSequence)repositoryProperties.getTextEncryptorSalt());
    }

    @Bean
    @ConditionalOnMissingBean(type={"top.dcenter.ums.security.core.oauth.signup.ConnectionService"})
    @ConditionalOnProperty(prefix="ums.oauth", name={"enable-user-connection-and-auth-token-table"}, havingValue="true")
    public ConnectionService connectionSignUp(UmsUserDetailsService userDetailsService, @Autowired(required=false) UsersConnectionTokenRepository usersConnectionTokenRepository, UsersConnectionRepository usersConnectionRepository, @Autowired(required=false) Auth2StateCoder auth2StateCoder) {
        return new DefaultConnectionService(userDetailsService, this.justAuthProperties, usersConnectionRepository, usersConnectionTokenRepository, auth2StateCoder);
    }

    @Bean
    public JustAuthRequestHolder auth2RequestHolder() {
        return JustAuthRequestHolder.getInstance();
    }

    public void afterPropertiesSet() throws Exception {
        block58: {
            if (!this.repositoryProperties.getEnableStartUpInitializeTable().booleanValue() || !this.justAuthProperties.getEnableUserConnectionAndAuthTokenTable().booleanValue()) {
                return;
            }
            try (Connection connection = this.dataSource.getConnection();){
                String database;
                if (connection == null) {
                    LogUtils.error((String)"\u9519\u8bef: \u521d\u59cb\u5316\u7b2c\u4e09\u65b9\u767b\u5f55\u7684 {} \u7528\u6237\u8868\u65f6\u53d1\u751f\u9519\u8bef", (Object[])new Object[]{this.repositoryProperties.getUserConnectionTableName()});
                    throw new Exception(String.format("\u521d\u59cb\u5316\u7b2c\u4e09\u65b9\u767b\u5f55\u7684 %s \u7528\u6237\u8868\u65f6\u53d1\u751f\u9519\u8bef", this.repositoryProperties.getUserConnectionTableName()));
                }
                try (PreparedStatement preparedStatement = connection.prepareStatement(this.repositoryProperties.getQueryDatabaseNameSql());
                     ResultSet resultSet = preparedStatement.executeQuery();){
                    resultSet.next();
                    database = resultSet.getString(1);
                }
                if (StringUtils.hasText((String)database)) {
                    int tableCount;
                    ResultSet resultSet;
                    block57: {
                        String queryUserConnectionTableExistSql = this.repositoryProperties.getQueryUserConnectionTableExistSql(database);
                        try (PreparedStatement preparedStatement1 = connection.prepareStatement(queryUserConnectionTableExistSql);){
                            resultSet = preparedStatement1.executeQuery();
                            try {
                                resultSet.next();
                                tableCount = resultSet.getInt(1);
                                if (tableCount >= 1) break block57;
                                String creatUserConnectionTableSql = this.repositoryProperties.getCreatUserConnectionTableSql();
                                try (PreparedStatement preparedStatement = connection.prepareStatement(creatUserConnectionTableSql);){
                                    preparedStatement.executeUpdate();
                                    LogUtils.info((String)"{} \u8868\u521b\u5efa\u6210\u529f\uff0cSQL\uff1a{}", (Object[])new Object[]{this.repositoryProperties.getUserConnectionTableName(), creatUserConnectionTableSql});
                                    if (!connection.getAutoCommit()) {
                                        connection.commit();
                                    }
                                }
                            }
                            finally {
                                if (resultSet != null) {
                                    resultSet.close();
                                }
                            }
                        }
                    }
                    if (!this.justAuthProperties.getEnableAuthTokenTable().booleanValue()) {
                        return;
                    }
                    try (PreparedStatement preparedStatement2 = connection.prepareStatement(this.repositoryProperties.getQueryAuthTokenTableExistSql(database));){
                        resultSet = preparedStatement2.executeQuery();
                        try {
                            resultSet.next();
                            tableCount = resultSet.getInt(1);
                            if (tableCount < 1) {
                                String createAuthTokenTableSql = this.repositoryProperties.getCreateAuthTokenTableSql();
                                connection.prepareStatement(createAuthTokenTableSql).executeUpdate();
                                LogUtils.info((String)"{} \u8868\u521b\u5efa\u6210\u529f\uff0cSQL\uff1a{}", (Object[])new Object[]{this.repositoryProperties.getAuthTokenTableName(), createAuthTokenTableSql});
                                if (!connection.getAutoCommit()) {
                                    connection.commit();
                                }
                            }
                            break block58;
                        }
                        finally {
                            if (resultSet != null) {
                                resultSet.close();
                            }
                        }
                    }
                }
                LogUtils.error((String)"\u9519\u8bef: \u521d\u59cb\u5316\u7b2c\u4e09\u65b9\u767b\u5f55\u7684 {} \u7528\u6237\u8868\u65f6\u53d1\u751f\u9519\u8bef", (Object[])new Object[]{this.repositoryProperties.getUserConnectionTableName()});
                throw new Exception(String.format("\u521d\u59cb\u5316\u7b2c\u4e09\u65b9\u767b\u5f55\u7684 %s \u7528\u6237\u8868\u65f6\u53d1\u751f\u9519\u8bef", this.repositoryProperties.getUserConnectionTableName()));
            }
        }
    }

    @Configuration
    @ConditionalOnProperty(prefix="ums.oauth", name={"enable-user-connection-and-auth-token-table"}, havingValue="true")
    static class AuthTokenAutoConfiguration {
        private final RepositoryProperties repositoryProperties;

        public AuthTokenAutoConfiguration(RepositoryProperties repositoryProperties) {
            this.repositoryProperties = repositoryProperties;
        }

        @Bean
        @ConditionalOnMissingBean(type={"top.dcenter.ums.security.core.oauth.repository.UsersConnectionTokenRepository"})
        @ConditionalOnProperty(prefix="ums.oauth", name={"enable-auth-token-table"}, havingValue="true")
        public UsersConnectionTokenRepository usersConnectionTokenRepository(@Qualifier(value="connectionTextEncryptor") TextEncryptor connectionTextEncryptor, JdbcTemplate auth2UserConnectionJdbcTemplate) {
            return new Auth2JdbcUsersConnectionTokenRepository(auth2UserConnectionJdbcTemplate, connectionTextEncryptor, this.repositoryProperties.getAuthTokenTableName());
        }
    }

    @Configuration
    @ConditionalOnProperty(prefix="ums.oauth", name={"enable-user-connection-and-auth-token-table"}, havingValue="true")
    static class JobAutoConfiguration {
        private final JustAuthProperties justAuthProperties;

        public JobAutoConfiguration(JustAuthProperties justAuthProperties) {
            this.justAuthProperties = justAuthProperties;
        }
    }
}

