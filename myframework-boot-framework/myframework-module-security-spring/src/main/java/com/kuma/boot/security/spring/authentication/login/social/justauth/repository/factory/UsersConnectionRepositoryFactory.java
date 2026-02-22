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
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.encrypt.TextEncryptor;

public interface UsersConnectionRepositoryFactory {
    public UsersConnectionRepository getUsersConnectionRepository(JdbcTemplate var1, TextEncryptor var2, RepositoryProperties var3);
}

