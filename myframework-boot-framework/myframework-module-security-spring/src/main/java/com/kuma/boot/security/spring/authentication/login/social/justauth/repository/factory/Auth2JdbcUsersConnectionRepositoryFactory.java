/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  org.springframework.jdbc.core.JdbcTemplate
 *  org.springframework.security.crypto.encrypt.TextEncryptor
 */
package com.kuma.boot.security.spring.authentication.login.social.justauth.repository.factory;

import com.kuma.boot.security.spring.authentication.login.social.justauth.properties.RepositoryProperties;
import com.kuma.boot.security.spring.authentication.login.social.justauth.repository.UsersConnectionRepository;
import com.kuma.boot.security.spring.authentication.login.social.justauth.repository.jdbc.Auth2JdbcUsersConnectionRepository;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.encrypt.TextEncryptor;

public class Auth2JdbcUsersConnectionRepositoryFactory
implements UsersConnectionRepositoryFactory {
    @Override
    public UsersConnectionRepository getUsersConnectionRepository(JdbcTemplate auth2UserConnectionJdbcTemplate, TextEncryptor textEncryptor, RepositoryProperties repositoryProperties) {
        return new Auth2JdbcUsersConnectionRepository(auth2UserConnectionJdbcTemplate, textEncryptor, repositoryProperties);
    }
}

