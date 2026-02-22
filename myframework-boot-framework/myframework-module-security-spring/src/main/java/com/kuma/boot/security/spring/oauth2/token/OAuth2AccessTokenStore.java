/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.kuma.boot.cache.redis.repository.RedisRepository
 *  org.springframework.security.core.userdetails.UserDetails
 *  org.springframework.security.oauth2.core.OAuth2AccessToken
 *  org.springframework.security.oauth2.core.OAuth2RefreshToken
 *  org.springframework.security.oauth2.core.endpoint.OAuth2AccessTokenResponse
 *  org.springframework.stereotype.Component
 */
package com.kuma.boot.security.spring.oauth2.token;

import com.kuma.boot.cache.redis.repository.RedisRepository;
import java.util.concurrent.TimeUnit;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.OAuth2RefreshToken;
import org.springframework.security.oauth2.core.endpoint.OAuth2AccessTokenResponse;
import org.springframework.stereotype.Component;

@Component
public class OAuth2AccessTokenStore {
    public static final String OAUTH2_AUTHORIZATION_ID = ":oauth2_authorization:id:";
    public static final String OAUTH2_AUTHORIZATION_TOKEN_TYPE = ":oauth2_authorization:tokenType:";
    public static final long AUTHORIZATION_TIMEOUT = 300L;
    public static final String PREFIX = "ttc-auth";
    private final RedisRepository redisRepository;

    public OAuth2AccessTokenStore(RedisRepository redisRepository) {
        this.redisRepository = redisRepository;
    }

    public void addToken(UserDetails userDetails, OAuth2AccessTokenResponse accessTokenResponse, long timeout, TimeUnit unit) {
        OAuth2RefreshToken refreshToken;
        OAuth2AccessToken accessToken = accessTokenResponse.getAccessToken();
        if (accessToken != null) {
            String tokenValue = accessToken.getTokenValue();
            this.redisRepository.set(tokenValue, (Object)userDetails);
            this.redisRepository.opsForValue().set((Object)("ttc-auth:oauth2_authorization:tokenType:access_token:" + tokenValue), (Object)tokenValue, timeout, unit);
        }
        if ((refreshToken = accessTokenResponse.getRefreshToken()) != null) {
            String tokenValue = refreshToken.getTokenValue();
            this.redisRepository.opsForValue().set((Object)("ttc-auth:oauth2_authorization:tokenType:refresh_token:" + tokenValue), (Object)tokenValue, timeout, unit);
        }
    }

    public OAuth2AccessTokenResponse freshToken(String freshToken) {
        return null;
    }

    public String findByToken(String token) {
        return "";
    }
}

