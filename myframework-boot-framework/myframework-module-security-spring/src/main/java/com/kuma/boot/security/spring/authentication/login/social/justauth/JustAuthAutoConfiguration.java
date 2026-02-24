/*
 * Copyright (c) 2020-2030, Kuma (2569277704@qq.com & https://blog.kumacloud.top/).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.kuma.boot.security.spring.authentication.login.social.justauth;

import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.boot.security.spring.authentication.login.social.justauth.consts.SecurityConstants;
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

/**
 * OAuth2 grant flow auto configuration
 *
 * @author YongWu zheng
 * @version V2.0  Created by 2020/10/5 21:47
 */
@SuppressWarnings({"AlibabaClassNamingShouldBeCamel"})
// @AutoConfigureAfter(value = {Auth2PropertiesAutoConfiguration.class})
// @Configuration
public class JustAuthAutoConfiguration implements InitializingBean {

    private final RepositoryProperties repositoryProperties;
    private final JustAuthProperties justAuthProperties;
    private final DataSource dataSource;

    public JustAuthAutoConfiguration(
            RepositoryProperties repositoryProperties,
            JustAuthProperties justAuthProperties,
            DataSource dataSource) {
        this.repositoryProperties = repositoryProperties;
        this.justAuthProperties = justAuthProperties;
        this.dataSource = dataSource;
    }

    @Bean
    @ConditionalOnMissingBean(
            type = {
                    "top.dcenter.ums.security.core.oauth.userdetails.converter.AuthenticationToUserDetailsConverter"
            })
    public AuthenticationToUserDetailsConverter authenticationToUserDetailsConverter() {
        return new Oauth2TokenAuthenticationTokenToUserConverter();
    }

    @Bean
    @ConditionalOnMissingBean(type = "top.dcenter.ums.security.core.oauth.service.Auth2UserService")
    public Auth2UserService auth2UserService() {
        return new DefaultAuth2UserService();
    }

    @Bean
    @ConditionalOnMissingBean(type = "org.springframework.jdbc.core.JdbcTemplate")
    @ConditionalOnProperty(
            prefix = "ums.oauth",
            name = "enable-user-connection-and-auth-token-table",
            havingValue = "true")
    public JdbcTemplate auth2UserConnectionJdbcTemplate() {
        return new JdbcTemplate(dataSource);
    }

    @Bean
    @ConditionalOnProperty(
            prefix = "ums.oauth",
            name = "enable-user-connection-and-auth-token-table",
            havingValue = "true")
    public UsersConnectionRepository usersConnectionRepository(
            UsersConnectionRepositoryFactory usersConnectionRepositoryFactory,
            JdbcTemplate auth2UserConnectionJdbcTemplate,
            @Qualifier("connectionTextEncryptor") TextEncryptor connectionTextEncryptor) {
        return usersConnectionRepositoryFactory.getUsersConnectionRepository(
                auth2UserConnectionJdbcTemplate, connectionTextEncryptor, repositoryProperties);
    }

    @Bean
    @ConditionalOnMissingBean(
            type = {
                    "top.dcenter.ums.security.core.oauth.repository.factory.UsersConnectionRepositoryFactory"
            })
    @ConditionalOnProperty(
            prefix = "ums.oauth",
            name = "enable-user-connection-and-auth-token-table",
            havingValue = "true")
    public UsersConnectionRepositoryFactory usersConnectionRepositoryFactory() {
        return new Auth2JdbcUsersConnectionRepositoryFactory();
    }

    @Bean
    public TextEncryptor connectionTextEncryptor(RepositoryProperties repositoryProperties) {
        return Encryptors.text(
                repositoryProperties.getTextEncryptorPassword(),
                repositoryProperties.getTextEncryptorSalt());
    }

    @Bean
    @ConditionalOnMissingBean(type = "top.dcenter.ums.security.core.oauth.signup.ConnectionService")
    @ConditionalOnProperty(
            prefix = "ums.oauth",
            name = "enable-user-connection-and-auth-token-table",
            havingValue = "true")
    public ConnectionService connectionSignUp(
            UmsUserDetailsService userDetailsService,
            @Autowired(required = false)
            UsersConnectionTokenRepository usersConnectionTokenRepository,
            UsersConnectionRepository usersConnectionRepository,
            @Autowired(required = false) Auth2StateCoder auth2StateCoder) {
        return new DefaultConnectionService(
                userDetailsService,
                justAuthProperties,
                usersConnectionRepository,
                usersConnectionTokenRepository,
                auth2StateCoder);
    }

    @Bean
    public JustAuthRequestHolder auth2RequestHolder() {
        return JustAuthRequestHolder.getInstance();
    }

    @SuppressWarnings("AlibabaMethodTooLong")
    @Override
    public void afterPropertiesSet() throws Exception {

        if (!repositoryProperties.getEnableStartUpInitializeTable()
                || !justAuthProperties.getEnableUserConnectionAndAuthTokenTable()) {
            // 不支持在启动时检查并自动创建 userConnectionTableName 与 authTokenTableName, 直接退出
            return;
        }

        // ====== 是否要初始化数据库 ======
        // 如果 Auth2JdbcUsersConnectionRepository, Auth2JdbcUsersConnectionTokenRepository 所需的表
        // user_connection, 未创建则创建它
        try (Connection connection = dataSource.getConnection()) {
            if (connection == null) {
                LogUtils.error(
                        "错误: 初始化第三方登录的 {} 用户表时发生错误",
                        repositoryProperties.getUserConnectionTableName());
                throw new Exception(
                        String.format(
                                "初始化第三方登录的 %s 用户表时发生错误",
                                repositoryProperties.getUserConnectionTableName()));
            }

            String database;

            try (final PreparedStatement preparedStatement =
                         connection.prepareStatement(
                                 repositoryProperties.getQueryDatabaseNameSql());
                 ResultSet resultSet = preparedStatement.executeQuery()) {
                resultSet.next();
                database =
                        resultSet.getString(
                                SecurityConstants.QUERY_TABLE_EXIST_SQL_RESULT_SET_COLUMN_INDEX);
            }

            if (StringUtils.hasText(database)) {
                String queryUserConnectionTableExistSql =
                        repositoryProperties.getQueryUserConnectionTableExistSql(database);

                try (final PreparedStatement preparedStatement1 =
                             connection.prepareStatement(queryUserConnectionTableExistSql);
                     ResultSet resultSet = preparedStatement1.executeQuery()) {
                    resultSet.next();
                    int tableCount =
                            resultSet.getInt(
                                    SecurityConstants
                                            .QUERY_TABLE_EXIST_SQL_RESULT_SET_COLUMN_INDEX);
                    if (tableCount < 1) {
                        String creatUserConnectionTableSql =
                                repositoryProperties.getCreatUserConnectionTableSql();
                        try (final PreparedStatement preparedStatement =
                                     connection.prepareStatement(creatUserConnectionTableSql)) {
                            preparedStatement.executeUpdate();
                            LogUtils.info(
                                    "{} 表创建成功，SQL：{}",
                                    repositoryProperties.getUserConnectionTableName(),
                                    creatUserConnectionTableSql);
                            if (!connection.getAutoCommit()) {
                                connection.commit();
                            }
                        }
                    }
                }

                // 不支持第三方 token 表(auth_token) 直接退出
                if (!justAuthProperties.getEnableAuthTokenTable()) {
                    return;
                }
                //noinspection TryStatementWithMultipleResources,TryStatementWithMultipleResources
                try (final PreparedStatement preparedStatement2 =
                             connection.prepareStatement(
                                     repositoryProperties.getQueryAuthTokenTableExistSql(
                                             database));
                     ResultSet resultSet = preparedStatement2.executeQuery()) {
                    resultSet.next();
                    int tableCount =
                            resultSet.getInt(
                                    SecurityConstants
                                            .QUERY_TABLE_EXIST_SQL_RESULT_SET_COLUMN_INDEX);
                    if (tableCount < 1) {
                        String createAuthTokenTableSql =
                                repositoryProperties.getCreateAuthTokenTableSql();
                        connection.prepareStatement(createAuthTokenTableSql).executeUpdate();
                        LogUtils.info(
                                "{} 表创建成功，SQL：{}",
                                repositoryProperties.getAuthTokenTableName(),
                                createAuthTokenTableSql);
                        if (!connection.getAutoCommit()) {
                            connection.commit();
                        }
                    }
                }
            } else {
                LogUtils.error(
                        "错误: 初始化第三方登录的 {} 用户表时发生错误",
                        repositoryProperties.getUserConnectionTableName());
                throw new Exception(
                        String.format(
                                "初始化第三方登录的 %s 用户表时发生错误",
                                repositoryProperties.getUserConnectionTableName()));
            }
        }
    }

    @Configuration
    @ConditionalOnProperty(
            prefix = "ums.oauth",
            name = "enable-user-connection-and-auth-token-table",
            havingValue = "true")
    static class JobAutoConfiguration {

        private final JustAuthProperties justAuthProperties;

        public JobAutoConfiguration(JustAuthProperties justAuthProperties) {
            this.justAuthProperties = justAuthProperties;
        }

        //		@Bean
        //		@ConditionalOnProperty(prefix = "ums.oauth", name = "enable-refresh-token-job",
        // havingValue = "true")
        //		public RefreshTokenJob refreshTokenJob(@Autowired(required = false)
        //											   UsersConnectionTokenRepository usersConnectionTokenRepository,
        //											   UsersConnectionRepository usersConnectionRepository,
        //											   @Qualifier("refreshTokenTaskExecutor") ExecutorService
        // refreshTokenTaskExecutor) {
        //			return new RefreshTokenJobImpl(usersConnectionRepository,
        // usersConnectionTokenRepository,
        //				auth2Properties, refreshTokenTaskExecutor);
        //		}
    }

    @Configuration
    @ConditionalOnProperty(
            prefix = "ums.oauth",
            name = "enable-user-connection-and-auth-token-table",
            havingValue = "true")
    static class AuthTokenAutoConfiguration {

        private final RepositoryProperties repositoryProperties;

        public AuthTokenAutoConfiguration(RepositoryProperties repositoryProperties) {
            this.repositoryProperties = repositoryProperties;
        }

        @Bean
        @ConditionalOnMissingBean(
                type = {
                        "top.dcenter.ums.security.core.oauth.repository.UsersConnectionTokenRepository"
                })
        @ConditionalOnProperty(
                prefix = "ums.oauth",
                name = "enable-auth-token-table",
                havingValue = "true")
        public UsersConnectionTokenRepository usersConnectionTokenRepository(
                @Qualifier("connectionTextEncryptor") TextEncryptor connectionTextEncryptor,
                JdbcTemplate auth2UserConnectionJdbcTemplate) {
            return new Auth2JdbcUsersConnectionTokenRepository(
                    auth2UserConnectionJdbcTemplate,
                    connectionTextEncryptor,
                    repositoryProperties.getAuthTokenTableName());
        }
    }
}
